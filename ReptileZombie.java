public class ReptileZombie extends Zombie {
    private static final long JUMP_COOLDOWN = 5000;
    private static final double JUMP_AWAY_DISTANCE = 200;
    protected static final double JUMP_SPEED = 10;
    protected static final double JUMP_DISTANCE = 300;

    protected boolean isJumping = false;
    protected double jumpDirectionX = 0;
    protected double jumpDirectionY = 0;
    protected double jumpDistanceTraveled = 0;
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
        
        if (jumpDistanceTraveled >= JUMP_AWAY_DISTANCE) {
            isJumping = false;
        }
    }
}
