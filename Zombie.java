import java.io.File;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Zombie extends Entity {
    protected static final int ZOMBIE_WIDTH = 96;
    protected static final int ZOMBIE_HEIGHT = 96;

    protected int attacksPerMinute = 60;
    protected int attackRange = 80;
    protected int health;
    protected int maxHealth;
    protected double damage;
    protected boolean canAttack = true;
    protected long lastAttackTime = 0;
    protected int score = 100;
    protected double attackDistancePercent = 0.5;

    public Zombie(int x, int y, int health, double speed, double damage, String appearanceImagePath, int score) {
        this(x, y, health, speed, damage, appearanceImagePath);
        this.score = score;
        lastAttackTime = System.currentTimeMillis();
    }

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

    public boolean canMoveCloser(double distance) {
        return distance > attackRange * attackDistancePercent;
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

    public static class Pool {
        private static final int INITIAL_POOL_SIZE = 100;
        
        private static List<NormalZombie> availableNormalZombies = new ArrayList<>();
        private static List<ReptileZombie> availableReptileZombies = new ArrayList<>();
        private static List<TankZombie> availableTankZombies = new ArrayList<>();
        private static List<AcidicZombie> availableAcidicZombies = new ArrayList<>();

        static {
            // Pre-populate the pools with zombies
            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                availableNormalZombies.add(new NormalZombie(0, 0));
                availableReptileZombies.add(new ReptileZombie(0, 0));
                availableTankZombies.add(new TankZombie(0, 0));
                availableAcidicZombies.add(new AcidicZombie(0, 0));
            }
        }
        
        public static NormalZombie getNormalZombie(int x, int y) {
            NormalZombie zombie;
            
            if (availableNormalZombies.isEmpty()) {
                zombie = new NormalZombie(x, y);
            } else {
                zombie = availableNormalZombies.remove(availableNormalZombies.size() - 1);
                zombie.reset(x, y);
            }
            
            return zombie;
        }
        
        public static ReptileZombie getReptileZombie(int x, int y) {
            ReptileZombie zombie;
            
            if (availableReptileZombies.isEmpty()) {
                zombie = new ReptileZombie(x, y);
            } else {
                zombie = availableReptileZombies.remove(availableReptileZombies.size() - 1);
                zombie.reset(x, y);
            }
            
            return zombie;
        }
        
        public static TankZombie getTankZombie(int x, int y) {
            TankZombie zombie;
            
            if (availableTankZombies.isEmpty()) {
                zombie = new TankZombie(x, y);
            } else {
                zombie = availableTankZombies.remove(availableTankZombies.size() - 1);
                zombie.reset(x, y);
            }
            
            return zombie;
        }
        
        public static AcidicZombie getAcidicZombie(int x, int y) {
            AcidicZombie zombie;
            
            if (availableAcidicZombies.isEmpty()) {
                zombie = new AcidicZombie(x, y);
            } else {
                zombie = availableAcidicZombies.remove(availableAcidicZombies.size() - 1);
                zombie.reset(x, y);
            }
            
            return zombie;
        }
        
        public static void returnZombie(Zombie zombie) {
            if (zombie instanceof NormalZombie) {
                availableNormalZombies.add((NormalZombie) zombie);
            } else if (zombie instanceof ReptileZombie) {
                availableReptileZombies.add((ReptileZombie) zombie);
            } else if (zombie instanceof TankZombie) {
                availableTankZombies.add((TankZombie) zombie);
            } else if (zombie instanceof AcidicZombie) {
                availableAcidicZombies.add((AcidicZombie) zombie);
            }
        }
        
        public static int getNormalZombiePoolSize() {
            return availableNormalZombies.size();
        }
        
        public static int getReptileZombiePoolSize() {
            return availableReptileZombies.size();
        }
        
        public static int getTankZombiePoolSize() {
            return availableTankZombies.size();
        }
        
        public static int getAcidicZombiePoolSize() {
            return availableAcidicZombies.size();
        }
    }

    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        this.health = this.maxHealth;
        this.canAttack = true;
        this.lastAttackTime = System.currentTimeMillis();
        setImage(new File(appearanceImagePath));
    }
}
