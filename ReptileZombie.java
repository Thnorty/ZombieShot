public class ReptileZombie extends Zombie {
    private static final long JUMP_COOLDOWN = 3000;
    private static final double JUMP_RANGE = 300;
    protected static final double JUMP_SPEED = 10;
    protected static final double JUMP_DISTANCE = 400;

    protected boolean isJumping = false;
    protected double jumpDirectionX = 0;
    protected double jumpDirectionY = 0;
    protected double jumpDistanceTraveled = 0;
    private long lastJumpTime = 0;

    public ReptileZombie(int x, int y) {
        super(x, y, 50, 2, 10, "assets/Zombies/reptile_zombie.png", 75);
        lastJumpTime = System.currentTimeMillis();
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
        
        jumpDirectionX = dx;
        jumpDirectionY = dy;
        
        isJumping = true;
        jumpDistanceTraveled = 0;
        lastJumpTime = System.currentTimeMillis();
    }

    // Check if enough time has passed to jump again
    public boolean canJump() {
        return !isJumping && System.currentTimeMillis() - lastJumpTime >= JUMP_COOLDOWN;
    }

    public void updateJump() {
        double moveAmount = moveSpeed * JUMP_SPEED;
        
        x += jumpDirectionX * moveAmount;
        y += jumpDirectionY * moveAmount;
        
        jumpDistanceTraveled += moveAmount;
        
        if (jumpDistanceTraveled >= JUMP_RANGE) {
            isJumping = false;
        }
    }
}
