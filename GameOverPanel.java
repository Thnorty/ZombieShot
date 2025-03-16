import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GameOverPanel extends JPanel {
    private GameInfo gameInfo;
    private final int PANEL_WIDTH = GameFrame.WIDTH;
    private final int PANEL_HEIGHT = GameFrame.HEIGHT;
    private Image backgroundImage;

    private String scoreText = "You survived %d waves, killed %d zombies and scored %d points";
    private String restartGameText = "Play Again";
    private String loadSaveText = "Resume from Last Save";
    private String exitGameText = "Exit Game";
    private JLabel gameOverLabel;
    private JLabel scoreLabel;
    private JButton restartButton;
    private JButton loadSaveButton;
    private JButton exitButton;

    public GameOverPanel(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        setVisible(false);
        
        if (gameInfo.backgroundImage != null) {
            backgroundImage = gameInfo.backgroundImage;
        } else {
            setBackground(new Color(32, 32, 32));
        }

        // Game Over text with shadow effect
        gameOverLabel = new JLabel("GAME OVER") {
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
        gameOverLabel.setForeground(Color.RED);
        gameOverLabel.setFont(new Font("Impact", Font.BOLD, 72));
        gameOverLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gameOverLabel.setBounds(50, PANEL_HEIGHT/4, PANEL_WIDTH, 80);
        add(gameOverLabel);
        
        // Score display
        scoreLabel = new JLabel(String.format(scoreText, gameInfo.currentWave-1, gameInfo.zombiesKilled, gameInfo.player.score));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Courier New", Font.BOLD, 28));
        scoreLabel.setHorizontalAlignment(SwingConstants.LEFT);
        scoreLabel.setBounds(50, PANEL_HEIGHT/4 + 100, PANEL_WIDTH - 100, 40);
        add(scoreLabel);
        
        // Left-align all buttons like MainMenuPanel
        int leftMargin = 50;
        int buttonWidth = 250;
        
        // Restart button
        restartButton = UIUtils.createTransparentButtonWithIcon(restartGameText, "assets/Icons/cycle.png");
        restartButton.setBounds(leftMargin, PANEL_HEIGHT/2 + 50, buttonWidth, 60);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameInfo.restartGame();
            }
        });
        add(restartButton);
        
        // Load save button
        loadSaveButton = UIUtils.createTransparentButtonWithIcon(loadSaveText, "assets/Icons/load.png");
        loadSaveButton.setBounds(leftMargin, PANEL_HEIGHT/2 + 120, buttonWidth + 200, 60);
        loadSaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadLastSave();
            }
        });
        add(loadSaveButton);
        
        // Exit button
        exitButton = UIUtils.createTransparentButtonWithIcon(exitGameText, "assets/Icons/power-button.png");
        exitButton.setBounds(leftMargin, PANEL_HEIGHT/2 + 190, buttonWidth, 60);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MusicPlayer.dispose();
                System.exit(0);
            }
        });
        add(exitButton);
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

    public void updateStats() {
        scoreLabel.setText(String.format(scoreText, gameInfo.currentWave-1, gameInfo.zombiesKilled, gameInfo.player.score));
        repaint();
    }
    
    private void loadLastSave() {
        boolean success = gameInfo.loadGame();
        
        if (success) {
            setVisible(false);
            gameInfo.gamePanel.setVisible(true);
            gameInfo.statPanel.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "No saved game found or failed to load game!", 
                "Load Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}