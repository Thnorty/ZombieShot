import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class ControlsPanel extends JPanel {
    private final int PANEL_WIDTH = GameFrame.WIDTH;
    private final int PANEL_HEIGHT = GameFrame.HEIGHT;
    private JButton backButton;
    private MainMenuPanel parentPanel;
    private GameInfo gameInfo;
    private HashMap<String, JLabel> keyLabels = new HashMap<>();
    private String awaitingRebind = null;
    private Image backgroundImage;

    public ControlsPanel(MainMenuPanel parentPanel) {
        this.parentPanel = parentPanel;
        this.gameInfo = parentPanel.gameInfo;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        setFocusable(true);
        
        if (gameInfo.backgroundImage != null) {
            backgroundImage = gameInfo.backgroundImage;
        } else {
            setBackground(new Color(32, 32, 32));
        }

        // Title
        JLabel titleLabel = new JLabel("GAME SETTINGS");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Impact", Font.BOLD, 72));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 50, PANEL_WIDTH, 80);
        add(titleLabel);

        // Create controls info panel
        JPanel controlsInfoPanel = createControlsInfoPanel();
        controlsInfoPanel.setBounds(PANEL_WIDTH/2 - 450, 150, 900, 850);
        add(controlsInfoPanel);

        // Back button - left-aligned like MainMenuPanel
        int leftMargin = 50;
        backButton = UIUtils.createTransparentButtonWithIcon("Back to Menu", "assets/Icons/house.png");
        backButton.setBounds(leftMargin, PANEL_HEIGHT - 100, 300, 60);
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

    private JPanel createControlsInfoPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(40, 40, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Movement section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        mainPanel.add(createSectionPanel("Movement", new String[][]{
            {"moveUp", "Move Up"},
            {"moveDown", "Move Down"},
            {"moveLeft", "Move Left"},
            {"moveRight", "Move Right"}
        }), gbc);

        // Combat section
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(createSectionPanel("Combat", new String[][]{
            {"reload", "Reload Weapon"}
        }), gbc);

        // Weapon Selection section
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(createSectionPanel("Weapon Selection", new String[][]{
            {"weapon1", "Pistol"},
            {"weapon2", "Rifle"},
            {"weapon3", "Shotgun"},
            {"weapon4", "Sniper"},
            {"weapon5", "Rocket Launcher"}
        }), gbc);

        // Game Controls section
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(createSectionPanel("Game Controls", new String[][]{
            {"pause", "Pause Game"},
            {"toggleFPS", "Toggle FPS Display"},
            {"debug", "Toggle Debug Mode"}
        }), gbc);
        
        // Audio Controls section
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(createAudioControlsPanel(), gbc);

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
            JButton rebindButton = UIUtils.createTransparentButton("Rebind", 16);
            rebindButton.setPreferredSize(new Dimension(80, 36));
            rebindButton.setActionCommand(action);
            rebindButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    awaitingRebind = e.getActionCommand();
                    keyLabel.setText("Press key...");
                    updateRebindButtonStates();
                    requestFocus();
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
            gameInfo.saveSettings();
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

    private void goBack() {
        // Finalize any pending key rebinds
        awaitingRebind = null;
        setVisible(false);
        parentPanel.showMainMenuContent();
    }

    private JPanel createAudioControlsPanel() {
        JPanel audioPanel = new JPanel();
        audioPanel.setLayout(new BorderLayout(10, 10));
        audioPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2),
            BorderFactory.createEmptyBorder(10, 15, 15, 15)
        ));
        audioPanel.setBackground(new Color(50, 50, 50));

        // Title
        JLabel titleLabel = new JLabel("Audio Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        audioPanel.add(titleLabel, BorderLayout.NORTH);

        // Content panel
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new GridLayout(3, 1, 0, 20));
        controlsPanel.setBackground(new Color(50, 50, 50));

        // Music Volume slider row
        JPanel musicVolumePanel = new JPanel();
        musicVolumePanel.setLayout(new BorderLayout(10, 0));
        musicVolumePanel.setBackground(new Color(50, 50, 50));

        JLabel musicVolumeLabel = new JLabel("Music Volume:");
        musicVolumeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        musicVolumeLabel.setForeground(Color.WHITE);
        musicVolumePanel.add(musicVolumeLabel, BorderLayout.WEST);

        // Current volume value as percentage
        JLabel musicVolumeValueLabel = new JLabel(Math.round(MusicPlayer.getMusicVolume() * 100) + "%");
        musicVolumeValueLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        musicVolumeValueLabel.setForeground(Color.WHITE);
        musicVolumeValueLabel.setPreferredSize(new Dimension(50, 30));
        musicVolumePanel.add(musicVolumeValueLabel, BorderLayout.EAST);

        // Slider
        JSlider musicVolumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(MusicPlayer.getMusicVolume() * 100));
        musicVolumeSlider.setMajorTickSpacing(25);
        musicVolumeSlider.setMinorTickSpacing(5);
        musicVolumeSlider.setPaintTicks(true);
        musicVolumeSlider.setBackground(new Color(50, 50, 50));
        musicVolumeSlider.setForeground(Color.WHITE);
        musicVolumeSlider.addChangeListener(e -> {
            int value = musicVolumeSlider.getValue();
            float volumeValue = value / 100f;
            MusicPlayer.setMusicVolume(volumeValue);
            musicVolumeValueLabel.setText(value + "%");
            gameInfo.saveSettings();
        });
        musicVolumePanel.add(musicVolumeSlider, BorderLayout.CENTER);
        
        // Sound Effects Volume slider row
        JPanel sfxVolumePanel = new JPanel();
        sfxVolumePanel.setLayout(new BorderLayout(10, 0));
        sfxVolumePanel.setBackground(new Color(50, 50, 50));

        JLabel sfxVolumeLabel = new JLabel("SFX Volume:");
        sfxVolumeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        sfxVolumeLabel.setForeground(Color.WHITE);
        sfxVolumePanel.add(sfxVolumeLabel, BorderLayout.WEST);

        // Current SFX volume value as percentage
        JLabel sfxVolumeValueLabel = new JLabel(Math.round(MusicPlayer.getSfxVolume() * 100) + "%");
        sfxVolumeValueLabel.setFont(new Font("Courier New", Font.BOLD, 18));
        sfxVolumeValueLabel.setForeground(Color.WHITE);
        sfxVolumeValueLabel.setPreferredSize(new Dimension(50, 30));
        sfxVolumePanel.add(sfxVolumeValueLabel, BorderLayout.EAST);

        // SFX Slider
        JSlider sfxVolumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(MusicPlayer.getSfxVolume() * 100));
        sfxVolumeSlider.setMajorTickSpacing(25);
        sfxVolumeSlider.setMinorTickSpacing(5);
        sfxVolumeSlider.setPaintTicks(true);
        sfxVolumeSlider.setBackground(new Color(50, 50, 50));
        sfxVolumeSlider.setForeground(Color.WHITE);
        sfxVolumeSlider.addChangeListener(e -> {
            int value = sfxVolumeSlider.getValue();
            float volumeValue = value / 100f;
            MusicPlayer.setSfxVolume(volumeValue);
            sfxVolumeValueLabel.setText(value + "%");
            gameInfo.saveSettings();
        });
        sfxVolumePanel.add(sfxVolumeSlider, BorderLayout.CENTER);

        controlsPanel.add(musicVolumePanel);
        controlsPanel.add(sfxVolumePanel);
        audioPanel.add(controlsPanel, BorderLayout.CENTER);

        return audioPanel;
    }
}