import java.io.File;

public class AcidicZombie extends Zombie {
    public static final int BLAST_RADIUS = 300;

    public AcidicZombie(int x, int y) {
        super(x, y, 50, 0.5, 10, "assets/Zombies/acidic_zombie.png", 150);
        attackRange = 400;
    }
    
    public Bullet shootAcid(double playerX, double playerY) {
        // Create the acid bullet at the zombie's position
        Bullet acidBullet = new Bullet(this.getCenterX(), this.getCenterY(), null);
        
        // Set custom acid appearance
        acidBullet.appearanceImagePath = "assets/Laser Sprites/09.png";
        acidBullet.setImage(new File(acidBullet.appearanceImagePath));
        acidBullet.bulletSize = 50;
        
        // Calculate direction towards player
        double dx = playerX - this.getCenterX();
        double dy = playerY - this.getCenterY();
        
        // Normalize direction
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx /= length;
            dy /= length;
        }
        
        // Set bullet properties
        acidBullet.directionX = dx;
        acidBullet.directionY = dy;
        acidBullet.rotation = Math.toDegrees(Math.atan2(dy, dx));
        
        // Set custom properties for AcidicZombie bullet
        acidBullet.hitZombies.add(this); // Prevent self-damage
        
        // Mark that we've attacked
        attack();
        
        return acidBullet;
    }
}
