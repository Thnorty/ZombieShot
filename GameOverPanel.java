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

public class GameOverPanel extends JPanel {
    private GameInfo gameInfo;
    private final int PANEL_WIDTH = GameFrame.WIDTH;
    private final int PANEL_HEIGHT = GameFrame.HEIGHT;

    private String scoreText = "You survived %d waves, killed %d zombies and scored %d points";
    private String restartGameText = "Play Again";
    private String exitGameText = "Exit Game";
    private JLabel gameOverLabel;
    private JLabel scoreLabel;
    private JButton restartButton;
    private JButton exitButton;

    public GameOverPanel(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(32, 32, 32));
        setLayout(null);
        setVisible(false);

        // Game Over text
        gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setForeground(Color.RED);
        gameOverLabel.setFont(new Font("Impact", Font.BOLD, 72));
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLabel.setBounds(0, PANEL_HEIGHT/4, PANEL_WIDTH, 80);
        add(gameOverLabel);
        
        // Score display
        scoreLabel = new JLabel(String.format(scoreText, gameInfo.currentWave-1, gameInfo.zombiesKilled, gameInfo.player.score));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Courier New", Font.BOLD, 28));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setBounds(0, PANEL_HEIGHT/4 + 100, PANEL_WIDTH, 40);
        add(scoreLabel);
        
        // Restart button
        restartButton = createButtonWithIcon(restartGameText, "assets/Icons/cycle.png");
        restartButton.setBounds(PANEL_WIDTH/2 - 125, PANEL_HEIGHT/2 + 50, 250, 60);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameInfo.restartGame();
            }
        });
        add(restartButton);
        
        // Exit button
        exitButton = createButtonWithIcon(exitGameText, "assets/Icons/power-button.png");
        exitButton.setBounds(PANEL_WIDTH/2 - 125, PANEL_HEIGHT/2 + 130, 250, 60);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        add(exitButton);
    }

    private JButton createButtonWithIcon(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Courier New", Font.BOLD, 24));
        
        try {
            Image img = ImageIO.read(new File(iconPath));
            Image resizedImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(resizedImg));
            
            button.setHorizontalTextPosition(JButton.RIGHT);
            button.setIconTextGap(10);
        } catch (IOException e) {
            System.err.println("Could not load icon: " + iconPath);
        }
        
        return button;
    }

    public void updateStats() {
        scoreLabel.setText(String.format(scoreText, gameInfo.currentWave-1, gameInfo.zombiesKilled, gameInfo.player.score));
        repaint();
    }
}