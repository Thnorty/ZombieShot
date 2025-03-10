import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class PauseGamePanel extends JPanel {
    private GameInfo gameInfo;
    private final int PANEL_WIDTH = GameFrame.WIDTH;
    private final int PANEL_HEIGHT = GameFrame.HEIGHT;
    
    private JLabel pauseLabel;
    private JButton resumeButton;
    private JButton mainMenuButton;
    private JButton exitButton;
    
    public PauseGamePanel(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        setOpaque(false);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    resumeGame();
                }
            }
        });
        
        // Pause title
        pauseLabel = new JLabel("GAME PAUSED");
        pauseLabel.setForeground(Color.WHITE);
        pauseLabel.setFont(new Font("Impact", Font.BOLD, 72));
        pauseLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pauseLabel.setBounds(0, PANEL_HEIGHT/4, PANEL_WIDTH, 80);
        add(pauseLabel);
        
        // Create UI components
        int buttonWidth = 250;
        int buttonHeight = 60;
        int buttonY = PANEL_HEIGHT/2;
        int buttonGap = 80;
        
        // Resume button
        resumeButton = createButtonWithIcon("Resume Game", "assets/Icons/play-button.png");
        resumeButton.setBounds(PANEL_WIDTH/2 - buttonWidth/2, buttonY, buttonWidth, buttonHeight);
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resumeGame();
            }
        });
        add(resumeButton);
        
        // Add Save Game button
        JButton saveGameButton = createButtonWithIcon("Save Game", "assets/Icons/save.png");
        saveGameButton.setBounds(PANEL_WIDTH/2 - buttonWidth/2, buttonY + buttonGap, buttonWidth, buttonHeight);
        saveGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGame();
            }
        });
        add(saveGameButton);
        
        // Main menu button - adjust position to account for new Save Game button
        mainMenuButton = createButtonWithIcon("Main Menu", "assets/Icons/house.png");
        mainMenuButton.setBounds(PANEL_WIDTH/2 - buttonWidth/2, buttonY + buttonGap*2, buttonWidth, buttonHeight);
        mainMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToMainMenu();
            }
        });
        add(mainMenuButton);
        
        // Exit button - adjust position to account for new Save Game button
        exitButton = createButtonWithIcon("Exit Game", "assets/Icons/power-button.png");
        exitButton.setBounds(PANEL_WIDTH/2 - buttonWidth/2, buttonY + buttonGap*3, buttonWidth, buttonHeight);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitGame();
            }
        });
        add(exitButton);
        
        setVisible(false);
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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        setBackground(new Color(32, 32, 32));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
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