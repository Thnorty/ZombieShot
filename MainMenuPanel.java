import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.RenderingHints;

public class MainMenuPanel extends JPanel {
    private final int PANEL_WIDTH = GameFrame.WIDTH;
    private final int PANEL_HEIGHT = GameFrame.HEIGHT;

    protected GameInfo gameInfo;
    private CharacterSelectionPanel characterSelectionPanel;
    private JPanel mainMenuContentPanel;
    protected boolean startGameAfterSelection = false;
    private ControlsPanel controlsPanel;
    private Image backgroundImage;
    private boolean startInHardMode = false;

    public MainMenuPanel(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        if (gameInfo.backgroundImage != null) {
            backgroundImage = gameInfo.backgroundImage;
        } else {
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

        // Add Start New Game button
        JButton startButton = UIUtils.createTransparentButtonWithIcon("Start New Game", "assets/Icons/play-button.png");
        startButton.setBounds(50, PANEL_HEIGHT/2 + 30, 350, 60);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startInHardMode = false;
                showCharacterSelectionForStart();
            }
        });
        mainMenuContentPanel.add(startButton);

        // Add Start New Game+ button
        JButton hardModeButton = UIUtils.createTransparentButtonWithIcon("Start New Game+", "assets/Icons/crowned-skull.png");
        hardModeButton.setBounds(50, PANEL_HEIGHT/2 + 100, 350, 60);
        hardModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startInHardMode = true;
                showCharacterSelectionForStart();
            }
        });
        mainMenuContentPanel.add(hardModeButton);

        // Load Game button
        JButton loadButton = UIUtils.createTransparentButtonWithIcon("Load Game", "assets/Icons/load.png");
        loadButton.setBounds(50, PANEL_HEIGHT/2 + 170, 250, 60);
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadGame();
            }
        });
        mainMenuContentPanel.add(loadButton);
        
        // Controls button
        JButton controlsButton = UIUtils.createTransparentButtonWithIcon("Settings", "assets/Icons/retro-controller.png");
        controlsButton.setBounds(50, PANEL_HEIGHT/2 + 240, 250, 60);
        controlsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showControlsScreen();
            }
        });
        mainMenuContentPanel.add(controlsButton);
        
        // Exit button
        JButton exitButton = UIUtils.createTransparentButtonWithIcon("Exit Game", "assets/Icons/power-button.png");
        exitButton.setBounds(50, PANEL_HEIGHT/2 + 310, 250, 60);
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

    public void showMainMenuContent() {
        mainMenuContentPanel.setVisible(true);
        characterSelectionPanel.setVisible(false);
    }

    void startGame() {
        // Set difficulty before restarting
        if (startInHardMode) {
            gameInfo.setDifficulty(GameInfo.GameDifficulty.HARD);
        } else {
            gameInfo.setDifficulty(GameInfo.GameDifficulty.NORMAL);
        }
        
        gameInfo.restartGame();
        
        setVisible(false);
        gameInfo.gameOverPanel.setVisible(false);
        gameInfo.pauseGamePanel.setVisible(false);
        gameInfo.statPanel.setVisible(true);
        gameInfo.gamePanel.setVisible(true);
        gameInfo.gamePanel.requestFocus();
    }

    private void exitGame() {
        MusicPlayer.dispose();
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
