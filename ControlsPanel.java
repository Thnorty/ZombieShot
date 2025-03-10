import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ControlsPanel extends JPanel {
    private final int PANEL_WIDTH = GameFrame.WIDTH;
    private final int PANEL_HEIGHT = GameFrame.HEIGHT;
    private JButton backButton;
    private MainMenuPanel parentPanel;

    public ControlsPanel(MainMenuPanel parentPanel) {
        this.parentPanel = parentPanel;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(32, 32, 32));
        setLayout(null);

        // Title
        JLabel titleLabel = new JLabel("GAME CONTROLS");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Impact", Font.BOLD, 72));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 50, PANEL_WIDTH, 80);
        add(titleLabel);

        // Create controls info panel
        JPanel controlsInfoPanel = createControlsInfoPanel();
        controlsInfoPanel.setBounds(PANEL_WIDTH/2 - 450, 150, 900, 550);
        add(controlsInfoPanel);

        // Back button
        backButton = createButtonWithIcon("Back to Menu", "assets/Icons/house.png");
        backButton.setBounds(PANEL_WIDTH/2 - 150, PANEL_HEIGHT - 100, 300, 60);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });
        add(backButton);
    }

    private JPanel createControlsInfoPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 20, 20)); // 2x2 grid with gaps
        mainPanel.setBackground(new Color(40, 40, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create each control section
        mainPanel.add(createSectionPanel("Movement", new String[][]{
            {"W", "Move Up"},
            {"A", "Move Left"},
            {"S", "Move Down"},
            {"D", "Move Right"}
        }));
        
        mainPanel.add(createSectionPanel("Combat", new String[][]{
            {"Left Click", "Shoot"},
            {"R", "Reload Weapon"}
        }));
        
        mainPanel.add(createSectionPanel("Weapon Selection", new String[][]{
            {"1", "Pistol"},
            {"2", "Rifle"},
            {"3", "Shotgun"},
            {"4", "Sniper"},
            {"5", "Rocket Launcher"}
        }));
        
        mainPanel.add(createSectionPanel("Game Controls", new String[][]{
            {"ESC", "Pause Game"},
            {"F3", "Toggle Debug Mode"}
        }));

        return mainPanel;
    }

    private JPanel createSectionPanel(String title, String[][] controls) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BorderLayout());
        sectionPanel.setBackground(new Color(50, 50, 50));
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2),
            BorderFactory.createEmptyBorder(10, 15, 15, 15)
        ));

        // Section title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        sectionPanel.add(titleLabel, BorderLayout.NORTH);

        // Controls content
        JPanel controlsContent = new JPanel();
        controlsContent.setLayout(new GridLayout(controls.length, 1, 0, 5));
        controlsContent.setBackground(new Color(50, 50, 50));

        for (String[] control : controls) {
            JPanel controlRow = new JPanel();
            controlRow.setLayout(new BorderLayout(10, 0));
            controlRow.setBackground(new Color(50, 50, 50));

            JLabel keyLabel = new JLabel(control[0]);
            keyLabel.setFont(new Font("Courier New", Font.BOLD, 18));
            keyLabel.setForeground(Color.WHITE);
            keyLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            
            // Ensure key label has consistent width
            keyLabel.setPreferredSize(new Dimension(180, 36));
            
            JLabel actionLabel = new JLabel(control[1]);
            actionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            actionLabel.setForeground(Color.WHITE);

            controlRow.add(keyLabel, BorderLayout.WEST);
            controlRow.add(actionLabel, BorderLayout.CENTER);

            controlsContent.add(controlRow);
        }

        sectionPanel.add(controlsContent, BorderLayout.CENTER);
        return sectionPanel;
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

    private void goBack() {
        setVisible(false);
        parentPanel.showMainMenuContent();
    }
}