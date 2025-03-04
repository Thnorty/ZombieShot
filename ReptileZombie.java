public class ReptileZombie extends Zombie {
    private static final long JUMP_COOLDOWN = 3000;
    protected static final double JUMP_SPEED = 100.0;

    protected boolean willJump = false;
    protected double jumpDirectionX = 0;
    protected double jumpDirectionY = 0;
    private long lastJumpTime = 0;

    public ReptileZombie(int x, int y) {
        super(x, y, 50, 2, 10, "assets/Zombies/reptile_zombie.png");
    }

    // Add a method to set random jump direction
    public void prepareJump(double playerX, double playerY) {
        // Calculate vector from zombie to player
        double dx = playerX - this.getCenterX();
        double dy = playerY - this.getCenterY();
        
        // Normalize the vector
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx /= length;
            dy /= length;
        }
        
        if (Math.random() < 0.5) {
            // Left side
            jumpDirectionX = -dy;
            jumpDirectionY = dx;
        } else {
            // Right side
            jumpDirectionX = dy;
            jumpDirectionY = -dx;
        }
        
        willJump = true;
        lastJumpTime = System.currentTimeMillis();
    }

    // Check if enough time has passed to jump again
    public boolean canJump() {
        return System.currentTimeMillis() - lastJumpTime >= JUMP_COOLDOWN;
    }
}
