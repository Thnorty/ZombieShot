import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Font;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Background implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected final int TILE_SIZE = 64;
    private final double OBSTACLE_PROBABILITY = 0.25;
    private transient List<BufferedImage> tileImages;
    private double offsetX = 0;
    private double offsetY = 0;
    private transient Random random = new Random();
    
    // Store pattern for each cell position
    private Map<String, Integer> cellPatterns;
    
    // Tile indices that represent obstacles (can't walk through)
    private Set<Integer> obstacleTileIndices;
    
    // Store image paths for reloading after deserialization
    private String[] backgroundImagePaths;
    private String[] obstacleImagePaths;
    
    // Debug visualization settings
    private boolean debugMode = false;
    private int checkedCellX;
    private int checkedCellY;
    private boolean lastMoveBlocked = false;
    
    public Background(String[] backgroundTileImages, String[] obstacleTilePaths) {
        // Store image paths for later reloading if needed
        this.backgroundImagePaths = backgroundTileImages;
        this.obstacleImagePaths = obstacleTilePaths;
        
        // Load all tile images (both background and obstacles)
        loadTileImages(backgroundTileImages, obstacleTilePaths);
        cellPatterns = new HashMap<>();
        obstacleTileIndices = new HashSet<>();

        int obstacleStartIndex = backgroundTileImages.length;
        for (int i = 0; i < obstacleTilePaths.length; i++) {
            obstacleTileIndices.add(obstacleStartIndex + i);
        }
    }
    
    public void toggleDebugMode() {
        debugMode = !debugMode;
    }
    
    private void loadTileImages(String[] backgroundPaths, String[] obstaclePaths) {
        tileImages = new ArrayList<>();
        
        // Load background tiles
        for (String path : backgroundPaths) {
            try {
                BufferedImage img = ImageIO.read(new File(path));
                tileImages.add(img);
            } catch (IOException e) {
                System.err.println("Failed to load background tile: " + path + " - " + e.getMessage());
            }
        }
        
        // Load obstacle tiles
        for (String path : obstaclePaths) {
            try {
                BufferedImage img = ImageIO.read(new File(path));
                tileImages.add(img);
            } catch (IOException e) {
                System.err.println("Failed to load obstacle tile: " + path + " - " + e.getMessage());
            }
        }
        
        // If no images were loaded successfully, create a default one
        if (tileImages.isEmpty()) {
            createDefaultTile();
        }
    }
    

    private void createDefaultTile() {
        BufferedImage defaultTile = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = defaultTile.createGraphics();
        g.setColor(java.awt.Color.DARK_GRAY);
        g.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
        g.setColor(java.awt.Color.GRAY);
        g.drawRect(0, 0, TILE_SIZE-1, TILE_SIZE-1);
        g.dispose();
        
        tileImages = new ArrayList<>();
        tileImages.add(defaultTile);
    }
    

    private int getTileForCell(int cellX, int cellY) {
        String key = cellX + "," + cellY;
        if (!cellPatterns.containsKey(key)) {
            if (random.nextDouble() < OBSTACLE_PROBABILITY) {
                cellPatterns.put(key, random.nextInt(tileImages.size()));
            } else {
                cellPatterns.put(key, 0);
            }
        }
        return cellPatterns.get(key);
    }

    public boolean isValidMove(double dx, double dy, int playerHeight) {
        // Calculate future position
        double newOffsetX = offsetX + dx;
        double newOffsetY = offsetY + dy;
                
        // Get the screen dimensions from the GamePanel class
        int screenWidth = GameFrame.WIDTH;
        int screenHeight = GameFrame.HEIGHT - StatPanel.HEIGHT;
        
        // Calculate center of screen (where player is)
        int screenCenterX = screenWidth / 2;
        int screenCenterY = screenHeight / 2 + playerHeight / 4;
        
        // Calculate the world coordinates of the player's center after movement
        double playerWorldX = screenCenterX + newOffsetX;
        double playerWorldY = screenCenterY + newOffsetY;
        
        // Convert to cell coordinates
        int centerCellX = (int)Math.floor(playerWorldX / TILE_SIZE);
        int centerCellY = (int)Math.floor(playerWorldY / TILE_SIZE);
        
        // Store for debug visualization
        checkedCellX = centerCellX;
        checkedCellY = centerCellY;
        
        // Check if this cell contains an obstacle
        String key = centerCellX + "," + centerCellY;
        if (cellPatterns.containsKey(key)) {
            int tileIndex = cellPatterns.get(key);
            boolean isObstacle = obstacleTileIndices.contains(tileIndex);
            lastMoveBlocked = isObstacle;
            return !isObstacle;
        }
        
        // If cell doesn't exist yet, it's a new cell - allow movement
        lastMoveBlocked = false;
        return true;
    }
    
    public boolean isValidMoveForEntity(double entityX, double entityY, double dx, double dy, int entityWidth, int entityHeight) {
        // Calculate future position
        double newX = entityX + dx;
        double newY = entityY + dy;
        
        // Calculate the world coordinates of the entity's center after movement
        double entityWorldCenterX = newX + entityWidth/2 + offsetX;
        double entityWorldCenterY = newY + entityHeight/2 + offsetY + 32;
        
        // Convert to cell coordinates
        int centerCellX = (int)Math.floor(entityWorldCenterX / TILE_SIZE);
        int centerCellY = (int)Math.floor(entityWorldCenterY / TILE_SIZE);
        
        // Check if this cell contains an obstacle
        String key = centerCellX + "," + centerCellY;
        if (cellPatterns.containsKey(key)) {
            int tileIndex = cellPatterns.get(key);
            boolean isObstacle = obstacleTileIndices.contains(tileIndex);
            return !isObstacle;
        }
        
        // If cell doesn't exist yet, it's a new cell - allow movement
        return true;
    }

    public boolean isValidSpawnPosition(double x, double y, int width, int height) {
        // Calculate the world coordinates of the entity's center
        double entityWorldCenterX = x + width/2 + offsetX;
        double entityWorldCenterY = y + height/2 + offsetY + 32; // Same offset as in isValidMoveForEntity
        
        // Convert to cell coordinates
        int centerCellX = (int)Math.floor(entityWorldCenterX / TILE_SIZE);
        int centerCellY = (int)Math.floor(entityWorldCenterY / TILE_SIZE);
        
        // Check if this cell contains an obstacle
        String key = centerCellX + "," + centerCellY;
        if (cellPatterns.containsKey(key)) {
            int tileIndex = cellPatterns.get(key);
            boolean isObstacle = obstacleTileIndices.contains(tileIndex);
            return !isObstacle; // Return true if NOT an obstacle
        }
        
        // If cell doesn't exist yet, it's a new cell - allow spawning
        return true;
    }

    public boolean update(double dx, double dy, int playerHeight) {
        if (!isValidMove(dx, dy, playerHeight)) {
            return false;
        }
        
        offsetX += dx;
        offsetY += dy;
        boolean limitMapSize = false;

        if (!limitMapSize) {
            return true;
        }

        // Limit the map size by removing cells that are too far away
        if (Math.abs(offsetX) > TILE_SIZE * 50 || Math.abs(offsetY) > TILE_SIZE * 50) {
            // Reset offset and map when too far from origin
            offsetX = offsetX % TILE_SIZE;
            offsetY = offsetY % TILE_SIZE;
            cellPatterns.clear();
        }
        
        return true;
    }
    
    public void draw(Graphics2D g2d, int width, int height, Player player) {
        if (tileImages.isEmpty()) return;
        
        // Calculate visible range
        int startCellX = (int)Math.floor(offsetX / TILE_SIZE) - 1;
        int startCellY = (int)Math.floor(offsetY / TILE_SIZE) - 1;
        int endCellX = startCellX + (width / TILE_SIZE) + 2;
        int endCellY = startCellY + (height / TILE_SIZE) + 2;
        
        // Draw visible tiles
        for (int cellY = startCellY; cellY <= endCellY; cellY++) {
            for (int cellX = startCellX; cellX <= endCellX; cellX++) {
                int tileIndex = getTileForCell(cellX, cellY);
                BufferedImage tile = tileImages.get(tileIndex);
                
                int drawX = (int)((cellX * TILE_SIZE) - offsetX);
                int drawY = (int)((cellY * TILE_SIZE) - offsetY);
                
                g2d.drawImage(tile, drawX, drawY, TILE_SIZE, TILE_SIZE, null);
                
                // Draw debug information if enabled
                if (debugMode) {
                    // Draw grid
                    g2d.setColor(new Color(255, 255, 255, 80)); // Semi-transparent white
                    g2d.drawRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                    
                    // Mark obstacles
                    if (obstacleTileIndices.contains(tileIndex)) {
                        g2d.setColor(new Color(255, 0, 0, 100)); // Semi-transparent red
                        g2d.fillRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                    }
                    
                    // Draw cell coordinates
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                    g2d.drawString(cellX + "," + cellY, drawX + 5, drawY + 15);
                }
            }
        }
        
        // Draw debug visualization for collision checks
        if (debugMode) {
            // Draw a marker at the center of the screen (player position)
            int screenCenterX = width / 2;
            int screenCenterY = height / 2;
            
            g2d.setColor(Color.GREEN);
            g2d.fillOval(screenCenterX - 5, screenCenterY - 5, 10, 10);
            
            // Draw the cell being checked for collision
            int cellDrawX = (int)((checkedCellX * TILE_SIZE) - offsetX);
            int cellDrawY = (int)((checkedCellY * TILE_SIZE) - offsetY);
            
            g2d.setStroke(new BasicStroke(2));
            if (lastMoveBlocked) {
                g2d.setColor(new Color(255, 0, 0, 180)); // Semi-transparent red for collision
            } else {
                g2d.setColor(new Color(0, 255, 0, 180)); // Semi-transparent green for no collision
            }
            g2d.drawRect(cellDrawX, cellDrawY, TILE_SIZE, TILE_SIZE);
            
            // Draw offset and other debug info
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("Offset: " + String.format("%.1f, %.1f", offsetX, offsetY), 10, 20);
            g2d.drawString("Player Cell: " + checkedCellX + "," + checkedCellY, 10, 40);
            g2d.drawString("Player Screen Position: " + String.format("%.1f, %.1f", player.x, player.y), 10, 60);
            g2d.drawString("Player World Position: " + String.format("%.1f, %.1f", player.x - offsetX, player.y - offsetY), 10, 80);
            
            // Draw player hitbox
            g2d.setStroke(new BasicStroke(1));
            g2d.setColor(new Color(0, 255, 255, 150)); // Cyan for player hitbox
            g2d.drawRect((int)player.x, (int)player.y, player.width, player.height);
        }
    }
    
    public double getOffsetX() {
        return offsetX;
    }
    
    public double getOffsetY() {
        return offsetY;
    }

    public int getCellXFromScreenPos(int screenX) {
        return (int)Math.floor((screenX + offsetX) / TILE_SIZE);
    }
    
    public int getCellYFromScreenPos(int screenY) {
        return (int)Math.floor((screenY + offsetY) / TILE_SIZE);
    }
    
    public void setOffset(double x, double y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        // Reinitialize transient fields
        random = new Random();
        
        // Reload tile images
        loadTileImages(backgroundImagePaths, obstacleImagePaths);
    }
}