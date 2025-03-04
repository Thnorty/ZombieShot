import java.io.File;
import java.util.ArrayList;

public class Bullet extends Entity {
    public int bulletSize = 100;
    protected ArrayList<Zombie> hitZombies = new ArrayList<Zombie>();
    protected boolean isZombieBullet = false;
    protected double damage = 0;
    private Weapon sourceWeapon;

    public Bullet(double x, double y, Weapon sourceWeapon) {
        super(x, y);

        if (sourceWeapon instanceof Pistol) {
            this.appearanceImagePath = "assets/Laser Sprites/01.png";
        } else if (sourceWeapon instanceof Rifle) {
            this.appearanceImagePath = "assets/Laser Sprites/12.png";
        } else if (sourceWeapon instanceof Shotgun) {
            this.appearanceImagePath = "assets/Laser Sprites/33.png";
        } else if (sourceWeapon instanceof Sniper) {
            this.appearanceImagePath = "assets/Laser Sprites/65.png";
        } else if (sourceWeapon instanceof RocketLauncher) {
            this.bulletSize = 200;
            this.appearanceImagePath = "assets/Laser Sprites/55.png";
        } else {
            this.appearanceImagePath = "assets/Laser Sprites/02.png";
        }

        this.x = x - bulletSize/2;
        this.y = y - bulletSize/2;
        this.width = bulletSize;
        this.height = bulletSize;
        this.sourceWeapon = sourceWeapon;

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
}
