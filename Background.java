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

public class Background {
    private List<BufferedImage> tileImages;
    private final int TILE_SIZE = 64;
    private double offsetX = 0;
    private double offsetY = 0;
    private Random random = new Random();
    
    // Store pattern for each cell position
    private Map<String, Integer> cellPatterns;
    
    // Tile indices that represent obstacles (can't walk through)
    private Set<Integer> obstacleTileIndices;
    
    // Debug visualization settings
    private boolean debugMode = false;
    private int checkedCellX;
    private int checkedCellY;
    private boolean lastMoveBlocked = false;
    
    /**
     * Creates a background with multiple tile types and specified obstacles
     * @param backgroundTileImages Array of paths to the background tile images
     * @param obstacleTilePaths Array of paths to the obstacle tile images
     */
    public Background(String[] backgroundTileImages, String[] obstacleTilePaths) {
        // Load all tile images (both background and obstacles)
        loadTileImages(backgroundTileImages, obstacleTilePaths);
        cellPatterns = new HashMap<>();
        obstacleTileIndices = new HashSet<>();
        
        // Mark obstacle tiles in the set
        // Obstacle tiles are loaded after background tiles, so their indices start 
        // after all background tiles
        int obstacleStartIndex = backgroundTileImages.length;
        for (int i = 0; i < obstacleTilePaths.length; i++) {
            obstacleTileIndices.add(obstacleStartIndex + i);
        }
    }
    
    /**
     * Toggle debug visualization mode
     */
    public void toggleDebugMode() {
        debugMode = !debugMode;
    }
    
    /**
     * Loads all tile images from the specified paths
     * @param backgroundPaths Array of paths to the background tile images
     * @param obstaclePaths Array of paths to the obstacle tile images
     */
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
    
    /**
     * Creates a default tile if no images load successfully
     */
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
    
    /**
     * Get a tile index for a specific cell position
     * If this is a new cell, assign a random tile with
     * the first tile having a higher chance
     */
    private int getTileForCell(int cellX, int cellY) {
        String key = cellX + "," + cellY;
        if (!cellPatterns.containsKey(key)) {
            if (random.nextDouble() < 0.7) {
                cellPatterns.put(key, 0);
            } else {
                cellPatterns.put(key, random.nextInt(tileImages.size()));
            }
        }
        return cellPatterns.get(key);
    }
    /**
     * Checks if a movement is valid (not into an obstacle)
     * @param dx Change in x position
     * @param dy Change in y position
     * @return true if movement is allowed, false if blocked by obstacle
     */
    public boolean isValidMove(double dx, double dy, int playerHeight) {
        // Calculate future position
        double newOffsetX = offsetX + dx;
        double newOffsetY = offsetY + dy;
        
        // The player is at the center of the screen
        // Since offset coordinates represent the top-left corner of the world relative to the screen,
        // we need to adjust by half the screen width/height to get the player's world position
        // We want the cell that the player's center would be in after the movement
        
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
    
    /**
     * Updates the background position based on player movement
     * @param dx Change in x position
     * @param dy Change in y position
     * @return true if the update was successful, false if blocked
     */
    public boolean update(double dx, double dy, int playerHeight) {
        if (!isValidMove(dx, dy, playerHeight)) {
            return false;
        }
        
        offsetX += dx;
        offsetY += dy;
        
        // Limit the map size by removing cells that are too far away
        if (Math.abs(offsetX) > TILE_SIZE * 50 || Math.abs(offsetY) > TILE_SIZE * 50) {
            // Reset offset and map when too far from origin
            offsetX = offsetX % TILE_SIZE;
            offsetY = offsetY % TILE_SIZE;
            cellPatterns.clear();
        }
        
        return true;
    }
    
    /**
     * Draws individual tiles with different patterns
     * @param g2d Graphics context to draw on
     * @param width Width of the panel
     * @param height Height of the panel
     * @param playerX Optional player X position (screen coordinates)
     * @param playerY Optional player Y position (screen coordinates)
     */
    public void draw(Graphics2D g2d, int width, int height, double playerX, double playerY) {
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
            g2d.drawString("Player Screen Position: " + String.format("%.1f, %.1f", playerX, playerY), 10, 60);
            g2d.drawString("Player World Position: " + 
                String.format("%.1f, %.1f", playerX - offsetX, playerY - offsetY), 10, 80);
            g2d.drawString("Last Move Blocked: " + lastMoveBlocked, 10, 100);
            
            // Draw player hitbox
            g2d.setStroke(new BasicStroke(1));
            g2d.setColor(new Color(0, 255, 255, 150)); // Cyan for player hitbox
            g2d.drawRect((int)playerX - 16, (int)playerY - 16, 32, 32); // Assuming 32x32 player size
        }
    }
    
    /**
     * Get current offset X position
     */
    public double getOffsetX() {
        return offsetX;
    }
    
    /**
     * Get current offset Y position
     */
    public double getOffsetY() {
        return offsetY;
    }
    
    /**
     * Get cell X coordinate from screen X position
     */
    public int getCellXFromScreenPos(int screenX) {
        return (int)Math.floor((screenX + offsetX) / TILE_SIZE);
    }
    
    /**
     * Get cell Y coordinate from screen Y position
     */
    public int getCellYFromScreenPos(int screenY) {
        return (int)Math.floor((screenY + offsetY) / TILE_SIZE);
    }
}