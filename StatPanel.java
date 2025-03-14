import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.awt.BasicStroke;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class StatPanel extends JPanel {
    public static final int WIDTH = GameFrame.WIDTH;
    public static final int HEIGHT = 75;

    private GameInfo gameInfo;
    private String ammoText = "Ammo: ";
    private String ammoClipText = "Ammo Clip: ";
    private String waveText = "Wave: ";
    private String zombiesRemainingText = "Zombies: ";
    private String scoreText = "Score: ";
    private JLabel ammoLabel;
    private JLabel ammoClipLabel;
    private JLabel waveLabel;
    private JLabel zombiesRemainingLabel;
    private JLabel scoreLabel;
    private JProgressBar healthBar;
    private JLabel healthLabel;
    private JLabel healthPercentLabel;
    private JLabel weaponNameLabel;
    private JLabel weaponImageLabel;
    private Image backgroundTexture;

    public StatPanel(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        setBounds(0, 0, WIDTH, HEIGHT);
        setLayout(null);
        
        // Load background texture
        try {
            backgroundTexture = ImageIO.read(new File("assets/Background/tile_0028.png"));
        } catch (IOException e) {
            System.err.println("Could not load panel texture");
        }
        
        // Custom weaponNameLabel panel with background
        JPanel weaponPanel = createStyledPanel(5, 5, 272, 65);
        weaponPanel.setLayout(null);
        
        weaponNameLabel = new JLabel(getWeaponName(gameInfo.player.currentWeapon));
        weaponNameLabel.setForeground(Color.YELLOW);
        weaponNameLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        weaponNameLabel.setBounds(10, 5, 200, 25);
        weaponPanel.add(weaponNameLabel);

        weaponImageLabel = new JLabel();
        updateWeaponImage(gameInfo.player.currentWeapon);
        weaponImageLabel.setBounds(200, 5, 64, 48);
        weaponPanel.add(weaponImageLabel);
        
        add(weaponPanel);

        // Create ammo panel
        JPanel ammoPanel = createStyledPanel(285, 5, 240, 65);
        ammoPanel.setLayout(null);
        
        ammoLabel = createLabelWithIcon(
            ammoText + gameInfo.player.currentWeapon.currentAmmo,
            "assets/Icons/bullets.png",
            Color.WHITE
        );
        ammoLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        ammoLabel.setBounds(10, 5, 220, 25);
        ammoPanel.add(ammoLabel);

        ammoClipLabel = createLabelWithIcon(
            ammoClipText + gameInfo.player.currentWeapon.currentTotalAmmo,
            "assets/Icons/gun-magazine.png",
            Color.WHITE
        );
        ammoClipLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        ammoClipLabel.setBounds(10, 35, 220, 25);  // Increased width
        ammoPanel.add(ammoClipLabel);
        
        add(ammoPanel);

        // Create wave panel (center) - make this stand out
        JPanel wavePanel = createStyledPanel(WIDTH/2 - 100, 5, 200, 65);
        wavePanel.setLayout(null);
        
        waveLabel = new JLabel(waveText + gameInfo.currentWave);
        waveLabel.setForeground(new Color(255, 100, 100));
        waveLabel.setFont(new Font("Impact", Font.BOLD, 32));
        waveLabel.setHorizontalAlignment(SwingConstants.CENTER);
        waveLabel.setBounds(0, 15, 200, 40);
        wavePanel.add(waveLabel);
        
        add(wavePanel);

        // Create zombies & score panel
        JPanel zombiesScorePanel = createStyledPanel(WIDTH - 490, 5, 200, 65);
        zombiesScorePanel.setLayout(null);
        
        zombiesRemainingLabel = createLabelWithIcon(
            zombiesRemainingText + gameInfo.zombies.size(),
            "assets/Icons/shambling-zombie.png",
            Color.GREEN
        );
        zombiesRemainingLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        zombiesRemainingLabel.setHorizontalAlignment(SwingConstants.LEFT);
        zombiesRemainingLabel.setBounds(10, 5, 200, 25);
        zombiesScorePanel.add(zombiesRemainingLabel);
        
        // Add score label under zombies
        scoreLabel = createLabelWithIcon(
            scoreText + gameInfo.player.score,
            "assets/Icons/stars-stack.png",
            Color.YELLOW
        );
        scoreLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        scoreLabel.setBounds(10, 35, 200, 25);
        zombiesScorePanel.add(scoreLabel);
        
        add(zombiesScorePanel);

        // Create health panel
        JPanel healthPanel = createStyledPanel(WIDTH - 280, 5, 275, 65);
        healthPanel.setLayout(null);
        
        healthLabel = createLabelWithIcon(
            "",
            "assets/Icons/health-normal.png",
            Color.RED,
            48, 48
        );
        healthLabel.setFont(new Font("Courier New", Font.BOLD, 16));
        healthLabel.setBounds(10, 5, 50, 58);
        healthPanel.add(healthLabel);

        healthBar = createCustomHealthBar();
        healthBar.setValue((int)((gameInfo.player.health / Player.PLAYER_HEALTH) * 100));
        healthBar.setBounds(65, 5, 165, 58);
        healthPanel.add(healthBar);

        healthPercentLabel = new JLabel((int)((gameInfo.player.health / Player.PLAYER_HEALTH) * 100) + "%");
        healthPercentLabel.setForeground(Color.RED);
        healthPercentLabel.setFont(new Font("Courier New", Font.BOLD, 14));
        healthPercentLabel.setBounds(235, 20, 40, 25);
        healthPanel.add(healthPercentLabel);
        
        add(healthPanel);

        setVisible(false);
    }

    private JPanel createStyledPanel(int x, int y, int width, int height) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(0, 0, 0, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2d.setColor(new Color(70, 70, 70));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                
                g2d.setColor(new Color(120, 120, 120, 100));
                g2d.drawLine(5, 2, getWidth()-5, 2);
            }
        };
        
        panel.setOpaque(false);
        panel.setBounds(x, y, width, height);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        return panel;
    }

    private JProgressBar createCustomHealthBar() {
        JProgressBar bar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(20, 20, 20));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                int val = getValue();
                int w = (int)(getWidth() * ((double)val / getMaximum()));
                
                Color startColor, endColor;
                if (val > 66) {
                    startColor = new Color(20, 200, 20);
                    endColor = new Color(100, 255, 100);
                } else if (val > 33) {
                    startColor = new Color(200, 150, 20);
                    endColor = new Color(255, 200, 0);
                } else {
                    startColor = new Color(200, 20, 20);
                    endColor = new Color(255, 100, 100);
                }
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, startColor, 
                    0, getHeight(), endColor
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, w, getHeight(), 8, 8);
                
                g2d.setColor(new Color(60, 60, 60));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
            }
        };
        
        bar.setBackground(new Color(30, 30, 30));
        bar.setForeground(Color.GREEN);
        bar.setBorderPainted(false);
        bar.setStringPainted(false);
        bar.setOpaque(false);
        return bar;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(40, 40, 40), 
            0, getHeight(), new Color(20, 20, 20)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        if (backgroundTexture != null) {
            g2d.setComposite(java.awt.AlphaComposite.getInstance(
                java.awt.AlphaComposite.SRC_OVER, 0.1f));
                
            int textureWidth = backgroundTexture.getWidth(this);
            int textureHeight = backgroundTexture.getHeight(this);
            
            for (int x = 0; x < getWidth(); x += textureWidth) {
                for (int y = 0; y < getHeight(); y += textureHeight) {
                    g2d.drawImage(backgroundTexture, x, y, textureWidth, textureHeight, this);
                }
            }
            g2d.setComposite(java.awt.AlphaComposite.SrcOver);
        }
        
        g2d.setColor(new Color(80, 80, 80));
        g2d.fillRect(0, getHeight()-2, getWidth(), 2);
    }

    public void update() {
        Weapon weapon = gameInfo.player.currentWeapon;
        weaponNameLabel.setText(getWeaponName(weapon));
        updateWeaponImage(weapon);
        ammoLabel.setText(ammoText + weapon.currentAmmo);
        ammoClipLabel.setText(ammoClipText + (weapon instanceof Pistol ? "∞" : weapon.currentTotalAmmo));
        zombiesRemainingLabel.setText(zombiesRemainingText + gameInfo.zombies.size());
        scoreLabel.setText(scoreText + gameInfo.player.score);
        waveLabel.setText(waveText + gameInfo.currentWave);

        int healthValue = (int)((gameInfo.player.health / Player.PLAYER_HEALTH) * 100);
        healthBar.setValue(healthValue);
        healthPercentLabel.setText(healthValue + "%");
        healthBar.repaint();
        
        Color healthColor;
        if (healthValue > 66) {
            healthColor = new Color(100, 255, 100);
        } else if (healthValue > 33) {
            healthColor = new Color(255, 200, 0);
        } else {
            healthColor = new Color(255, 100, 100);
        }
        
        healthPercentLabel.setForeground(healthColor);
    }

    private String getWeaponName(Weapon weapon) {
        if (weapon instanceof Pistol) {
            return "Pistol";
        } else if (weapon instanceof Rifle) {
            return "Rifle";
        } else if (weapon instanceof Shotgun) {
            return "Shotgun";
        } else if (weapon instanceof Sniper) {
            return "Sniper";
        } else if (weapon instanceof RocketLauncher) {
            return "Rocket Launcher";
        } else {
            return "Unknown Weapon";
        }
    }
    
    private void updateWeaponImage(Weapon weapon) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(weapon.appearanceImagePath));
            Image resizedImage = originalImage.getScaledInstance(64, 48, Image.SCALE_SMOOTH);
            weaponImageLabel.setIcon(new ImageIcon(resizedImage));
        } catch (IOException e) {
            System.err.println("Could not load weapon image: " + weapon.appearanceImagePath);
        }
    }
    
    private JLabel createLabelWithIcon(String text, String iconPath, Color textColor) {
        return createLabelWithIcon(text, iconPath, textColor, 24, 24);
    }
    
    private JLabel createLabelWithIcon(String text, String iconPath, Color textColor, int iconWidth, int iconHeight) {
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        
        try {
            Image img = ImageIO.read(new File(iconPath));
            Image resizedImg = img.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(resizedImg));
            
            label.setIconTextGap(8);
        } catch (IOException e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        
        return label;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }
}
