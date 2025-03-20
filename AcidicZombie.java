import java.io.File;

public class AcidicZombie extends Zombie {
    public static final int BLAST_RADIUS = 300;
    protected String firingSoundPath = "assets/WeaponSounds/Firing/spitting.wav";

    public AcidicZombie(int x, int y) {
        super(x, y, 50, 0.5, 10, "assets/Zombies/acidic_zombie.png", 150);
        attackRange = 500;
        attackDistancePercent = 0.75;
    }
    
    public Bullet shootAcid(double playerX, double playerY) {
        Bullet acidBullet = new Bullet(this.getCenterX(), this.getCenterY(), null);
        
        acidBullet.appearanceImagePath = "assets/Laser Sprites/09.png";
        acidBullet.setImage(new File(acidBullet.appearanceImagePath));
        acidBullet.bulletSize = 50;
        
        double dx = playerX - this.getCenterX();
        double dy = playerY - this.getCenterY();
        
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx /= length;
            dy /= length;
        }
        
        acidBullet.directionX = dx;
        acidBullet.directionY = dy;
        acidBullet.rotation = Math.toDegrees(Math.atan2(dy, dx));
        
        // Prevent self-damage
        acidBullet.hitZombies.add(this);
        
        attack();
        acidBullet.setZombieBullet(true);
        acidBullet.setDamage(damage);
        acidBullet.moveSpeed = acidBullet.moveSpeed * 0.5;
        playFiringSound();

        return acidBullet;
    }
    
    public void playFiringSound() {
        GameInfo.playSound(firingSoundPath);
    }
}
