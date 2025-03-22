import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;

public class Bullet extends Entity {
    public int bulletSize = 100;
    protected ArrayList<Zombie> hitZombies = new ArrayList<Zombie>();
    protected boolean isZombieBullet = false;
    protected double damage = 0;
    protected Weapon sourceWeapon;

    public Bullet(double x, double y, Weapon sourceWeapon) {
        super(x, y);
        
        reset(x, y, sourceWeapon);
    }

    public static class Pool {
        private static final int INITIAL_POOL_SIZE = 100;
        
        private static List<Bullet> availableBullets;

        static {
            availableBullets = new ArrayList<>();
            
            // Pre-populate the pool with bullets
            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                availableBullets.add(new Bullet(0, 0, null));
            }
        }
        
        public static Bullet getBullet(double x, double y, Weapon sourceWeapon) {
            Bullet bullet;
            
            if (availableBullets.isEmpty()) {
                // Create a new bullet if the pool is empty
                bullet = new Bullet(x, y, sourceWeapon);
            } else {
                // Get a bullet from the pool
                bullet = availableBullets.remove(availableBullets.size() - 1);
                bullet.reset(x, y, sourceWeapon);
            }
            
            return bullet;
        }
        
        public static void returnBullet(Bullet bullet) {
            bullet.hitZombies.clear();
            availableBullets.add(bullet);
        }
        
        public static int getPoolSize() {
            return availableBullets.size();
        }
    }
    
    public void reset(double x, double y, Weapon sourceWeapon) {
        this.sourceWeapon = sourceWeapon;
        this.isZombieBullet = false;
        this.damage = 0;
        this.hitZombies.clear();
        
        // Update appearance based on weapon type
        if (sourceWeapon instanceof Pistol) {
            this.appearanceImagePath = "assets/Laser Sprites/01.png";
            this.bulletSize = 100;
        } else if (sourceWeapon instanceof Rifle) {
            this.appearanceImagePath = "assets/Laser Sprites/12.png";
            this.bulletSize = 100;
        } else if (sourceWeapon instanceof Shotgun) {
            this.appearanceImagePath = "assets/Laser Sprites/33.png";
            this.bulletSize = 100;
        } else if (sourceWeapon instanceof Sniper) {
            this.appearanceImagePath = "assets/Laser Sprites/65.png";
            this.bulletSize = 100;
        } else if (sourceWeapon instanceof RocketLauncher) {
            this.appearanceImagePath = "assets/Laser Sprites/55.png";
            this.bulletSize = 200;
        } else {
            this.appearanceImagePath = "assets/Laser Sprites/02.png";
            this.bulletSize = 100;
        }
        
        this.x = x - bulletSize/2;
        this.y = y - bulletSize/2;
        this.width = bulletSize;
        this.height = bulletSize;
        this.moveSpeed = GameInfo.BULLET_SPEED;
        
        setImage(new File(appearanceImagePath));
    }

    public double getDamage() {
        if (isZombieBullet) {
            return damage;
        }
        return sourceWeapon.damage;
    }
    
    public void setDamage(double damage) {
        this.damage = damage;
    }
    
    public Weapon getSourceWeapon() {
        return sourceWeapon;
    }
    
    public void setZombieBullet(boolean isZombieBullet) {
        this.isZombieBullet = isZombieBullet;
    }
    
    public boolean isZombieBullet() {
        return isZombieBullet;
    }

    public Rectangle getBounds() {
        int reducedWidth = width / 2;
        int reducedHeight = height / 2;
        int centerX = (int)x + (width - reducedWidth) / 2;
        int centerY = (int)y + (height - reducedHeight) / 2;
        return new Rectangle(centerX, centerY, reducedWidth, reducedHeight);
    }
}
