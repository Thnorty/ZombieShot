import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Dimension;
import java.awt.Color;

public class GamePanel extends JPanel implements ActionListener {
    private final int PANEL_WIDTH = GameFrame.WIDTH;
    private final int PANEL_HEIGHT = GameFrame.HEIGHT - StatPanel.HEIGHT;

    private GameInfo gameInfo;
    private int mouseX = PANEL_WIDTH / 2;
    private int mouseY = PANEL_HEIGHT / 2;
    private Random random = new Random();

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

        // Mouse motion listener for player rotation
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent evt) {
                updateMousePosition(evt);
            }
            
            public void mouseDragged(MouseEvent evt) {
                updateMousePosition(evt);
            }
            
            private void updateMousePosition(MouseEvent evt) {
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
                switch (evt.getKeyCode()) {
                    case KeyEvent.VK_W:
                        moveUp = true;
                        break;
                    case KeyEvent.VK_S:
                        moveDown = true;
                        break;
                    case KeyEvent.VK_A:
                        moveLeft = true;
                        break;
                    case KeyEvent.VK_D:
                        moveRight = true;
                        break;
                    case KeyEvent.VK_R:
                        gameInfo.player.reload();
                        if (gameInfo.statPanel != null)
                            gameInfo.statPanel.update();
                        break;
                    case KeyEvent.VK_1:
                        if (gameInfo.player.weapons.size() >= 1) {
                            gameInfo.player.currentWeapon = gameInfo.player.weapons.get(0);
                            if (gameInfo.statPanel != null)
                                gameInfo.statPanel.update();
                        }
                        break;
                    case KeyEvent.VK_2:
                        if (gameInfo.player.weapons.size() >= 2) {
                            gameInfo.player.currentWeapon = gameInfo.player.weapons.get(1);
                            if (gameInfo.statPanel != null)
                                gameInfo.statPanel.update();
                        }
                        break;
                    case KeyEvent.VK_3:
                        if (gameInfo.player.weapons.size() >= 3) {
                            gameInfo.player.currentWeapon = gameInfo.player.weapons.get(2);
                            if (gameInfo.statPanel != null)
                                gameInfo.statPanel.update();
                        }
                        break;
                    case KeyEvent.VK_4:
                        if (gameInfo.player.weapons.get(3) != null) {
                            gameInfo.player.currentWeapon = gameInfo.player.weapons.get(3);
                            if (gameInfo.statPanel != null)
                                gameInfo.statPanel.update();
                        }
                        break;
                    case KeyEvent.VK_5:
                        if (gameInfo.player.weapons.get(4) != null) {
                            gameInfo.player.currentWeapon = gameInfo.player.weapons.get(4);
                            if (gameInfo.statPanel != null)
                                gameInfo.statPanel.update();
                        }
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                switch (evt.getKeyCode()) {
                    case KeyEvent.VK_W:
                        moveUp = false;
                        break;
                    case KeyEvent.VK_S:
                        moveDown = false;
                        break;
                    case KeyEvent.VK_A:
                        moveLeft = false;
                        break;
                    case KeyEvent.VK_D:
                        moveRight = false;
                        break;
                }
            }
        });

        centerplayer();
        gameInfo.gameTimer = new Timer(16, this);
        
        // Zombie spawn timer
        gameInfo.zombieSpawnTimer = new Timer(gameInfo.ZOMBIE_SPAWN_RATE, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spawnRandomZombie();
            }
        });

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
        } while (distance < safeDistance);

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
        if (randomNumber == 0) {
            gameInfo.addZombie(new NormalZombie(x, y));
        } else if (randomNumber == 1) {
            gameInfo.addZombie(new ReptileZombie(x, y));
        } else if (randomNumber == 2) {
            gameInfo.addZombie(new TankZombie(x, y));
        } else {
            gameInfo.addZombie(new AcidicZombie(x, y));
        }
    }

    public void centerplayer() {
        if (gameInfo.player.image != null) {
            int x = (PANEL_WIDTH / 2) - (gameInfo.player.width / 2);
            int y = (PANEL_HEIGHT / 2) - (gameInfo.player.height / 2);
            
            gameInfo.player.x = x;
            gameInfo.player.y = y;
        } else {
            gameInfo.player.x = (PANEL_WIDTH / 2) - 25;
            gameInfo.player.y = (PANEL_HEIGHT / 2) - 25;
        }
    }

    private void shootBullet() {
        Weapon currentWeapon = gameInfo.player.currentWeapon;

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

        AffineTransform originalTransform = g2d.getTransform();

        // Draw player
        if (gameInfo.player.image != null) {
            // Store original transform
            AffineTransform transform = g2d.getTransform();
            
            // Determine if player should be flipped based on mouse position
            boolean facingLeft = mouseX < gameInfo.player.getCenterX();
            
            if (facingLeft) {
                // player is facing left: flip horizontally
                AffineTransform playerTransform = new AffineTransform();
                playerTransform.translate(gameInfo.player.x + gameInfo.player.width, gameInfo.player.y);
                playerTransform.scale(-1, 1);
                g2d.setTransform(playerTransform);
                g2d.drawImage(gameInfo.player.image, 0, 0, gameInfo.player.width, gameInfo.player.height, null);
            } else {
                // player is facing right: draw normally
                g2d.drawImage(gameInfo.player.image, (int)gameInfo.player.x, (int)gameInfo.player.y, 
                             gameInfo.player.width, gameInfo.player.height, null);
            }
            
            // Reset transform
            g2d.setTransform(transform);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect((int)gameInfo.player.x, (int)gameInfo.player.y, 50, 50);
        }

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
            
            // Apply rotation for rendering
            gunTransform.rotate(angleRad, playerCenterX, playerCenterY);
            g2d.setTransform(gunTransform);
            
            // Draw the gun
            g2d.drawImage(gameInfo.player.currentWeapon.image, (int)gameInfo.player.currentWeapon.x, (int)gameInfo.player.currentWeapon.y, 
                         gameInfo.player.currentWeapon.width, gameInfo.player.currentWeapon.height, null);
        }
        g2d.setTransform(originalTransform);

        // Draw all bullets
        for (Bullet bullet : gameInfo.bullets) {
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

        // Draw all zombies
        for (Zombie zombie : gameInfo.zombies) {
            if (zombie.image != null) {
                AffineTransform transform = g2d.getTransform();
        
                // Create a new transform for drawing the zombie
                AffineTransform zombieTransform = new AffineTransform();
                if (zombie.directionX < 0) {
                    // Facing left: flip the image horizontally
                    zombieTransform.translate(zombie.x + zombie.width, zombie.y);
                    zombieTransform.scale(-1, 1);
                    g2d.setTransform(zombieTransform);
                    g2d.drawImage(zombie.image, 0, 0, zombie.width, zombie.height, null);
                } else {
                    g2d.drawImage(zombie.image, (int)zombie.x, (int)zombie.y, zombie.width, zombie.height, null);
                }
                g2d.setTransform(transform);
                drawHealthBar(g2d, zombie);
            } else {
                g2d.setColor(Color.GREEN);
                g2d.fillRect((int)zombie.x, (int)zombie.y, 50, 50);
            }
        }

        // Draw all drops
        for (Drop drop : gameInfo.drops) {
            if (!drop.isCollected() && drop.image != null) {
                g2d.drawImage(drop.image, (int)drop.x, (int)drop.y, 
                             drop.width, drop.height, null);
            } else if (!drop.isCollected()) {
                // Fallback if image fails to load
                g2d.setColor(Color.YELLOW);
                g2d.fillRect((int)drop.x, (int)drop.y, drop.width, drop.height);
            }
        }
    }

    // Game update loop
    @Override
    public void actionPerformed(ActionEvent e) {
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
        float horizontalMovement = 0;
        float verticalMovement = 0;
        
        if (moveUp) verticalMovement -= gameInfo.PLAYER_SPEED;
        if (moveDown) verticalMovement += gameInfo.PLAYER_SPEED;
        if (moveLeft) horizontalMovement -= gameInfo.PLAYER_SPEED;
        if (moveRight) horizontalMovement += gameInfo.PLAYER_SPEED;

        // Normalize the direction vector
        double movementLength = Math.sqrt(horizontalMovement * horizontalMovement + verticalMovement * verticalMovement);
        if (movementLength > 0) {
            horizontalMovement /= movementLength;
            verticalMovement /= movementLength;
        }
        if (horizontalMovement != 0 || verticalMovement != 0) {
            for (Entity entity : Entity.entities) {
                if (entity instanceof Player || entity instanceof Weapon) {
                    continue;
                }
                entity.x -= horizontalMovement * gameInfo.PLAYER_SPEED;
                entity.y -= verticalMovement * gameInfo.PLAYER_SPEED;
            }
        }
        
        // Update bullet positions
        List<Bullet> bulletsToRemove = new ArrayList<>();
        List<Zombie> zombiesToRemove = new ArrayList<>();
        List<Drop> dropsToRemove = new ArrayList<>();

        for (Bullet bullet : gameInfo.bullets) {
            // Move bullet in its direction
            bullet.x += bullet.directionX * gameInfo.BULLET_SPEED;
            bullet.y += bullet.directionY * gameInfo.BULLET_SPEED;
            
            // Remove bullets that go off screen
            if (bullet.x < 0 || bullet.x > PANEL_WIDTH || bullet.y < 0 || bullet.y > PANEL_HEIGHT) {
                bulletsToRemove.add(bullet);
                continue;
            }

            if (bullet.isZombieBullet()) {
                // Check collision with player
                if (bullet.getBounds().intersects(gameInfo.player.getBounds())) {
                    gameInfo.player.health -= bullet.getDamage();
                    if (gameInfo.statPanel != null) {
                        gameInfo.statPanel.update();
                    }
                    bulletsToRemove.add(bullet);
                }
            } else {
                for (Zombie zombie : gameInfo.zombies) {
                    if (!bullet.hitZombies.contains(zombie) && bullet.getBounds().intersects(zombie.getBounds())) {
                        if (bullet.getSourceWeapon() instanceof RocketLauncher) {
                            applyBlastDamageToZombies(zombiesToRemove, bullet, zombie, RocketLauncher.BLAST_RADIUS);
                        } else {
                            zombie.health -= bullet.getDamage();
                            if (zombie.health <= 0) {
                                zombiesToRemove.add(zombie);
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
    
            double distanceToplayer = Math.sqrt(dx * dx + dy * dy);
            distanceToplayer = length;

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
                    if (gameInfo.statPanel != null) {
                        gameInfo.statPanel.update();
                    }
                    zombie.attack();
                }
            }

            // Update zombie positions
            if (zombie instanceof ReptileZombie) {
                ReptileZombie reptileZombie = (ReptileZombie)zombie;
                
                if (distanceToplayer < 200) {
                    if (!reptileZombie.willJump && reptileZombie.canJump()) {
                        reptileZombie.prepareJump(charCenterX, charCenterY);
                    }
                }

                if (reptileZombie.willJump) {
                    reptileZombie.x += reptileZombie.jumpDirectionX * zombie.moveSpeed * ReptileZombie.JUMP_SPEED;
                    reptileZombie.y += reptileZombie.jumpDirectionY * zombie.moveSpeed * ReptileZombie.JUMP_SPEED;
                    reptileZombie.willJump = false;
                } else {
                    zombie.x += dx * zombie.moveSpeed;
                    zombie.y += dy * zombie.moveSpeed;
                }
            } else {
                zombie.x += dx * zombie.moveSpeed;
                zombie.y += dy * zombie.moveSpeed;
            }

            // Store direction for flipping in the renderer
            zombie.directionX = dx;
            zombie.directionY = dy;
        }

        for (Drop drop : gameInfo.drops) {
            if (!drop.isCollected() && gameInfo.player.getBounds().intersects(drop.getBounds())) {
                // Player collected the ammo drop
                drop.collect();
                dropsToRemove.add(drop);

                if (drop instanceof AmmoDrop) {
                    AmmoDrop weaponDrop = (AmmoDrop)drop;

                    int ammoToAdd = weaponDrop.getAmmoAmount();
                    Weapon sourceWeapon = weaponDrop.getSourceWeapon();

                    for (Weapon playerWeapon : gameInfo.player.weapons) {
                        if (playerWeapon != null && playerWeapon.getClass().equals(sourceWeapon.getClass())) {
                            if (playerWeapon.currentTotalAmmo <= Integer.MAX_VALUE - ammoToAdd) {
                                playerWeapon.currentTotalAmmo += ammoToAdd;
                            } else {
                                playerWeapon.currentTotalAmmo = Integer.MAX_VALUE;
                            }
                            break;
                        }
                    }
                } else if (drop instanceof HealthDrop) {
                    HealthDrop healthDrop = (HealthDrop)drop;
                    if (gameInfo.player.health <= Player.PLAYER_HEALTH - healthDrop.getHealthAmount()) {
                        gameInfo.player.health += healthDrop.getHealthAmount();
                    } else {
                        gameInfo.player.health = Player.PLAYER_HEALTH;
                    }
                }
                
                // Update the stats panel
                if (gameInfo.statPanel != null) {
                    gameInfo.statPanel.update();
                }
            }
        }

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
                if (targetZombie.health <= 0) {
                    zombiesToRemove.add(targetZombie);
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
            int ammoAmount = 30 + random.nextInt(10);
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
                AmmoDrop ammoDrop = new AmmoDrop(zombie.getCenterX(), zombie.getCenterY(), ammoAmount, randomWeapon);
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
        
        // Choose color based on health percentage
        if (healthPercentage > 0.66) {
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
}
