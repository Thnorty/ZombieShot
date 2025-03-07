import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class CharacterSelectionPanel extends JPanel {
    private GameInfo gameInfo;
    private List<CharacterOption> characterOptions = new ArrayList<>();
    private int selectedIndex = 0;
    private JButton selectButton;
    private JButton backButton;
    
    public CharacterSelectionPanel(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        setLayout(null);
        setBackground(new Color(32, 32, 32));
        
        JLabel titleLabel = new JLabel("SELECT CHARACTER");
        titleLabel.setFont(new Font("Impact", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 50, GameFrame.WIDTH, 60);
        add(titleLabel);

        // Load character options
        loadCharacterOptions();

        // Create UI components
        int buttonWidth = 200;
        int buttonHeight = 50;

        selectButton = new JButton("SELECT");
        selectButton.setFont(new Font("Courier New", Font.BOLD, 20));
        selectButton.setBounds(GameFrame.WIDTH/2 - buttonWidth/2, GameFrame.HEIGHT - 150, buttonWidth, buttonHeight);
        selectButton.addActionListener(e -> selectCharacter());
        add(selectButton);

        backButton = new JButton("BACK");
        backButton.setFont(new Font("Courier New", Font.BOLD, 20));
        backButton.setBounds(GameFrame.WIDTH/2 - buttonWidth/2, GameFrame.HEIGHT - 90, buttonWidth, buttonHeight);
        backButton.addActionListener(e -> goBack());
        add(backButton);

        // Arrow buttons for character selection
        JButton leftArrow = new JButton("<");
        leftArrow.setFont(new Font("Arial", Font.BOLD, 24));
        leftArrow.setBounds(100, GameFrame.HEIGHT/2 - 25, 50, 50);
        leftArrow.addActionListener(e -> previousCharacter());
        add(leftArrow);

        JButton rightArrow = new JButton(">");
        rightArrow.setFont(new Font("Arial", Font.BOLD, 24));
        rightArrow.setBounds(GameFrame.WIDTH - 150, GameFrame.HEIGHT/2 - 25, 50, 50);
        rightArrow.addActionListener(e -> nextCharacter());
        add(rightArrow);
    }
    
    private void loadCharacterOptions() {
        // Get all character folders that match the pattern "char_XX"
        File playerDir = new File("assets/Player");
        if (!playerDir.exists() || !playerDir.isDirectory()) {
            System.err.println("Player directory not found: assets/Player");
            return;
        }
        
        // Get all subdirectories that match the pattern char_XX
        File[] characterDirs = playerDir.listFiles(file -> 
            file.isDirectory() && Pattern.matches("char_\\d+", file.getName()));
        
        if (characterDirs == null || characterDirs.length == 0) {
            System.err.println("No character directories found matching pattern 'char_XX'");
            return;
        }
        
        for (File characterDir : characterDirs) {
            try {
                // Extract character number from folder name
                String folderName = characterDir.getName();
                String numStr = folderName.substring(folderName.lastIndexOf('_') + 1);
                int characterNum = Integer.parseInt(numStr);
                
                String characterPath = characterDir.getPath();
                
                // Find the first walking animation image to display
                File walkingDir = new File(characterPath + "/walking");
                
                if (walkingDir.exists() && walkingDir.isDirectory()) {
                    File[] walkingFiles = walkingDir.listFiles((dir, name) -> 
                        name.toLowerCase().endsWith(".png"));
                    
                    if (walkingFiles != null && walkingFiles.length > 0) {
                        BufferedImage characterImg = ImageIO.read(walkingFiles[0]);
                        characterOptions.add(new CharacterOption(characterNum, characterImg));
                    } else {
                        // Try looking for any png in the character folder 
                        File[] files = characterDir.listFiles((dir, name) -> 
                            name.toLowerCase().endsWith(".png"));
                        
                        if (files != null && files.length > 0) {
                            BufferedImage characterImg = ImageIO.read(files[0]);
                            characterOptions.add(new CharacterOption(characterNum, characterImg));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading character from " + characterDir.getName() + ": " + e.getMessage());
            }
        }
        
        if (characterOptions.isEmpty()) {
            System.err.println("No characters found! Please check the assets directory structure.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw character preview
        if (!characterOptions.isEmpty()) {
            CharacterOption currentOption = characterOptions.get(selectedIndex);
        
            String characterName = "Character " + currentOption.id;
            g.setFont(new Font("Courier New", Font.BOLD, 24));
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            int nameWidth = fm.stringWidth(characterName);
            g.drawString(characterName, GameFrame.WIDTH/2 - nameWidth/2, GameFrame.HEIGHT/2 + 150);
            
            if (currentOption.image != null) {
                int maxWidth = 200;
                int maxHeight = 200;
                
                int origWidth = currentOption.image.getWidth();
                int origHeight = currentOption.image.getHeight();
                
                int newWidth, newHeight;
                
                if (origWidth > origHeight) {
                    newWidth = maxWidth;
                    newHeight = (int)(((double)origHeight / origWidth) * maxWidth);
                } else {
                    newHeight = maxHeight;
                    newWidth = (int)(((double)origWidth / origHeight) * maxHeight);
                }
                
                int x = GameFrame.WIDTH/2 - newWidth/2;
                int y = GameFrame.HEIGHT/2 - newHeight/2;
                
                g.drawImage(currentOption.image, x, y, newWidth, newHeight, null);
            }
        } else {
            // Display a message if no characters were found
            g.setFont(new Font("Courier New", Font.BOLD, 24));
            g.setColor(Color.RED);
            String message = "No character files found!";
            FontMetrics fm = g.getFontMetrics();
            int msgWidth = fm.stringWidth(message);
            g.drawString(message, GameFrame.WIDTH/2 - msgWidth/2, GameFrame.HEIGHT/2);
        }
    }
    
    private void previousCharacter() {
        if (characterOptions.isEmpty()) return;
        
        selectedIndex--;
        if (selectedIndex < 0) {
            selectedIndex = characterOptions.size() - 1;
        }
        repaint();
    }
    
    private void nextCharacter() {
        if (characterOptions.isEmpty()) return;
        
        selectedIndex++;
        if (selectedIndex >= characterOptions.size()) {
            selectedIndex = 0;
        }
        repaint();
    }
    
    public void selectCharacter() {
        if (!characterOptions.isEmpty()) {
            gameInfo.setSelectedCharacter(characterOptions.get(selectedIndex).id);
            setVisible(false);
            
            Container parent = getParent();
            if (parent instanceof MainMenuPanel) {
                MainMenuPanel mainMenu = (MainMenuPanel) parent;
                mainMenu.showMainMenuContent();
                if (mainMenu.startGameAfterSelection) {
                    mainMenu.startGame();
                    mainMenu.startGameAfterSelection = false;
                } else {
                    mainMenu.showMainMenuContent();
                }
            }
        }
    }
    
    public void goBack() {
        setVisible(false);
        
        Container parent = getParent();
        if (parent instanceof MainMenuPanel) {
            MainMenuPanel mainMenu = (MainMenuPanel) parent;
            mainMenu.showMainMenuContent();
            mainMenu.startGameAfterSelection = false;
            mainMenu.showMainMenuContent();
        }
    }
    
    private static class CharacterOption {
        int id;
        BufferedImage image;
        
        CharacterOption(int id, BufferedImage image) {
            this.id = id;
            this.image = image;
        }
    }
}