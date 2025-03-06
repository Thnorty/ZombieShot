import java.io.File;
import java.awt.Rectangle;

public class Zombie extends Entity {
    protected static final int ZOMBIE_WIDTH = 96;
    protected static final int ZOMBIE_HEIGHT = 96;

    protected int attacksPerMinute = 60;
    protected int attackRange = 80;
    protected int health;
    protected int maxHealth;
    protected double moveSpeed;
    protected double damage;
    protected boolean canAttack = true;
    protected long lastAttackTime = 0;
    protected int score = 100;

    public Zombie(int x, int y, int health, double speed, double damage, String appearanceImagePath) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.maxHealth = health;
        this.moveSpeed = speed;
        this.damage = damage;
        this.width = ZOMBIE_WIDTH;
        this.height = ZOMBIE_HEIGHT;
        this.appearanceImagePath = appearanceImagePath;
        setImage(new File(appearanceImagePath));
    }

    public boolean canAttack() {
        if (!canAttack) {
            // Calculate if enough time has passed since last attack
            long currentTime = System.currentTimeMillis();
            long fireDelay = 60000 / attacksPerMinute;
            long difference = currentTime - lastAttackTime;
            if (difference >= fireDelay) {
                canAttack = true;
            }
        }
        return canAttack;
    }

    public void attack() {
        if (canAttack) {
            canAttack = false;
            lastAttackTime = System.currentTimeMillis();
        }
    }

    public Rectangle getBounds() {
        int reducedWidth = width / 2;
        int reducedHeight = height / 2;
        int topLeftX = (int)x + (width - reducedWidth) / 2;
        int topLeftY = (int)y + (height - reducedHeight) / 2 + height / 8;
        return new Rectangle(topLeftX, topLeftY, reducedWidth, reducedHeight);
    }
}
