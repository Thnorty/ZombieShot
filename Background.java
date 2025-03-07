import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Background {
    private List<BufferedImage> tileImages;
    private final int TILE_SIZE = 64;
    private double offsetX = 0;
    private double offsetY = 0;
    private Random random = new Random();
    
    // Store pattern for each cell position
    private Map<String, Integer> cellPatterns;
    
    /**
     * Creates a background with multiple tile types
     * @param tileImagePaths Array of paths to the tile images
     */
    public Background(String... tileImagePaths) {
        loadTileImages(tileImagePaths);
        cellPatterns = new HashMap<>();
    }
    
    /**
     * Loads all tile images from the specified paths
     * @param imagePaths Array of paths to the tile images
     */
    private void loadTileImages(String[] imagePaths) {
        tileImages = new ArrayList<>();
        
        for (String path : imagePaths) {
            try {
                BufferedImage img = ImageIO.read(new File(path));
                tileImages.add(img);
            } catch (IOException e) {
                System.err.println("Failed to load background tile: " + path + " - " + e.getMessage());
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
     * If this is a new cell, assign a random tile
     */
    private int getTileForCell(int cellX, int cellY) {
        String key = cellX + "," + cellY;
        if (!cellPatterns.containsKey(key)) {
            cellPatterns.put(key, random.nextInt(tileImages.size()));
        }
        return cellPatterns.get(key);
    }
    
    /**
     * Updates the background position based on player movement
     * @param dx Change in x position
     * @param dy Change in y position
     */
    public void update(double dx, double dy) {
        offsetX += dx;
        offsetY += dy;
        
        // Limit the map size by removing cells that are too far away
        if (Math.abs(offsetX) > TILE_SIZE * 50 || Math.abs(offsetY) > TILE_SIZE * 50) {
            // Reset offset and map when too far from origin
            offsetX = offsetX % TILE_SIZE;
            offsetY = offsetY % TILE_SIZE;
            cellPatterns.clear();
        }
    }
    
    /**
     * Draws individual tiles with different patterns
     * @param g2d Graphics context to draw on
     * @param width Width of the panel
     * @param height Height of the panel
     */
    public void draw(Graphics2D g2d, int width, int height) {
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
            }
        }
    }
}