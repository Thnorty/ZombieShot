import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Player extends Entity {
    private static final int PLAYER_WIDTH = 64;
    private static final int PLAYER_HEIGHT = 64;
    public static final int PLAYER_HEALTH = 100;

    // Animation fields
    private transient BufferedImage[] walkingFrames;
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private static final int FRAME_DELAY = 100;
    private boolean isMoving = false;
    private int selectedCharacter;

    protected ArrayList<Weapon> weapons = new ArrayList<Weapon>();
    protected Weapon currentWeapon;
    protected double health;
    protected int score = 0;
    protected int kills = 0;
    public boolean facingLeft = false;

    public Player(int x, int y, int characterNumber) {
        this.x = x;
        this.y = y;
        this.width = PLAYER_WIDTH;
        this.height = PLAYER_HEIGHT;
        this.health = PLAYER_HEALTH;
        this.selectedCharacter = characterNumber;
        String charNumString = characterNumber < 10 ? "0" + characterNumber : String.valueOf(characterNumber);
        this.appearanceImagePath = "assets/Player/char_" + charNumString + "/walking/walking_01.png";
        String walkingDirPath = "assets/Player/char_" + charNumString + "/walking";
        setImage(new File(appearanceImagePath));
        weapons.add(new Pistol());
        weapons.add(new Rifle());
        weapons.add(new Shotgun());
        weapons.add(new Sniper());
        weapons.add(new RocketLauncher());
        currentWeapon = weapons.get(0);

        loadWalkingFrames(walkingDirPath);
    }
    
    private void loadWalkingFrames(String walkingDirPath) {
        try {
            File walkingDir = new File(walkingDirPath);
            if (walkingDir.exists() && walkingDir.isDirectory()) {
                File[] files = walkingDir.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(".png"));
                if (files != null && files.length > 0) {
                    walkingFrames = new BufferedImage[files.length];
                    for (int i = 0; i < files.length; i++) {
                        walkingFrames[i] = ImageIO.read(files[i]);
                    }
                } else {
                    System.err.println("No PNG files found in walking directory: " + walkingDirPath);
                }
            } else {
                System.err.println("Walking animation directory not found: " + walkingDirPath);
            }
        } catch (Exception e) {
            System.err.println("Error loading walking animation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setMoving(boolean moving) {
        this.isMoving = moving;
    }

    public void updateAnimation() {
        if (walkingFrames == null) {
            // Reload walking frames if they're null (after deserialization)
            String charNumString = (selectedCharacter < 10) ? "0" + selectedCharacter : String.valueOf(selectedCharacter);
            String walkingDirPath = "assets/Player/char_" + charNumString + "/walking";
            loadWalkingFrames(walkingDirPath);
        }
        
        if (isMoving && walkingFrames != null && walkingFrames.length > 0) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameTime > FRAME_DELAY) {
                currentFrame = (currentFrame + 1) % walkingFrames.length;
                image = walkingFrames[currentFrame];
                lastFrameTime = currentTime;
            }
        } else {
            // Reset to standing frame when not moving
            if (walkingFrames != null && walkingFrames.length > 0 && currentFrame != 0) {
                image = walkingFrames[0];
                currentFrame = 0;
            }
        }
    }

    public void reload() {
        Weapon currentWeapon = this.currentWeapon;

        if (currentWeapon instanceof RocketLauncher) {
            return;
        }

        currentWeapon.reload();
        
        if (currentWeapon.isReloading) {
            new Thread(() -> {
                try {
                    Thread.sleep(currentWeapon.reloadTimeMs);
                    
                    int ammoNeeded = currentWeapon.maxAmmoInClip - currentWeapon.currentAmmo;
                    if (currentWeapon.currentTotalAmmo >= ammoNeeded) {
                        currentWeapon.currentAmmo += ammoNeeded;
                        currentWeapon.currentTotalAmmo -= ammoNeeded;
                    } else {
                        currentWeapon.currentAmmo += currentWeapon.currentTotalAmmo;
                        currentWeapon.currentTotalAmmo = 0;
                    }
                    
                    currentWeapon.isReloading = false;
                    currentWeapon.canShoot = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void updateGunPosition() {
        if (currentWeapon != null) {
            // Position the gun relative to player facing direction
            double offsetX = 50;
            
            // Calculate gun position based on player rotation
            double angle = Math.toRadians(rotation);
            
            // If player is facing left, adjust the gun position
            if (facingLeft) {
                // Mirror the gun position when player is facing left
                angle = Math.PI - angle;
            }
            
            double gunX = getCenterX() + Math.cos(angle) * offsetX - currentWeapon.width / 2;
            double gunY = getCenterY() + Math.sin(angle) * offsetX - currentWeapon.height / 2;
            
            currentWeapon.x = gunX;
            currentWeapon.y = gunY;
            
            // Set gun rotation to match player rotation
            currentWeapon.rotation = rotation;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Reload the image and walking frames after deserialization
        loadImage();
        String charNumString = selectedCharacter < 10 ? "0" + selectedCharacter : String.valueOf(selectedCharacter);
        String walkingDirPath = "assets/Player/char_" + charNumString + "/walking";
        loadWalkingFrames(walkingDirPath);
    }
}
