import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.io.*;
import java.util.HashMap;
import java.awt.Image;
import java.awt.event.KeyEvent;

public class GameInfo {
    protected final int ZOMBIES_PER_WAVE = 20;
    protected final int ZOMBIE_INCREASE_PERCENT = 25;
    protected final float HEALTH_DROP_CHANCE = 0.01f;
    protected final float AMMO_DROP_CHANCE = 0.3f;
    protected final int ZOMBIE_SPAWN_RATE = 400;
    protected final float PLAYER_SPEED = 5.0f;
    protected final float BULLET_SPEED = 20.0f;
    protected final String BACKGROUND_IMAGE_PATH = "assets/Backgrounds/menu_background.png";

    protected Player player;
    protected StatPanel statPanel;
    protected GamePanel gamePanel;
    protected GameOverPanel gameOverPanel;
    protected MainMenuPanel mainMenuPanel;
    protected PauseGamePanel pauseGamePanel;
    protected List<Zombie> zombies = new ArrayList<>();
    protected List<Bullet> bullets = new ArrayList<>();
    protected List<Drop> drops = new ArrayList<>();
    protected Timer gameTimer;
    protected Timer zombieSpawnTimer;
    protected HashMap<String, Integer> keyBindings = new HashMap<>();
    protected Image backgroundImage;

    private int zombiesKilledLastWave = 0;
    protected int currentWave = 1;
    protected int zombiesKilled = 0;
    protected int zombiesSpawned = 0;
    protected int selectedCharacter = 1;
    protected boolean isPaused = false;

    public GameInfo() {
        player = new Player(0, 0, selectedCharacter);
        
        keyBindings.put("moveUp", KeyEvent.VK_W);
        keyBindings.put("moveDown", KeyEvent.VK_S);
        keyBindings.put("moveLeft", KeyEvent.VK_A);
        keyBindings.put("moveRight", KeyEvent.VK_D);
        keyBindings.put("reload", KeyEvent.VK_R);
        keyBindings.put("weapon1", KeyEvent.VK_1);
        keyBindings.put("weapon2", KeyEvent.VK_2);
        keyBindings.put("weapon3", KeyEvent.VK_3);
        keyBindings.put("weapon4", KeyEvent.VK_4);
        keyBindings.put("weapon5", KeyEvent.VK_5);
        keyBindings.put("pause", KeyEvent.VK_ESCAPE);
        keyBindings.put("debug", KeyEvent.VK_F3);

        try {
            backgroundImage = ImageIO.read(new File(BACKGROUND_IMAGE_PATH));
        } catch (IOException e) {
            System.err.println("Could not load background image: " + BACKGROUND_IMAGE_PATH);
        }
        
        // Try to load saved keybindings
        loadKeyBindings();
    }

    public void updateZombiesRemaining(int count) {
        if (statPanel != null) {
            statPanel.update();
        }
    }

    public void incrementWaveIfNeeded() {
        int maxZombiesForCurrentWave = getMaxZombiesPerWave();
        if (zombies.isEmpty() && zombiesSpawned == maxZombiesForCurrentWave) {
            zombiesKilledLastWave = zombiesKilled;
            currentWave++;
            if (statPanel != null) {
                statPanel.update();
            }
        }
    }

    public int getMaxZombiesPerWave() {
        return zombiesKilledLastWave + ZOMBIES_PER_WAVE + (ZOMBIES_PER_WAVE * ZOMBIE_INCREASE_PERCENT * (currentWave - 1) / 100);
    }

    public void addZombie(Zombie zombie) {
        zombies.add(zombie);
        zombiesSpawned++;
        updateZombiesRemaining(zombies.size());
    }
    
    public void addDrop(Drop drop) {
        drops.add(drop);
    }

    public void showGameOver() {
        if (gameOverPanel != null) {
            gameOverPanel.updateStats();
            gameOverPanel.setVisible(true);
        }
    }

    public void setSelectedCharacter(int characterNumber) {
        this.selectedCharacter = characterNumber;
    }
    
    public void restartGame() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        if (zombieSpawnTimer != null && zombieSpawnTimer.isRunning()) {
            zombieSpawnTimer.stop();
        }

        isPaused = false;

        player = new Player(0, 0, selectedCharacter);
        gamePanel.centerplayer();

        gamePanel.moveUp = false;
        gamePanel.moveDown = false;
        gamePanel.moveLeft = false;
        gamePanel.moveRight = false;
        gamePanel.leftMousePressed = false;

        player.weapons.clear();
        player.weapons.add(new Pistol());
        player.weapons.add(new Rifle());
        player.weapons.add(new Shotgun());
        player.weapons.add(new Sniper());
        player.weapons.add(new RocketLauncher());
        player.currentWeapon = player.weapons.get(0);

