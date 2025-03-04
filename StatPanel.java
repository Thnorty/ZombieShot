import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;


public class StatPanel extends JPanel {
    public static final int WIDTH = GameFrame.WIDTH;
    public static final int HEIGHT = 75;

    private GameInfo gameInfo;
    private String ammoText = "Ammo: ";
    private String ammoClipText = "Ammo Clip: ";
    private String waveText = "Wave: ";
    private String zombiesRemainingText = "Zombies: ";
    private String healthText = "Health: ";
    private JLabel ammoLabel;
    private JLabel ammoClipLabel;
    private JLabel waveLabel;
    private JLabel zombiesRemainingLabel;
    private JLabel healthLabel;
    private JLabel healthPercentLabel;
    private JLabel weaponNameLabel;
    private JLabel weaponImageLabel;
    private JProgressBar healthBar;

    public StatPanel(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        setBounds(0, 0, WIDTH, HEIGHT);
        setBackground(Color.BLACK);
        setLayout(null);

        weaponNameLabel = new JLabel(getWeaponName(gameInfo.player.currentWeapon));
        weaponNameLabel.setForeground(Color.YELLOW);
        weaponNameLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        weaponNameLabel.setBounds(10, 10, 200, 25);
        add(weaponNameLabel);
        
        weaponImageLabel = new JLabel();
        updateWeaponImage(gameInfo.player.currentWeapon);
        weaponImageLabel.setBounds(210, 10, 64, 55);
        weaponImageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(weaponImageLabel);

        // Move ammo labels to the right
        ammoLabel = createLabelWithIcon(
            ammoText + gameInfo.player.currentWeapon.currentAmmo,
            "assets/Icons/bullets.png",
            Color.WHITE
        );
        ammoLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        ammoLabel.setBounds(290, 10, 200, 25);
        add(ammoLabel);

        ammoClipLabel = createLabelWithIcon(
            ammoClipText + gameInfo.player.currentWeapon.currentTotalAmmo,
            "assets/Icons/gun-magazine.png",
            Color.WHITE
        );
        ammoClipLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        ammoClipLabel.setBounds(290, 40, 400, 25);
        add(ammoClipLabel);

        waveLabel = new JLabel(waveText + gameInfo.currentWave);
        waveLabel.setForeground(Color.RED);
        waveLabel.setFont(new Font("Courier New", Font.BOLD, 30));
        waveLabel.setHorizontalAlignment(SwingConstants.CENTER);
        waveLabel.setBounds(WIDTH/2 - 100, 15, 200, 40);
        add(waveLabel);

        zombiesRemainingLabel = createLabelWithIcon(
            zombiesRemainingText + gameInfo.zombies.size(),
            "assets/Icons/shambling-zombie.png",
            Color.GREEN
        );
        zombiesRemainingLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        zombiesRemainingLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        zombiesRemainingLabel.setBounds(WIDTH - 400, 40, 200, 35);
        add(zombiesRemainingLabel);
        
        healthLabel = createLabelWithIcon(
            healthText,
            "assets/Icons/health-normal.png",
            Color.RED
        );
        healthLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        healthLabel.setBounds(WIDTH - 390, 10, 150, 35);
        add(healthLabel);
        
        healthBar = new JProgressBar(0, 100);
        healthBar.setValue((int)((gameInfo.player.health / Player.PLAYER_HEALTH) * 100));
        healthBar.setForeground(Color.RED);
        healthBar.setBounds(WIDTH - 260, 10, 200, 35);
        add(healthBar);
        
        healthPercentLabel = new JLabel((int)((gameInfo.player.health / Player.PLAYER_HEALTH) * 100) + "%");
        healthPercentLabel.setForeground(Color.RED);
        healthPercentLabel.setFont(new Font("Courier New", Font.BOLD, 16));
        healthPercentLabel.setBounds(WIDTH - 50, 10, 50, 35);
        add(healthPercentLabel);

        setVisible(false);
    }

    public void update() {
        Weapon weapon = gameInfo.player.currentWeapon;
        weaponNameLabel.setText(getWeaponName(weapon));
        updateWeaponImage(weapon);
        ammoLabel.setText(ammoText + weapon.currentAmmo);
        ammoClipLabel.setText(ammoClipText + (weapon instanceof Pistol ? "∞" : weapon.currentTotalAmmo));
        zombiesRemainingLabel.setText(zombiesRemainingText + gameInfo.zombies.size());
        waveLabel.setText(waveText + gameInfo.currentWave);

        int healthValue = (int)((gameInfo.player.health / Player.PLAYER_HEALTH) * 100);
        healthBar.setValue(healthValue);
        healthPercentLabel.setText(healthValue + "%");
        
        // Change health bar and percentage text color based on health level
        Color healthColor;
        if (healthValue > 66) {
            healthColor = Color.GREEN;
        } else if (healthValue > 33) {
            healthColor = Color.ORANGE;
        } else {
            healthColor = Color.RED;
        }
        
        healthBar.setForeground(healthColor);
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
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        
        try {
            Image img = ImageIO.read(new File(iconPath));
            Image resizedImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
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
