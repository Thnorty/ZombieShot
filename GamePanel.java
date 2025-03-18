import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.RenderingHints;

public class GamePanel extends JPanel implements ActionListener {
    private final int PANEL_WIDTH = GameFrame.WIDTH;
    private final int PANEL_HEIGHT = GameFrame.HEIGHT - StatPanel.HEIGHT;
    
    // Performance optimization variables
    private int targetFPS = 120;
    private boolean showFPS = false;
    private long lastFPSCheck = 0;
    private int currentFPS = 0;
    private int frameCount = 0;
    private boolean useViewportCulling = true;
    private int cullingMargin = 100;
    private boolean useImageCaching = true;
    
    // Pre-compute flash images for performance
    private ColorConvertOp flashEffect = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
    private BufferedImage playerFlashImage = null;
    private BufferedImage[] zombieFlashImages = new BufferedImage[4]; // One for each zombie type

    private GameInfo gameInfo;
    private int mouseX = PANEL_WIDTH / 2;
    private int mouseY = PANEL_HEIGHT / 2;
    private Random random = new Random();

    protected Background background;
    protected boolean moveUp = false;
    protected boolean moveDown = false;
    protected boolean moveLeft = false;
    protected boolean moveRight = false;
    protected boolean leftMousePressed = false;

    public GamePanel(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.GRAY);
        setFocusable(true);

        background = new Background(
            new String[]{
                "assets/Background/tile_0000.png",
                "assets/Background/tile_0001.png",
                "assets/Background/tile_0002.png",
                "assets/Background/tile_0043.png"
            }, new String[]{
                "assets/Background/tile_0027.png",
                "assets/Background/tile_0028.png",
            }
        );

