import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.RenderingHints;

public class MainMenuPanel extends JPanel {
    private final int PANEL_WIDTH = GameFrame.WIDTH;
    private final int PANEL_HEIGHT = GameFrame.HEIGHT;
    private final String backgroundImagePath = "assets/Backgrounds/menu_background.png";

    protected GameInfo gameInfo;
    private CharacterSelectionPanel characterSelectionPanel;
    private JPanel mainMenuContentPanel;
    protected boolean startGameAfterSelection = false;
    private ControlsPanel controlsPanel;
    private Image backgroundImage;

    public MainMenuPanel(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        try {
            backgroundImage = ImageIO.read(new File(backgroundImagePath));
        } catch (IOException e) {
            System.err.println("Could not load background image: " + backgroundImagePath);
            setBackground(new Color(32, 32, 32));
        }

        // Create main content panel
        mainMenuContentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 0));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        mainMenuContentPanel.setLayout(null);
        mainMenuContentPanel.setBounds(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        mainMenuContentPanel.setOpaque(false);

        // Main menu text with shadow
        JLabel mainMenuLabel = new JLabel(GameFrame.GAME_TITLE) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(getText(), 4, fm.getAscent() + 4);
                
                // Draw text
                g2d.setColor(getForeground());
                g2d.drawString(getText(), 0, fm.getAscent());
                
                g2d.dispose();
            }
        };
        mainMenuLabel.setForeground(Color.WHITE);
        mainMenuLabel.setFont(new Font("Impact", Font.BOLD, 72));
        mainMenuLabel.setHorizontalAlignment(SwingConstants.LEFT);
        mainMenuLabel.setBounds(50, PANEL_HEIGHT/4, PANEL_WIDTH - 100, 80);
        mainMenuContentPanel.add(mainMenuLabel);

        // Start button
        JButton startButton = createTransparentButtonWithIcon("Start New Game", "assets/Icons/play-button.png");
        startButton.setBounds(50, PANEL_HEIGHT/2 + 30, 350, 60);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCharacterSelectionForStart();
            }
        });
        mainMenuContentPanel.add(startButton);

        // Load Game button
        JButton loadButton = createTransparentButtonWithIcon("Load Game", "assets/Icons/load.png");
        loadButton.setBounds(50, PANEL_HEIGHT/2 + 100, 250, 60);
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadGame();
            }
        });
        mainMenuContentPanel.add(loadButton);
        
        // Controls button
        JButton controlsButton = createTransparentButtonWithIcon("Controls", "assets/Icons/retro-controller.png");
        controlsButton.setBounds(50, PANEL_HEIGHT/2 + 170, 250, 60);
        controlsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showControlsScreen();
            }
        });
        mainMenuContentPanel.add(controlsButton);
        
        // Exit button
        JButton exitButton = createTransparentButtonWithIcon("Exit Game", "assets/Icons/power-button.png");
        exitButton.setBounds(50, PANEL_HEIGHT/2 + 240, 250, 60);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitGame();
            }
        });
        mainMenuContentPanel.add(exitButton);

        add(mainMenuContentPanel);

        characterSelectionPanel = new CharacterSelectionPanel(gameInfo);
        characterSelectionPanel.setBounds(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        characterSelectionPanel.setVisible(false);
        add(characterSelectionPanel);

        controlsPanel = new ControlsPanel(this);
        controlsPanel.setBounds(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        controlsPanel.setVisible(false);
        add(controlsPanel);

        setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (backgroundImage != null) {
            int imgWidth = backgroundImage.getWidth(this);
            int imgHeight = backgroundImage.getHeight(this);
            
            // Calculate scaling factor to cover the entire panel
            double scale = Math.max(
                (double) getWidth() / imgWidth,
                (double) getHeight() / imgHeight
            );
            
            // Calculate new dimensions
            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);
            
            // Center the scaled image (excess will be cropped)
            int x = (getWidth() - scaledWidth) / 2;
            int y = (getHeight() - scaledHeight) / 2;
            
            g.drawImage(backgroundImage, x, y, scaledWidth, scaledHeight, this);
        }
    }

    private void showCharacterSelectionForStart() {
        startGameAfterSelection = true;
        mainMenuContentPanel.setVisible(false);
        characterSelectionPanel.setVisible(true);
    }

    private JButton createTransparentButtonWithIcon(String text, String iconPath) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Draw semi-transparent background
                g2d.setColor(new Color(40, 40, 40, 160));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Get the icon and text position
                ImageIcon icon = (ImageIcon) getIcon();
                FontMetrics fm = g2d.getFontMetrics();
                int iconTextGap = getIconTextGap();
                int iconWidth = icon != null ? icon.getIconWidth() : 0;
                
                int x = 20;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                
                // Draw icon if exists
                if (icon != null) {
                    int iconY = (getHeight() - icon.getIconHeight()) / 2;
                    icon.paintIcon(this, g2d, x, iconY);
                    x += iconWidth + iconTextGap;
                }
                
                // Draw text shadow
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.drawString(getText(), x + 2, textY + 2);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), x, textY);
                
                g2d.dispose();
            }
        };
        button.setFont(new Font("Courier New", Font.BOLD, 28));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setOpaque(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        try {
            Image img = ImageIO.read(new File(iconPath));
            Image resizedImg = img.getScaledInstance(36, 36, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(resizedImg));
            
            button.setHorizontalTextPosition(JButton.RIGHT);
            button.setIconTextGap(12);
        } catch (IOException e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        
        return button;
    }

    public void showMainMenuContent() {
        mainMenuContentPanel.setVisible(true);
        characterSelectionPanel.setVisible(false);
    }

    void startGame() {
        gameInfo.restartGame();
        setVisible(false);
        gameInfo.gameOverPanel.setVisible(false);
        gameInfo.pauseGamePanel.setVisible(false);
        gameInfo.statPanel.setVisible(true);
        gameInfo.gamePanel.setVisible(true);
        gameInfo.gamePanel.requestFocus();
    }

    private void exitGame() {
        System.exit(0);
    }

    private void loadGame() {
        boolean success = gameInfo.loadGame();
        
        if (success) {
            setVisible(false);
            gameInfo.gameOverPanel.setVisible(false);
            gameInfo.pauseGamePanel.setVisible(false);
            gameInfo.statPanel.setVisible(true);
            gameInfo.gamePanel.setVisible(true);
            
            // Start game timers
            if (gameInfo.gameTimer != null && !gameInfo.gameTimer.isRunning()) {
                gameInfo.gameTimer.start();
            }
            if (gameInfo.zombieSpawnTimer != null && !gameInfo.zombieSpawnTimer.isRunning()) {
                gameInfo.zombieSpawnTimer.start();
            }
            
            gameInfo.gamePanel.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, 
                "No saved game found or failed to load game!", 
                "Load Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showControlsScreen() {
        mainMenuContentPanel.setVisible(false);
        controlsPanel.setVisible(true);
    }
}