        currentWave = 1;
        zombiesKilled = 0;
        zombiesSpawned = 0;
        zombiesKilledLastWave = 0;
        zombies.clear();
        bullets.clear();
        drops.clear();
        player.health = Player.PLAYER_HEALTH;

        if (statPanel != null) {
            statPanel.update();
        }
        
        gameOverPanel.setVisible(false);

        if (!gameTimer.isRunning()) {
            gameTimer.start();
        }
        if (!zombieSpawnTimer.isRunning()) {
            zombieSpawnTimer.start();
        }
    }

    public boolean saveGame() {
        try {
            File saveDir = new File("saves");
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            
            FileOutputStream fileOut = new FileOutputStream("saves/zombieshot_save.dat");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            
            // Create a game state object with only the necessary data
            GameState state = new GameState();
            
            // Save basic game state
            state.currentWave = this.currentWave;
            state.selectedCharacter = this.selectedCharacter;
            state.player = this.player;
            state.zombies = new ArrayList<>(this.zombies);
            state.bullets = new ArrayList<>(this.bullets);
            state.drops = new ArrayList<>(this.drops);
            state.zombiesKilled = this.zombiesKilled;
            state.zombiesSpawned = this.zombiesSpawned;
            state.zombiesKilledLastWave = this.zombiesKilledLastWave;
            
            // Save background itself instead of just the offset
            if (gamePanel != null && gamePanel.background != null) {
                state.background = gamePanel.background;
            }
            
            out.writeObject(state);
            out.close();
            fileOut.close();
            
            return true;
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadGame() {
        try {
            File saveFile = new File("saves/zombieshot_save.dat");
            if (!saveFile.exists()) {
                return false;
            }
            
            FileInputStream fileIn = new FileInputStream(saveFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            
            GameState state = (GameState) in.readObject();
            in.close();
            fileIn.close();
            
            // Stop any running timers
            if (gameTimer != null && gameTimer.isRunning()) {
                gameTimer.stop();
            }
            if (zombieSpawnTimer != null && zombieSpawnTimer.isRunning()) {
                zombieSpawnTimer.stop();
            }
            
            // Restore game state
            this.currentWave = state.currentWave;
            this.selectedCharacter = state.selectedCharacter;
            this.player = state.player;
            
            // Ensure the player has its images loaded
            this.player.loadImage();
            this.gamePanel.centerplayer();
            
            // Clear and restore entities
            this.zombies.clear();
            this.zombies.addAll(state.zombies);

            this.bullets.clear();
            this.bullets.addAll(state.bullets);
            
            this.drops.clear();
            this.drops.addAll(state.drops);
            
            this.zombiesKilled = state.zombiesKilled;
            this.zombiesSpawned = state.zombiesSpawned;
            this.zombiesKilledLastWave = state.zombiesKilledLastWave;
            
            // Restore background
            if (gamePanel != null && state.background != null) {
                gamePanel.background = state.background;
            }
            
            // Update UI
            if (statPanel != null) {
                statPanel.update();
            }
            
            // Start the timers
            gameTimer.start();
            zombieSpawnTimer.start();

            isPaused = false;
            gamePanel.requestFocus();
            
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveKeyBindings() {
        try {
            File saveDir = new File("saves");
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            
            FileOutputStream fileOut = new FileOutputStream("saves/keybinds.dat");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            
            // Save key bindings hashmap
            out.writeObject(keyBindings);
            out.close();
            fileOut.close();
            
            return true;
        } catch (IOException e) {
            System.err.println("Error saving keybindings: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadKeyBindings() {
        try {
            File keybindsFile = new File("saves/keybinds.dat");
            if (!keybindsFile.exists()) {
                return false;
            }
            
            FileInputStream fileIn = new FileInputStream(keybindsFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            
            // Load key bindings hashmap
            @SuppressWarnings("unchecked")
            HashMap<String, Integer> savedBindings = (HashMap<String, Integer>) in.readObject();
            in.close();
            fileIn.close();
            
            if (savedBindings != null) {
                keyBindings = savedBindings;
                return true;
            }
            return false;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading keybindings: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int getKeyBinding(String action) {
        return keyBindings.getOrDefault(action, 0);
    }

    public void setKeyBinding(String action, int keyCode) {
        keyBindings.put(action, keyCode);
    }

    // The GameState class holds all serializable game data
    private static class GameState implements Serializable {
        private static final long serialVersionUID = 1L;
        int currentWave;
        int selectedCharacter;
        Player player;
        List<Zombie> zombies;
        List<Bullet> bullets;
        List<Drop> drops;
        int zombiesKilled;
        int zombiesSpawned;
        int zombiesKilledLastWave;
        Background background;
    }
}
