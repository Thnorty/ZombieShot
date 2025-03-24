import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class Animation extends Entity {
    private List<BufferedImage> frames;
    private int width, height;
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private boolean isCompleted = false;
    private boolean loop = false;
    private int frameDelay;
    private String type; // Can be used to categorize animations (e.g., "explosion", "powerup", etc.)
    
    public Animation(double x, double y, int width, int height, int frameDelay, boolean loop, String type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.frameDelay = frameDelay;
        this.loop = loop;
        this.type = type;
        this.frames = new ArrayList<>();
    }
    
    public void loadFrames(String directory) {
        frames.clear();
        File dir = new File(directory);
        
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Animation directory not found: " + directory);
            return;
        }
        
        // Get all PNG files in the directory
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
        
        if (files == null || files.length == 0) {
            System.err.println("No frames found in: " + directory);
            return;
        }
        
        java.util.Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
        
        for (File file : files) {
            try {
                BufferedImage frame = ImageIO.read(file);
                frames.add(frame);
            } catch (IOException e) {
                System.err.println("Error loading frame: " + file.getName());
            }
        }
        
        lastFrameTime = System.currentTimeMillis();
    }
    
    public void addFrame(BufferedImage frame) {
        if (frames.isEmpty()) {
            lastFrameTime = System.currentTimeMillis();
        }
        frames.add(frame);
    }
    
    public void setFrames(List<BufferedImage> frames) {
        this.frames = new ArrayList<>(frames);
        lastFrameTime = System.currentTimeMillis();
    }
    
    public boolean update() {
        if (isCompleted || frames == null || frames.isEmpty()) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameDelay) {
            currentFrame++;
            
            if (currentFrame >= frames.size()) {
                if (loop) {
                    currentFrame = 0;
                } else {
                    isCompleted = true;
                    return false;
                }
            }
            
            lastFrameTime = currentTime;
        }
        return true;
    }
    
    public void draw(Graphics2D g2d) {
        if (isCompleted || frames == null || frames.isEmpty() || currentFrame < 0 || currentFrame >= frames.size()) {
            return;
        }
        
        BufferedImage currentImage = frames.get(currentFrame);
        g2d.drawImage(currentImage, (int)x, (int)y, width, height, null);
    }
    
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public String getType() {
        return type;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    
    public void reset() {
        currentFrame = 0;
        isCompleted = false;
        lastFrameTime = System.currentTimeMillis();
    }
    
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
}