        // Mouse motion listener for player rotation
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent evt) {
                updateMousePosition(evt);
            }
            
            public void mouseDragged(MouseEvent evt) {
                updateMousePosition(evt);
            }
            
            private void updateMousePosition(MouseEvent evt) {
                if (gameInfo.isPaused) {
                    return;
                }
                mouseX = evt.getX();
                mouseY = evt.getY();
                
                // Calculate angle for gun rotation
                double angle = Math.atan2(mouseY - gameInfo.player.y - gameInfo.player.height/2, 
                                mouseX - gameInfo.player.x - gameInfo.player.width/2);
                gameInfo.player.rotation = Math.toDegrees(angle);
                
                // Track facing direction for player flipping
                gameInfo.player.facingLeft = (mouseX < gameInfo.player.getCenterX());
                
                repaint();
            }
        });

        // Mouse click listener for shooting
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    leftMousePressed = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    leftMousePressed = false;
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                int keyCode = evt.getKeyCode();
                
                if (keyCode == gameInfo.getKeyBinding("moveUp")) {
                    moveUp = true;
                } else if (keyCode == gameInfo.getKeyBinding("moveDown")) {
                    moveDown = true;
                } else if (keyCode == gameInfo.getKeyBinding("moveLeft")) {
                    moveLeft = true;
                } else if (keyCode == gameInfo.getKeyBinding("moveRight")) {
                    moveRight = true;
                } else if (keyCode == gameInfo.getKeyBinding("reload")) {
                    gameInfo.player.reload();
                    if (gameInfo.statPanel != null)
                        gameInfo.statPanel.update();
                } else if (keyCode == gameInfo.getKeyBinding("weapon1")) {
                    if (gameInfo.player.weapons.size() >= 1) {
                        gameInfo.player.currentWeapon = gameInfo.player.weapons.get(0);
                        if (gameInfo.statPanel != null)
                            gameInfo.statPanel.update();
                    }
                } else if (keyCode == gameInfo.getKeyBinding("weapon2")) {
                    if (gameInfo.player.weapons.size() >= 2) {
                        gameInfo.player.currentWeapon = gameInfo.player.weapons.get(1);
                        if (gameInfo.statPanel != null)
                            gameInfo.statPanel.update();
                    }
                } else if (keyCode == gameInfo.getKeyBinding("weapon3")) {
                    if (gameInfo.player.weapons.size() >= 3) {
                        gameInfo.player.currentWeapon = gameInfo.player.weapons.get(2);
                        if (gameInfo.statPanel != null)
                            gameInfo.statPanel.update();
                    }
                } else if (keyCode == gameInfo.getKeyBinding("weapon4")) {
                    if (gameInfo.player.weapons.size() >= 4) {
                        gameInfo.player.currentWeapon = gameInfo.player.weapons.get(3);
                        if (gameInfo.statPanel != null)
                            gameInfo.statPanel.update();
                    }
                } else if (keyCode == gameInfo.getKeyBinding("weapon5")) {
                    if (gameInfo.player.weapons.size() >= 5) {
                        gameInfo.player.currentWeapon = gameInfo.player.weapons.get(4);
                        if (gameInfo.statPanel != null)
                            gameInfo.statPanel.update();
                    }
                } else if (keyCode == gameInfo.getKeyBinding("pause")) {
                    togglePause();
                } else if (keyCode == gameInfo.getKeyBinding("debug")) {
                    background.toggleDebugMode();
                } else if (keyCode == KeyEvent.VK_F1) {
                    toggleFPSDisplay();
                    System.out.println("FPS Display: " + (showFPS ? "Enabled" : "Disabled"));
                }
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                int keyCode = evt.getKeyCode();
                
                if (keyCode == gameInfo.getKeyBinding("moveUp")) {
                    moveUp = false;
                } else if (keyCode == gameInfo.getKeyBinding("moveDown")) {
                    moveDown = false;
                } else if (keyCode == gameInfo.getKeyBinding("moveLeft")) {
                    moveLeft = false;
                } else if (keyCode == gameInfo.getKeyBinding("moveRight")) {
                    moveRight = false;
                }
            }
        });

        centerplayer();
        gameInfo.gameTimer = new Timer(16, this);
        
        // Zombie spawn timer
        gameInfo.zombieSpawnTimer = new Timer(gameInfo.currentZombieSpawnRate, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spawnRandomZombie();
            }
        });
        
        updateTimerDelay();
        requestFocus();
    }

    private void spawnRandomZombie() {
        if (gameInfo.zombiesSpawned >= gameInfo.getMaxZombiesPerWave()) {
            return;
        }

        // Avoid spawning too close to the player
        int safeDistance = 200; // Minimum distance from player
        int x, y;
        double distance;
        boolean validPosition;

        do {
            // Generate random position within panel
            x = random.nextInt(PANEL_WIDTH - Zombie.ZOMBIE_WIDTH);
            y = random.nextInt(PANEL_HEIGHT - Zombie.ZOMBIE_HEIGHT);

            // Calculate distance from player center
            double charCenterX = gameInfo.player.getCenterX();
            double charCenterY = gameInfo.player.getCenterY();
            double zombieCenterX = x + Zombie.ZOMBIE_WIDTH / 2;
            double zombieCenterY = y + Zombie.ZOMBIE_HEIGHT / 2;
            
            double dx = charCenterX - zombieCenterX;
            double dy = charCenterY - zombieCenterY;
            distance = Math.sqrt(dx*dx + dy*dy);
            
            // Check if position is valid (not on an obstacle)
            validPosition = background.isValidSpawnPosition(x, y, Zombie.ZOMBIE_WIDTH, Zombie.ZOMBIE_HEIGHT);
            
        } while (distance < safeDistance || !validPosition);

        // Create and add the new zombie
        int zombieVariety = 0;
        if (gameInfo.currentWave <= 2) {
            zombieVariety = 1;
        } else if (gameInfo.currentWave <= 4) {
            zombieVariety = 2;
        } else if (gameInfo.currentWave <= 6) {
            zombieVariety = 3;
        } else {
            zombieVariety = 4;
        }
        int randomNumber = random.nextInt(zombieVariety);
        Zombie newZombie;

        if (randomNumber == 0) {
            newZombie = new NormalZombie(x, y);
        } else if (randomNumber == 1) {
            newZombie = new ReptileZombie(x, y);
        } else if (randomNumber == 2) {
            newZombie = new TankZombie(x, y);
        } else {
            newZombie = new AcidicZombie(x, y);
        }

        newZombie.moveSpeed *= gameInfo.currentZombieSpeedMultiplier;
        gameInfo.addZombie(newZombie);
    }

    public void centerplayer() {
        // Initial position at center of screen
        int x = (PANEL_WIDTH / 2) - (gameInfo.player.width / 2);
        int y = (PANEL_HEIGHT / 2) - (gameInfo.player.height / 2);
        
        // Check if this is a valid spawn position (not on an obstacle)
        boolean validPosition = background.isValidSpawnPosition(x, y, gameInfo.player.width, gameInfo.player.height);
        
        // If the center position isn't valid, find a nearby valid position using a spiral search pattern
        if (!validPosition) {
            int searchRadius = 25;
            int spiralX = 0;
            int spiralY = 0;
            int spiralDx = 0;
            int spiralDy = -1;
            
            // Search up to 25 tile cells away (should be enough to find an empty spot)
            for (int i = 0; i < searchRadius * searchRadius; i++) {
                // Try the current position in the spiral
                int testX = x + (spiralX * background.TILE_SIZE);
                int testY = y + (spiralY * background.TILE_SIZE);
                
                if (background.isValidSpawnPosition(testX, testY, gameInfo.player.width, gameInfo.player.height)) {
                    // Found a valid position
                    x = testX;
                    y = testY;
                    validPosition = true;
                    break;
                }
                
                // Move to the next position in the spiral
                if (spiralX == spiralY || (spiralX < 0 && spiralX == -spiralY) || (spiralX > 0 && spiralX == 1-spiralY)) {
                    // Change direction when we hit a corner in the spiral
                    int temp = spiralDx;
                    spiralDx = -spiralDy;
                    spiralDy = temp;
                }
                spiralX += spiralDx;
                spiralY += spiralDy;
            }
            
            // If we still couldn't find a valid position, use the original center (fallback)
            // This is very unlikely but we should handle it anyway
            if (!validPosition) {
                x = (PANEL_WIDTH / 2) - (gameInfo.player.width / 2);
                y = (PANEL_HEIGHT / 2) - (gameInfo.player.height / 2);
            }
        }
        
        // Set player position
        gameInfo.player.x = x;
        gameInfo.player.y = y;
    }

    private void shootBullet() {
        Weapon currentWeapon = gameInfo.player.currentWeapon;

        if (currentWeapon.isReloading) {
            return;
        }

        currentWeapon.currentAmmo -= 1;
        if (currentWeapon.currentAmmo < 0) {
            currentWeapon.currentAmmo = 0;
            return;
        }
        if (gameInfo.statPanel != null) {
            gameInfo.statPanel.update();
        }
        double centerX = currentWeapon.getCenterX();
        double centerY = currentWeapon.getCenterY();

        if (currentWeapon instanceof Shotgun) {
            int bulletCount = 9;
            double spreadAngle = 5.0;
            double startAngle = currentWeapon.rotation - (spreadAngle * (bulletCount - 1) / 2);
            
            for (int i = 0; i < bulletCount; i++) {
                Bullet bullet = new Bullet(centerX, centerY, currentWeapon);
                
                // Calculate angle for this bullet
                double bulletAngle = startAngle + (spreadAngle * i);
                double angleRadians = Math.toRadians(bulletAngle);
                
                bullet.directionX = Math.cos(angleRadians);
                bullet.directionY = Math.sin(angleRadians);
                bullet.rotation = bulletAngle;
                
                gameInfo.bullets.add(bullet);
            }
        } else {
            Bullet bullet = new Bullet(centerX, centerY, currentWeapon);

            double shootingAngleOffset = 0;
            if (currentWeapon != null) {
                shootingAngleOffset = currentWeapon.shootingAngle;
            }

            // Generate random angle within the gun's shooting angle range
            double randomSpread = random.nextDouble() * shootingAngleOffset - (shootingAngleOffset / 2);

            // Apply the player rotation plus random spread
            double finalAngle = currentWeapon.rotation + randomSpread;
            double angleRadians = Math.toRadians(finalAngle);

            bullet.directionX = Math.cos(angleRadians);
            bullet.directionY = Math.sin(angleRadians);
            bullet.rotation = finalAngle;

            gameInfo.bullets.add(bullet);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Apply rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // Draw the background
        background.draw(g2d, PANEL_WIDTH, PANEL_HEIGHT, gameInfo.player.x, gameInfo.player.y);

        AffineTransform originalTransform = g2d.getTransform();

        // Calculate viewport bounds (what's visible on screen) with culling margin
        int viewportMinX = -cullingMargin;
        int viewportMinY = -cullingMargin;
        int viewportMaxX = PANEL_WIDTH + cullingMargin;
        int viewportMaxY = PANEL_HEIGHT + cullingMargin;

        // Draw all drops - with culling
        for (Drop drop : gameInfo.drops) {
            if (!drop.isCollected() && isEntityVisible(drop, viewportMinX, viewportMinY, viewportMaxX, viewportMaxY)) {
                if (drop.image != null) {
                    g2d.drawImage(drop.image, (int)drop.x, (int)drop.y, 
                                 drop.width, drop.height, null);
                } else {
                    // Fallback if image fails to load
                    g2d.setColor(Color.YELLOW);
                    g2d.fillRect((int)drop.x, (int)drop.y, drop.width, drop.height);
                }
            }
        }

        // Draw player
        if (gameInfo.player.image != null) {
            AffineTransform transform = g2d.getTransform();
            
            // Create a new transform for drawing the player
            AffineTransform playerTransform = new AffineTransform();
            
            // Apply player position and flipping
            if (gameInfo.player.facingLeft) {
                playerTransform.translate(gameInfo.player.x + gameInfo.player.width, gameInfo.player.y);
                playerTransform.scale(-1, 1);
            } else {
                playerTransform.translate(gameInfo.player.x, gameInfo.player.y);
            }
            
            g2d.setTransform(playerTransform);
            
            // Apply flash effect if player is flashing
            if (gameInfo.player.isFlashing()) {
                // Use pre-cached flash image if available, otherwise create it
                if (playerFlashImage == null && useImageCaching) {
                    playerFlashImage = flashEffect.filter(gameInfo.player.image, null);
                }
                
                BufferedImage flashImage = useImageCaching ? playerFlashImage : flashEffect.filter(gameInfo.player.image, null);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.drawImage(flashImage, 0, 0, gameInfo.player.width, gameInfo.player.height, null);
            } else {
                g2d.drawImage(gameInfo.player.image, 0, 0, gameInfo.player.width, gameInfo.player.height, null);
            }
            
            g2d.setTransform(transform);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect((int)gameInfo.player.x, (int)gameInfo.player.y, 50, 50);
        }
        drawCooldownBar(g2d);
        drawReloadingBar(g2d);

        // Draw the gun
        gameInfo.player.updateGunPosition();
        if (gameInfo.player.currentWeapon != null && gameInfo.player.currentWeapon.image != null) {
            AffineTransform gunTransform = new AffineTransform();
            
            // First translate to player center
            double playerCenterX = gameInfo.player.getCenterX();
            double playerCenterY = gameInfo.player.getCenterY();
            
            // Calculate gun offset from player center
            double gunOffsetX = gameInfo.player.width/2; // Offset from player center
            
            // Apply rotation around player center
            // Add 45-degree offset to compensate for tilted weapon image
            double weaponAngleOffset = Math.toRadians(45);
            double angleRad = Math.toRadians(gameInfo.player.currentWeapon.rotation) + weaponAngleOffset;
            
            // Calculate gun position after rotation
            double rotatedGunX = playerCenterX + Math.cos(angleRad) * gunOffsetX - gameInfo.player.currentWeapon.width/2;
            
            // Update gun position
            gameInfo.player.currentWeapon.x = rotatedGunX;
            gameInfo.player.currentWeapon.y = gameInfo.player.getCenterY() - gameInfo.player.currentWeapon.height/2;
            
            boolean facingLeft = mouseX < gameInfo.player.getCenterX();
            if (facingLeft) {
                angleRad += Math.toRadians(-90);
            }
            gunTransform.rotate(angleRad, playerCenterX, playerCenterY);
            
            if (facingLeft) {
                gunTransform.scale(1, -1);
                gunTransform.translate(0, -2 * gameInfo.player.currentWeapon.y - gameInfo.player.currentWeapon.height);
            }

            g2d.setTransform(gunTransform);

            g2d.drawImage(gameInfo.player.currentWeapon.image, (int)gameInfo.player.currentWeapon.x, (int)gameInfo.player.currentWeapon.y, 
                         gameInfo.player.currentWeapon.width, gameInfo.player.currentWeapon.height, null);
        }
        g2d.setTransform(originalTransform);

        // Draw all bullets - with culling
        for (Bullet bullet : gameInfo.bullets) {
            if (isEntityVisible(bullet, viewportMinX, viewportMinY, viewportMaxX, viewportMaxY)) {
                if (bullet.image != null) {
                    // Create rotation transform for the bullet
                    AffineTransform bulletTransform = new AffineTransform();
                    bulletTransform.rotate(Math.toRadians(bullet.rotation), bullet.getCenterX(), bullet.getCenterY());
                    g2d.setTransform(bulletTransform);

                    g2d.drawImage(bullet.image, (int)bullet.x, (int)bullet.y, 
                                 bullet.width, bullet.height, null);

                    // Reset transform after drawing each bullet
                    g2d.setTransform(originalTransform);
                } else {
                    // Fallback if bullet image fails to load
                    g2d.setColor(Color.BLUE);
                    g2d.fillOval((int)bullet.x, (int)bullet.y, bullet.width, bullet.height);
                }
            }
        }

        // Draw all zombies - with culling
        for (Zombie zombie : gameInfo.zombies) {
            if (isEntityVisible(zombie, viewportMinX, viewportMinY, viewportMaxX, viewportMaxY)) {
                if (zombie.image != null) {
                    AffineTransform transform = g2d.getTransform();
                    AffineTransform zombieTransform = new AffineTransform();
                    
                    if (zombie.directionX < 0) {
                        zombieTransform.translate(zombie.x + zombie.width, zombie.y);
                        zombieTransform.scale(-1, 1);
                    } else {
                        zombieTransform.translate(zombie.x, zombie.y);
                    }
                    
                    g2d.setTransform(zombieTransform);
                    
                    // Apply flash effect if zombie is flashing
                    if (zombie.isFlashing()) {
                        // Get zombie type to use appropriate cached image
                        int zombieType = getZombieTypeIndex(zombie);
                        
                        // Use pre-cached flash image if available, otherwise create it
                        if (useImageCaching && zombieFlashImages[zombieType] == null) {
                            zombieFlashImages[zombieType] = flashEffect.filter(zombie.image, null);
                        }
                        
                        BufferedImage flashImage = useImageCaching ? 
                            zombieFlashImages[zombieType] : flashEffect.filter(zombie.image, null);
                        
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                        g2d.drawImage(flashImage, 0, 0, zombie.width, zombie.height, null);
                    } else {
                        g2d.drawImage(zombie.image, 0, 0, zombie.width, zombie.height, null);
                    }
                    
                    g2d.setTransform(transform);
                    drawHealthBar(g2d, zombie);
                }
            }
        }

        // Draw all active animations - with culling
        for (Animation animation : gameInfo.animations) {
            if (isEntityVisible(animation, viewportMinX, viewportMinY, viewportMaxX, viewportMaxY)) {
                animation.draw(g2d);
            }
        }
        
        // Display FPS if enabled
        if (showFPS) {
            // Calculate FPS
            frameCount++;
            long currentTime = System.currentTimeMillis();
            double updateInterval = 1000;
            if (currentTime - lastFPSCheck >= updateInterval) {
                currentFPS = (int) (frameCount * 1000 / updateInterval);
                frameCount = 0;
                lastFPSCheck = currentTime;
            }
            
            // Draw FPS counter
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("FPS: " + currentFPS, PANEL_WIDTH - 100, 20);
        }
    }
    
    // Helper method to determine zombie type for caching
    private int getZombieTypeIndex(Zombie zombie) {
        if (zombie instanceof NormalZombie) return 0;
        if (zombie instanceof ReptileZombie) return 1;
        if (zombie instanceof TankZombie) return 2;
        if (zombie instanceof AcidicZombie) return 3;
        return 0; // Default to normal zombie
    }
    
    // Helper method for viewport culling
    private boolean isEntityVisible(Entity entity, int minX, int minY, int maxX, int maxY) {
        if (!useViewportCulling) return true;
        
        return entity.x + entity.width >= minX && entity.x <= maxX &&
               entity.y + entity.height >= minY && entity.y <= maxY;
    }

    // Game update loop
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameInfo.isPaused) {
            return;
        }

        if (gameInfo.player.health <= 0) {
            gameInfo.player.health = 0;
            gameInfo.gameTimer.stop();
            gameInfo.zombieSpawnTimer.stop();
            gameInfo.showGameOver();
            return;
        }

        Weapon currentWeapon = gameInfo.player.currentWeapon;

        if (leftMousePressed && currentWeapon.canShoot()) {
            shootBullet();
            currentWeapon.shoot();
        }

        // Handle player movement
        double horizontalMovement = 0;
        double verticalMovement = 0;
        
        if (moveUp) verticalMovement -= gameInfo.PLAYER_SPEED;
        if (moveDown) verticalMovement += gameInfo.PLAYER_SPEED;
        if (moveLeft) horizontalMovement -= gameInfo.PLAYER_SPEED;
        if (moveRight) horizontalMovement += gameInfo.PLAYER_SPEED;

        // Set player animation state based on movement
        boolean isMoving = (horizontalMovement != 0 || verticalMovement != 0);
        gameInfo.player.setMoving(isMoving);
        gameInfo.player.updateAnimation();

        // Normalize the direction vector
        double movementLength = Math.sqrt(horizontalMovement * horizontalMovement + verticalMovement * verticalMovement);
        if (movementLength > 0) {
            horizontalMovement /= movementLength;
            verticalMovement /= movementLength;
        }
        if (horizontalMovement != 0 || verticalMovement != 0) {
            // Try to update the background position with collision detection
            boolean moveSuccessful = background.update(horizontalMovement * gameInfo.PLAYER_SPEED, 
                                                      verticalMovement * gameInfo.PLAYER_SPEED,
                                                        gameInfo.player.height);
            
            // Only move entities if the player's move was successful
            if (moveSuccessful) {
                for (Entity entity : Entity.entities) {
                    if (entity instanceof Player || entity instanceof Weapon) {
                        continue;
                    }
                    entity.x -= horizontalMovement * gameInfo.PLAYER_SPEED;
                    entity.y -= verticalMovement * gameInfo.PLAYER_SPEED;
                }
            }
        }
        
        // Update bullet positions
        List<Bullet> bulletsToRemove = new ArrayList<>();
        List<Zombie> zombiesToRemove = new ArrayList<>();
        List<Drop> dropsToRemove = new ArrayList<>();
        List<Animation> animationsToRemove = new ArrayList<>();

        for (Bullet bullet : gameInfo.bullets) {
            // Move bullet in its direction
            bullet.x += bullet.directionX * gameInfo.BULLET_SPEED;
            bullet.y += bullet.directionY * gameInfo.BULLET_SPEED;
            
            // Remove bullets that go off screen
            if (bullet.x < -PANEL_WIDTH || bullet.x > PANEL_WIDTH*2 || bullet.y < -PANEL_HEIGHT || bullet.y > PANEL_HEIGHT*2) {
                bulletsToRemove.add(bullet);
                continue;
            }

            if (bullet.isZombieBullet()) {
                // Check collision with player
                if (bullet.getBounds().intersects(gameInfo.player.getBounds())) {
                    gameInfo.player.health -= bullet.getDamage();
                    gameInfo.player.startFlashEffect();
                    if (gameInfo.statPanel != null) {
                        gameInfo.statPanel.update();
                    }
                    bulletsToRemove.add(bullet);
                }
            } else {
                for (Zombie zombie : gameInfo.zombies) {
                    if (!bullet.hitZombies.contains(zombie) && bullet.getBounds().intersects(zombie.getBounds())) {
                        if (bullet.getSourceWeapon() instanceof RocketLauncher) {
                            GameInfo.playSound(((RocketLauncher)bullet.getSourceWeapon()).hitSoundPath);
                            applyBlastDamageToZombies(zombiesToRemove, bullet, zombie, RocketLauncher.BLAST_RADIUS);

                            // Create an explosion
                            int explosionSize = (int) (RocketLauncher.BLAST_RADIUS * 1.5);
                            Animation explosion = new Animation(
                                zombie.getCenterX() - explosionSize/2,
                                zombie.getCenterY() - explosionSize/2,
                                explosionSize, explosionSize, 50, false, "explosion");
                            explosion.loadFrames("assets/Explosion");
                            gameInfo.animations.add(explosion);
                        } else {
                            zombie.health -= bullet.getDamage();
                            zombie.startFlashEffect();
                            if (zombie.health <= 0) {
                                zombiesToRemove.add(zombie);
                                gameInfo.player.kills++;
                                gameInfo.player.score += zombie.score;                
                                createLootDrop(zombie);
                                if (zombie instanceof AcidicZombie) {
                                    applyBlastDamageToZombies(zombiesToRemove, bullet, zombie, AcidicZombie.BLAST_RADIUS);
                                }
                            }
                            bullet.hitZombies.add(zombie);
                        }
                        if (!(bullet.getSourceWeapon() instanceof Sniper)) {
                            bulletsToRemove.add(bullet);
                            break;
                        }
                    }
                }
            }
        }

        // Update zombie positions
        for (Zombie zombie : gameInfo.zombies) {
            // Calculate direction vector from zombie to player
            double zombieCenterX = zombie.getCenterX();
            double zombieCenterY = zombie.getCenterY();
            double charCenterX = gameInfo.player.getCenterX();
            double charCenterY = gameInfo.player.getCenterY();

            // Direction vector
            double dx = charCenterX - zombieCenterX;
            double dy = charCenterY - zombieCenterY;

            // Normalize the direction vector
            double length = Math.sqrt(dx * dx + dy * dy);
            if (length > 0) {
                dx /= length;
                dy /= length;
            }
    
            double distanceToplayer = length;

            // Check if the zombie can hit the player
            if (distanceToplayer < zombie.attackRange && zombie.canAttack()) {
                if (zombie instanceof AcidicZombie) {
                    AcidicZombie acidicZombie = (AcidicZombie)zombie;
                    Bullet acidBullet = acidicZombie.shootAcid(charCenterX, charCenterY);
                    acidBullet.setZombieBullet(true);
                    acidBullet.setDamage(acidicZombie.damage);        
                    gameInfo.bullets.add(acidBullet);
                } else {
                    gameInfo.player.health -= zombie.damage;
                    gameInfo.player.startFlashEffect();
                    if (gameInfo.statPanel != null) {
                        gameInfo.statPanel.update();
                    }
                    zombie.attack();
                }
            }

            // Update zombie positions
            if (zombie instanceof ReptileZombie) {
                ReptileZombie reptileZombie = (ReptileZombie)zombie;
                
                if (distanceToplayer < ReptileZombie.JUMP_DISTANCE) {
                    if (!reptileZombie.isJumping && reptileZombie.canJump()) {
                        reptileZombie.prepareJump(charCenterX, charCenterY);
                    }
                }

                if (reptileZombie.isJumping) {
                    double moveX = reptileZombie.jumpDirectionX * reptileZombie.moveSpeed * ReptileZombie.JUMP_SPEED;
                    double moveY = reptileZombie.jumpDirectionY * reptileZombie.moveSpeed * ReptileZombie.JUMP_SPEED;
                    
                    // Check if move is valid before applying it
                    if (background.isValidMoveForEntity(zombie.x, zombie.y, moveX, moveY, zombie.width, zombie.height)) {
                        reptileZombie.updateJump();
                    } else {
                        // If movement is blocked, stop jumping and try normal movement next frame
                        reptileZombie.isJumping = false;
                    }
                } else {
                    double moveX = dx * zombie.moveSpeed;
                    double moveY = dy * zombie.moveSpeed;
                    
                    // Check if move is valid before applying it
                    if (background.isValidMoveForEntity(zombie.x, zombie.y, moveX, moveY, zombie.width, zombie.height)) {
                        zombie.x += moveX;
                        zombie.y += moveY;
                    } else {
                        // Try to slide along walls by attempting to move in just X or Y direction
                        if (background.isValidMoveForEntity(zombie.x, zombie.y, moveX, 0, zombie.width, zombie.height)) {
                            zombie.x += moveX;
                        } else if (background.isValidMoveForEntity(zombie.x, zombie.y, 0, moveY, zombie.width, zombie.height)) {
                            zombie.y += moveY;
                        }
                    }
                }
            } else {
                double moveX = dx * zombie.moveSpeed;
                double moveY = dy * zombie.moveSpeed;
                
                // Check if move is valid before applying it
                if (background.isValidMoveForEntity(zombie.x, zombie.y, moveX, moveY, zombie.width, zombie.height)) {
                    zombie.x += moveX;
                    zombie.y += moveY;
                } else {
                    // Try to slide along walls by attempting to move in just X or Y direction
                    if (background.isValidMoveForEntity(zombie.x, zombie.y, moveX, 0, zombie.width, zombie.height)) {
                        zombie.x += moveX;
                    } else if (background.isValidMoveForEntity(zombie.x, zombie.y, 0, moveY, zombie.width, zombie.height)) {
                        zombie.y += moveY;
                    }
                }
            }

            // Store direction for flipping in the renderer
            zombie.directionX = dx;
            zombie.directionY = dy;
        }

        for (Drop drop : gameInfo.drops) {
            if (!drop.isCollected() && gameInfo.player.getBounds().intersects(drop.getBounds())) {
                if (drop instanceof HealthDrop) {
                    HealthDrop healthDrop = (HealthDrop)drop;
                    if (gameInfo.player.health < Player.PLAYER_HEALTH) {
                        drop.collect();
                        dropsToRemove.add(drop);
                        
                        if (gameInfo.player.health <= Player.PLAYER_HEALTH - healthDrop.getHealthAmount()) {
                            gameInfo.player.health += healthDrop.getHealthAmount();
                        } else {
                            gameInfo.player.health = Player.PLAYER_HEALTH;
                        }
                    }
                } else {
                    drop.collect();
                    dropsToRemove.add(drop);

                    if (drop instanceof AmmoDrop) {
                        AmmoDrop weaponDrop = (AmmoDrop)drop;

                        int ammoToAdd = weaponDrop.getAmmoAmount();
                        Weapon sourceWeapon = weaponDrop.getSourceWeapon();

                        for (Weapon playerWeapon : gameInfo.player.weapons) {
                            if (playerWeapon != null && playerWeapon.getClass().equals(sourceWeapon.getClass())) {
                                if (playerWeapon instanceof RocketLauncher) {
                                    playerWeapon.currentAmmo += ammoToAdd;
                                } else {                    
                                    if (playerWeapon.currentTotalAmmo <= Integer.MAX_VALUE - ammoToAdd) {
                                        playerWeapon.currentTotalAmmo += ammoToAdd;
                                    } else {
                                        playerWeapon.currentTotalAmmo = Integer.MAX_VALUE;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
     
                // Update the stats panel
                if (gameInfo.statPanel != null) {
                    gameInfo.statPanel.update();
                }
            }
        }

        for (Animation animation : gameInfo.animations) {
            if (!animation.update()) {
                animationsToRemove.add(animation);
            }
        }

        // Remove animations that have finished
        gameInfo.animations.removeAll(animationsToRemove);
        // Remove bullets that went off screen or hit zombies
        gameInfo.bullets.removeAll(bulletsToRemove);
        // Remove zombies that were hit
        gameInfo.zombies.removeAll(zombiesToRemove);
        // Remove collected drops
        gameInfo.drops.removeAll(dropsToRemove);

        // Update zombies killed count
        gameInfo.zombiesKilled += zombiesToRemove.size();
        
        // Update zombies remaining count
        gameInfo.updateZombiesRemaining(gameInfo.zombies.size());
        
        // Check if we need to advance to the next wave
        gameInfo.incrementWaveIfNeeded();

        repaint();
    }

    private void applyBlastDamageToZombies(List<Zombie> zombiesToRemove, Bullet bullet, Zombie sourceZombie, double blastRadius) {
        for (Zombie targetZombie : gameInfo.zombies) {
            if (zombiesToRemove.contains(targetZombie)) {
                continue;
            }
            double dx = targetZombie.getCenterX() - sourceZombie.getCenterX();
            double dy = targetZombie.getCenterY() - sourceZombie.getCenterY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < blastRadius) {
                targetZombie.health -= bullet.getDamage();
                targetZombie.startFlashEffect();
                if (targetZombie.health <= 0) {
                    zombiesToRemove.add(targetZombie);
                    gameInfo.player.kills++;
                    gameInfo.player.score += targetZombie.score;    
                    createLootDrop(targetZombie);
                    if (targetZombie instanceof AcidicZombie) {
                        applyBlastDamageToZombies(zombiesToRemove, bullet, sourceZombie, AcidicZombie.BLAST_RADIUS);
                    }
                }
                bullet.hitZombies.add(sourceZombie);
            }
        }
    }

    private void createLootDrop(Zombie zombie) {
        if (random.nextFloat() < gameInfo.HEALTH_DROP_CHANCE) {
            HealthDrop healthDrop = new HealthDrop(zombie.getCenterX(), zombie.getCenterY());
            gameInfo.addDrop(healthDrop);
        } else if (random.nextFloat() < gameInfo.AMMO_DROP_CHANCE) {
            Weapon randomWeapon;
            int randomWeaponVariety;
            if (gameInfo.currentWave == 1) {
                randomWeaponVariety = -1;
            } else if (gameInfo.currentWave <= 3) {
                randomWeaponVariety = 1;
            } else if (gameInfo.currentWave <= 5) {
                randomWeaponVariety = 2;
            } else if (gameInfo.currentWave <= 10) {
                randomWeaponVariety = 3;
            } else {
                randomWeaponVariety = 4;
            }
            if (randomWeaponVariety != -1) {
                int randomWeaponNumber = random.nextInt(randomWeaponVariety);
                if (randomWeaponNumber == 0) {
                    randomWeapon = new Rifle();
                } else if (randomWeaponNumber == 1) {
                    randomWeapon = new Shotgun();
                } else if (randomWeaponNumber == 2) {
                    randomWeapon = new Sniper();
                } else {
                    randomWeapon = new RocketLauncher();
                }
                AmmoDrop ammoDrop = new AmmoDrop(zombie.getCenterX(), zombie.getCenterY(), randomWeapon);
                gameInfo.addDrop(ammoDrop);
            }
        }
    }

    private void drawHealthBar(Graphics2D g2d, Zombie zombie) {
        // Health bar dimensions and position
        int barWidth = zombie.width;
        int barHeight = 5;
        int barX = (int)zombie.x;
        int barY = (int)zombie.y - 10;
        
        // Draw background (empty health bar)
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        // Calculate filled portion based on health percentage
        double healthPercentage = (double)zombie.health / zombie.maxHealth;
        int filledWidth = (int)(barWidth * healthPercentage);
        
        // Choose color based on health percentage or flash white if being hit
        if (zombie.isFlashing()) {
            g2d.setColor(Color.WHITE);
        } else if (healthPercentage > 0.66) {
            g2d.setColor(Color.GREEN);
        } else if (healthPercentage > 0.33) {
            g2d.setColor(Color.ORANGE);
        } else {
            g2d.setColor(Color.RED);
        }
        
        // Draw filled portion
        g2d.fillRect(barX, barY, filledWidth, barHeight);
        
        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(barX, barY, barWidth, barHeight);
    }

    private void drawCooldownBar(Graphics2D g2d) {
        Weapon currentWeapon = gameInfo.player.currentWeapon;
        if (!currentWeapon.isReloading && !currentWeapon.canShoot() && currentWeapon.currentAmmo > 0) {
            long currentTime = System.currentTimeMillis();
            long lastShotTime = currentWeapon.lastShotTime;

            double elapsedTime = currentTime - lastShotTime;
            double cooldownPercentage = Math.min(1.0, elapsedTime / currentWeapon.fireDelay);

            int barWidth = gameInfo.player.width;
            int barHeight = 5;
            int barX = (int)gameInfo.player.x;
            int barY = (int)gameInfo.player.y - 15;
            
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(barX, barY, barWidth, barHeight);
            
            int filledWidth = (int)(barWidth * cooldownPercentage);
            
            g2d.setColor(new Color(30, 144, 255));
            g2d.fillRect(barX, barY, filledWidth, barHeight);
            
            g2d.setColor(Color.BLACK);
            g2d.drawRect(barX, barY, barWidth, barHeight);
        }
    }

    private void drawReloadingBar(Graphics2D g2d) {
        Weapon currentWeapon = gameInfo.player.currentWeapon;
        
        // Only show when player is reloading
        if (currentWeapon.isReloading) {
            long currentTime = System.currentTimeMillis();
            long reloadStartTime = currentWeapon.reloadStartTime;
            long reloadTime = currentWeapon.reloadTimeMs;

            double elapsedTime = currentTime - reloadStartTime;
            double reloadPercentage = Math.min(1.0, elapsedTime / reloadTime);

            int barWidth = gameInfo.player.width;
            int barHeight = 5;
            int barX = (int)gameInfo.player.x;
            int barY = (int)gameInfo.player.y - 25; // Position above cooldown bar
            
            // Draw background (empty reload bar)
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(barX, barY, barWidth, barHeight);
            
            // Draw filled portion
            int filledWidth = (int)(barWidth * reloadPercentage);
            g2d.setColor(Color.YELLOW); // Yellow for reloading
            g2d.fillRect(barX, barY, filledWidth, barHeight);
            
            // Draw border
            g2d.setColor(Color.BLACK);
            g2d.drawRect(barX, barY, barWidth, barHeight);
            
            // Optionally display "RELOADING" text
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString("RELOADING", barX + 2, barY - 2);
        }
    }

    private void togglePause() {
        if (gameInfo.isPaused) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    public void pauseGame() {
        gameInfo.isPaused = true;

        // Stop game timers
        if (gameInfo.gameTimer != null && gameInfo.gameTimer.isRunning()) {
            gameInfo.gameTimer.stop();
        }
        if (gameInfo.zombieSpawnTimer != null && gameInfo.zombieSpawnTimer.isRunning()) {
            gameInfo.zombieSpawnTimer.stop();
        }
        
        // Show pause panel
        if (gameInfo.pauseGamePanel != null) {
            gameInfo.pauseGamePanel.setVisible(true);
        }
    }
    
    public void resumeGame() {
        gameInfo.isPaused = false;
        
        // Restart game timers
        if (gameInfo.gameTimer != null && !gameInfo.gameTimer.isRunning()) {
            gameInfo.gameTimer.start();
        }
        if (gameInfo.zombieSpawnTimer != null && !gameInfo.zombieSpawnTimer.isRunning()) {
            gameInfo.zombieSpawnTimer.start();
        }
        
        // Hide pause panel
        if (gameInfo.pauseGamePanel != null) {
            gameInfo.pauseGamePanel.setVisible(false);
        }
        
        // Ensure focus returns to game panel
        requestFocus();
    }

    private void toggleFPSDisplay() {
        showFPS = !showFPS;
    }
    
    private void updateTimerDelay() {
        int delay = 1000 / targetFPS;
        if (gameInfo.gameTimer != null) {
            gameInfo.gameTimer.setDelay(delay);
        }
    }
}
