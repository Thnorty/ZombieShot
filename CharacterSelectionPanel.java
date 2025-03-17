import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class CharacterSelectionPanel extends JPanel {
    private GameInfo gameInfo;
    private List<CharacterOption> characterOptions = new ArrayList<>();
    private int selectedIndex = 0;
    private JButton selectButton;
    private JButton backButton;
    private Image backgroundImage;
    
    public CharacterSelectionPanel(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
        setLayout(null);
        if (gameInfo.backgroundImage != null) {
            backgroundImage = gameInfo.backgroundImage;
        } else {
            setBackground(new Color(32, 32, 32));
        }
        
        JLabel titleLabel = new JLabel("SELECT CHARACTER") {
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
        titleLabel.setFont(new Font("Impact", Font.BOLD, 72));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(50, 50, GameFrame.WIDTH, 80);
        add(titleLabel);

        // Load character options
        loadCharacterOptions();

        // Create UI components
        int buttonWidth = 250;
        int buttonHeight = 75;

        int leftMargin = 50;
        
        selectButton = UIUtils.createTransparentButton("SELECT");
        selectButton.setFont(new Font("Courier New", Font.BOLD, 20));
        selectButton.setBounds(GameFrame.WIDTH/2 - buttonWidth/2, GameFrame.HEIGHT - 350, buttonWidth, buttonHeight);
        selectButton.addActionListener(e -> selectCharacter());
        add(selectButton);

        backButton = UIUtils.createTransparentButton("BACK");
        backButton.setFont(new Font("Courier New", Font.BOLD, 20));
        backButton.setBounds(leftMargin, GameFrame.HEIGHT - 200, buttonWidth, buttonHeight);
        backButton.addActionListener(e -> goBack());
        add(backButton);

        int characterDisplayWidth = 200;
        int arrowButtonWidth = 60;
        int arrowButtonHeight = 60;
        int arrowButtonY = GameFrame.HEIGHT/2 - arrowButtonHeight/2;
        int gap = 30;

        JButton leftArrow = UIUtils.createTransparentButton("<");
        leftArrow.setFont(new Font("Arial", Font.BOLD, 36));
        leftArrow.setBounds(
            (GameFrame.WIDTH/2) - (characterDisplayWidth/2) - arrowButtonWidth - gap, 
            arrowButtonY, 
            arrowButtonWidth, 
            arrowButtonHeight);
        leftArrow.addActionListener(e -> previousCharacter());
        add(leftArrow);

        JButton rightArrow = UIUtils.createTransparentButton(">");
        rightArrow.setFont(new Font("Arial", Font.BOLD, 36));
        rightArrow.setBounds(
            (GameFrame.WIDTH/2) + (characterDisplayWidth/2) + gap, 
            arrowButtonY, 
            arrowButtonWidth, 
            arrowButtonHeight);
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
                
                String characterName = "Character " + characterNum;
                File nameFile = new File(characterDir, "name.txt");
                if (nameFile.exists()) {
                    try (Scanner scanner = new Scanner(nameFile)) {
                        if (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            if (line != null && !line.trim().isEmpty()) {
                                characterName = line.trim();
                            }
                        }
                    }
                }
                
                String characterPath = characterDir.getPath();
                    
                // Find the first walking animation image to display
                File walkingDir = new File(characterPath + "/walking");
                
                if (walkingDir.exists() && walkingDir.isDirectory()) {
                    File[] walkingFiles = walkingDir.listFiles((dir, name) -> 
                        name.toLowerCase().endsWith(".png"));
                    
                    if (walkingFiles != null && walkingFiles.length > 0) {
                        BufferedImage characterImg = ImageIO.read(walkingFiles[0]);
                        characterOptions.add(new CharacterOption(characterNum, characterImg, characterName));
                    } else {
                        // Try looking for any png in the character folder 
                        File[] files = characterDir.listFiles((dir, name) -> 
                            name.toLowerCase().endsWith(".png"));
                        
                        if (files != null && files.length > 0) {
                            BufferedImage characterImg = ImageIO.read(files[0]);
                            characterOptions.add(new CharacterOption(characterNum, characterImg, characterName));
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
        
        // Draw character preview
        if (!characterOptions.isEmpty()) {
            CharacterOption currentOption = characterOptions.get(selectedIndex);
            int maxWidth = 200;
            int maxHeight = 200;
            int y = GameFrame.HEIGHT/2 - maxHeight/2;

            // Set up character name text
            String characterName = currentOption.name;
            g.setFont(new Font("Courier New", Font.BOLD, 24));
            FontMetrics fm = g.getFontMetrics();
            int nameWidth = fm.stringWidth(characterName);
            int nameHeight = fm.getHeight();
            int textY = y - 20;
            int textX = GameFrame.WIDTH/2 - nameWidth/2;
            
            // Add background rectangle with padding
            int padding = 20;
            int boxX = textX - padding;
            int boxY = textY - nameHeight - 3;
            int boxWidth = nameWidth + (padding * 2);
            int boxHeight = nameHeight + padding;
            
            // Draw semi-transparent background with rounded corners
            Graphics2D g2d = (Graphics2D) g;
            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2d.setColor(new Color(0, 0, 0));
            g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);
            g2d.setComposite(originalComposite);
            
            // Draw border for the name box
            g2d.setColor(new Color(180, 180, 180));
            g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);
            
            // Draw the text (white color stands out against dark background)
            g.setColor(Color.WHITE);
            g.drawString(characterName, textX, textY);
            
            if (currentOption.image != null) {
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
                
                int imgX = GameFrame.WIDTH/2 - newWidth/2;
                int imgY = GameFrame.HEIGHT/2 - newHeight/2;
                
                g.drawImage(currentOption.image, imgX, imgY, newWidth, newHeight, null);
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
    
    private void selectCharacter() {
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
    
    private void goBack() {
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
        String name;
        
        CharacterOption(int id, BufferedImage image, String name) {
            this.id = id;
            this.image = image;
            this.name = name;
        }
    }
}