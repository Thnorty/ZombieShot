import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.RenderingHints;
import java.awt.FontMetrics;

public class PauseGamePanel extends JPanel {
    private GameInfo gameInfo;
    private final int PANEL_WIDTH = GameFrame.WIDTH;
    private final int PANEL_HEIGHT = GameFrame.HEIGHT;
    private Image backgroundImage;
    
    private JLabel pauseLabel;
    private JButton resumeButton;
    private JButton mainMenuButton;
    private JButton exitButton;
    
    public PauseGamePanel(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        
        if (gameInfo.backgroundImage != null) {
            backgroundImage = gameInfo.backgroundImage;
        }
        
        setOpaque(false);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    resumeGame();
                }
            }
        });
        
        // Pause Title
        pauseLabel = new JLabel("GAME PAUSED") {
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
        pauseLabel.setForeground(Color.WHITE);
        pauseLabel.setFont(new Font("Impact", Font.BOLD, 72));
        pauseLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pauseLabel.setBounds(50, PANEL_HEIGHT/4, PANEL_WIDTH, 80);
        add(pauseLabel);
        
        // Left-align all buttons
        int leftMargin = 50;
        int buttonWidth = 250;
        int buttonHeight = 60;
        int buttonY = PANEL_HEIGHT/2;
        int buttonGap = 80;
        
        // Resume button
        resumeButton = UIUtils.createTransparentButtonWithIcon("Resume Game", "assets/Icons/play-button.png");
        resumeButton.setBounds(leftMargin, buttonY, buttonWidth + 40, buttonHeight);
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resumeGame();
            }
        });
        add(resumeButton);
        
        // Add Save Game button
        JButton saveGameButton = UIUtils.createTransparentButtonWithIcon("Save Game", "assets/Icons/save.png");
        saveGameButton.setBounds(leftMargin, buttonY + buttonGap, buttonWidth, buttonHeight);
        saveGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGame();
            }
        });
        add(saveGameButton);
        
        // Main menu button
        mainMenuButton = UIUtils.createTransparentButtonWithIcon("Main Menu", "assets/Icons/house.png");
        mainMenuButton.setBounds(leftMargin, buttonY + buttonGap*2, buttonWidth, buttonHeight);
        mainMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToMainMenu();
            }
        });
        add(mainMenuButton);
        
        // Exit button
        exitButton = UIUtils.createTransparentButtonWithIcon("Exit Game", "assets/Icons/power-button.png");
        exitButton.setBounds(leftMargin, buttonY + buttonGap*3, buttonWidth, buttonHeight);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitGame();
            }
        });
        add(exitButton);
        
        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        
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
            
            // Draw with reduced alpha for the background
            g2d.drawImage(backgroundImage, x, y, scaledWidth, scaledHeight, this);
        }
        
        g2d.dispose();
        super.paintComponent(g);
    }
    
    public void resumeGame() {
        setVisible(false);
        gameInfo.gamePanel.requestFocus();
        gameInfo.gamePanel.resumeGame();
    }
    
    private void goToMainMenu() {
        setVisible(false);
        gameInfo.gamePanel.setVisible(false);
        gameInfo.statPanel.setVisible(false);
        gameInfo.mainMenuPanel.setVisible(true);
        
        // Stop all game timers
        if (gameInfo.gameTimer != null && gameInfo.gameTimer.isRunning()) {
            gameInfo.gameTimer.stop();
        }
        if (gameInfo.zombieSpawnTimer != null && gameInfo.zombieSpawnTimer.isRunning()) {
            gameInfo.zombieSpawnTimer.stop();
        }
    }
    
    private void exitGame() {
        MusicPlayer.dispose();
        System.exit(0);
    }
    
    private void saveGame() {
        boolean success = gameInfo.saveGame();
        
        if (success) {
            // Show a message that game was saved
            JOptionPane.showMessageDialog(this, 
                "Game saved successfully!", 
                "Save Complete", 
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            // Show error message if save failed
            JOptionPane.showMessageDialog(this, 
                "Failed to save game!", 
                "Save Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }

        this.requestFocus();
    }
}