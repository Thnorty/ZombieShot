import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.Rectangle;

public class Entity implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String appearanceImagePath = "assets/default.png";
    protected double x = 0;
    protected double y = 0;
    protected double directionX = 0;
    protected double directionY = 0;
    protected int width = 32;
    protected int height = 32;
    protected transient BufferedImage image;
    protected double rotation = 0;
    protected boolean isFlashing = false;
    protected long flashStartTime = 0;
    protected double moveSpeed;
    protected static final long FLASH_DURATION = 150;

    protected static List<Entity> entities = new ArrayList<>();

    public Entity() {
        entities.add(this);
    }

    public Entity(double x, double y) {
        this.x = x;
        this.y = y;
        entities.add(this);
    }

    public void calculatePreservedRatio() {
        if (image != null) {
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            
            double ratio = (double)originalHeight / originalWidth;
            
            height = (int)(width * ratio);
        }
    }

    public void setImage(File imageFile) {
        try {
            BufferedImage originalImage = ImageIO.read(imageFile);
            this.image = originalImage;
            calculatePreservedRatio();
        } catch (Exception e) {
            System.out.println("Failed to load image: " + image);
            e.printStackTrace();
        }
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        calculatePreservedRatio();
    }

    public void setTargetWidth(int width) {
        this.width = width;
        calculatePreservedRatio();
    }
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
    public double getCenterX() {
        return x + width / 2;
    }
    public double getCenterY() {
        return y + height / 2;
    }

    public void loadImage() {
        setImage(new File(appearanceImagePath));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Reload the image
        loadImage();
        if (!entities.contains(this)) {
            entities.add(this);
        }
    }
    
    // Check if entity should be drawn with flash effect
    public boolean isFlashing() {
        if (!isFlashing) return false;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - flashStartTime > FLASH_DURATION) {
            isFlashing = false;
            return false;
        }
        return true;
    }
    
    // Trigger flash effect
    public void startFlashEffect() {
        isFlashing = true;
        flashStartTime = System.currentTimeMillis();
    }
}