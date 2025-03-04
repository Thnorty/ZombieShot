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

public class MainMenuPanel extends JPanel {
    private final int PANEL_WIDTH = GameFrame.WIDTH;
    private final int PANEL_HEIGHT = GameFrame.HEIGHT;

    private GameInfo gameInfo;
    private CharacterSelectionPanel characterSelectionPanel;
    private JPanel mainMenuContentPanel;

    public MainMenuPanel(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(32, 32, 32));
        setLayout(null);

        // Create main content panel
        mainMenuContentPanel = new JPanel();
        mainMenuContentPanel.setLayout(null);
        mainMenuContentPanel.setBounds(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        mainMenuContentPanel.setBackground(new Color(32, 32, 32));
        mainMenuContentPanel.setOpaque(true);
        add(mainMenuContentPanel);

        // Main menu text
        JLabel mainMenuLabel = new JLabel("MAIN MENU");
        mainMenuLabel.setForeground(Color.WHITE);
        mainMenuLabel.setFont(new Font("Impact", Font.BOLD, 72));
        mainMenuLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainMenuLabel.setBounds(0, PANEL_HEIGHT/4, PANEL_WIDTH, 80);
        mainMenuContentPanel.add(mainMenuLabel);

        // Start button
        JButton startButton = createButtonWithIcon("Start Game", "assets/Icons/play-button.png");
        startButton.setBounds(PANEL_WIDTH/2 - 125, PANEL_HEIGHT/2 + 30, 250, 60);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        mainMenuContentPanel.add(startButton);

        // Character Selection button
        JButton characterButton = createButtonWithIcon("Select Character", "assets/Icons/kimono.png");
        characterButton.setBounds(PANEL_WIDTH/2 - 150, PANEL_HEIGHT/2 + 100, 300, 60);
        characterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCharacterSelection();
            }
        });
        mainMenuContentPanel.add(characterButton);

        // Exit button
        JButton exitButton = createButtonWithIcon("Exit Game", "assets/Icons/power-button.png");
        exitButton.setBounds(PANEL_WIDTH/2 - 125, PANEL_HEIGHT/2 + 170, 250, 60);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitGame();
            }
        });
        mainMenuContentPanel.add(exitButton);

        // Create character selection panel
        characterSelectionPanel = new CharacterSelectionPanel(gameInfo);
        characterSelectionPanel.setBounds(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        characterSelectionPanel.setVisible(false);
        add(characterSelectionPanel);
        
        setVisible(true);
    }

    private void showCharacterSelection() {
        mainMenuContentPanel.setVisible(false);
        characterSelectionPanel.setVisible(true);
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

    public void showMainMenuContent() {
        mainMenuContentPanel.setVisible(true);
        characterSelectionPanel.setVisible(false);
    }

    private void startGame() {
        gameInfo.restartGame();
        setVisible(false);
        gameInfo.gameOverPanel.setVisible(false);
        gameInfo.statPanel.setVisible(true);
        gameInfo.gamePanel.setVisible(true);
    }

    private void exitGame() {
        System.exit(0);
    }
}
