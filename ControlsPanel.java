import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ControlsPanel extends JPanel {
    private final int PANEL_WIDTH = GameFrame.WIDTH;
    private final int PANEL_HEIGHT = GameFrame.HEIGHT;
    private JButton backButton;
    private MainMenuPanel parentPanel;
    private GameInfo gameInfo;
    private HashMap<String, JLabel> keyLabels = new HashMap<>();
    private String awaitingRebind = null;

    public ControlsPanel(MainMenuPanel parentPanel) {
        this.parentPanel = parentPanel;
        this.gameInfo = parentPanel.gameInfo;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(32, 32, 32));
        setLayout(null);
        setFocusable(true);

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

        // Key input listener for rebinding
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (awaitingRebind != null) {
                    // Update the key binding
                    gameInfo.setKeyBinding(awaitingRebind, e.getKeyCode());
                    
                    // Update the label
                    JLabel keyLabel = keyLabels.get(awaitingRebind);
                    if (keyLabel != null) {
                        keyLabel.setText(KeyEvent.getKeyText(e.getKeyCode()));
                    }
                    
                    // Reset the awaiting state
                    awaitingRebind = null;
                    
                    // Update the visual state of all rebinding buttons
                    updateRebindButtonStates();
                }
            }
        });
    }

    private JPanel createControlsInfoPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 20, 20)); // 2x2 grid with gaps
        mainPanel.setBackground(new Color(40, 40, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create each control section
        mainPanel.add(createSectionPanel("Movement", new String[][]{
            {"moveUp", "Move Up"},
            {"moveDown", "Move Down"},
            {"moveLeft", "Move Left"},
            {"moveRight", "Move Right"}
        }));
        
        mainPanel.add(createSectionPanel("Combat", new String[][]{
            {"reload", "Reload Weapon"}
        }));
        
        mainPanel.add(createSectionPanel("Weapon Selection", new String[][]{
            {"weapon1", "Pistol"},
            {"weapon2", "Rifle"},
            {"weapon3", "Shotgun"},
            {"weapon4", "Sniper"},
            {"weapon5", "Rocket Launcher"}
        }));
        
        mainPanel.add(createSectionPanel("Game Controls", new String[][]{
            {"pause", "Pause Game"},
            {"debug", "Toggle Debug Mode"}
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

            // Create key label that shows the current binding
            String action = control[0];
            int keyCode = gameInfo.getKeyBinding(action);
            JLabel keyLabel = new JLabel(KeyEvent.getKeyText(keyCode));
            keyLabel.setFont(new Font("Courier New", Font.BOLD, 18));
            keyLabel.setForeground(Color.WHITE);
            keyLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            
            // Store the label for later updates
            keyLabels.put(action, keyLabel);
            
            // Ensure key label has consistent width
            keyLabel.setPreferredSize(new Dimension(140, 36));
            
            // Add rebind button
            JButton rebindButton = new JButton("Rebind");
            rebindButton.setPreferredSize(new Dimension(80, 36));
            rebindButton.setActionCommand(action);
            rebindButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    awaitingRebind = e.getActionCommand();
                    keyLabel.setText("Press key...");
                    updateRebindButtonStates();
                    requestFocus(); // Get focus to receive key events
                }
            });
            
            JLabel actionLabel = new JLabel(control[1]);
            actionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            actionLabel.setForeground(Color.WHITE);

            // Use a panel to hold key label and rebind button
            JPanel keyBindPanel = new JPanel(new BorderLayout(5, 0));
            keyBindPanel.setBackground(new Color(50, 50, 50));
            keyBindPanel.add(keyLabel, BorderLayout.CENTER);
            keyBindPanel.add(rebindButton, BorderLayout.EAST);
            
            controlRow.add(keyBindPanel, BorderLayout.WEST);
            controlRow.add(actionLabel, BorderLayout.CENTER);

            controlsContent.add(controlRow);
        }

        sectionPanel.add(controlsContent, BorderLayout.CENTER);
        return sectionPanel;
    }
    
    private void updateRebindButtonStates() {
        Component[] components = getComponents();
        for (Component c : components) {
            if (c instanceof JPanel) {
                updateButtonsInContainer((Container)c);
            }
        }
        
        // Save keybindings when changes are made
        if (gameInfo != null && awaitingRebind == null) {
            gameInfo.saveKeyBindings();
        }
    }
    
    private void updateButtonsInContainer(Container container) {
        Component[] components = container.getComponents();
        for (Component c : components) {
            if (c instanceof JButton) {
                JButton button = (JButton)c;
                if (button.getText().equals("Rebind")) {
                    button.setEnabled(awaitingRebind == null);
                }
            } else if (c instanceof Container) {
                updateButtonsInContainer((Container)c);
            }
        }
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
        // Finalize any pending key rebinds
        awaitingRebind = null;
        setVisible(false);
        parentPanel.showMainMenuContent();
    }
}