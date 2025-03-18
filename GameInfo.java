import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;
import java.io.*;
import java.util.HashMap;
import java.awt.Image;
import java.awt.event.KeyEvent;

public class GameInfo {
    public enum GameDifficulty { NORMAL, HARD }

    protected GameDifficulty currentDifficulty = GameDifficulty.NORMAL;

    protected final int NORMAL_ZOMBIE_SPAWN_RATE = 400;
    protected final int HARD_ZOMBIE_SPAWN_RATE = 200;
    protected final int NORMAL_ZOMBIES_PER_WAVE = 10;
    protected final int HARD_ZOMBIES_PER_WAVE = 15;
    protected final int NORMAL_ZOMBIE_INCREASE_PERCENT = 50;
    protected final int HARD_ZOMBIE_INCREASE_PERCENT = 75;
    protected final double NORMAL_ZOMBIE_SPEED_MULTIPLIER = 1.0;
    protected final double HARD_ZOMBIE_SPEED_MULTIPLIER = 1.3;

    protected int currentZombieSpawnRate = NORMAL_ZOMBIE_SPAWN_RATE;
    protected int currentZombiesPerWave = NORMAL_ZOMBIES_PER_WAVE;
    protected int currentZombieIncreasePercent = NORMAL_ZOMBIE_INCREASE_PERCENT;
    protected double currentZombieSpeedMultiplier = NORMAL_ZOMBIE_SPEED_MULTIPLIER;

    protected final int ZOMBIE_INCREASE_PERCENT = 50;
    protected final float HEALTH_DROP_CHANCE = 0.01f;
    protected final float AMMO_DROP_CHANCE = 0.3f;
    protected final float PLAYER_SPEED = 5.0f;
    protected final float BULLET_SPEED = 20.0f;
    protected final String BACKGROUND_IMAGE_PATH = "assets/Backgrounds/menu_background.png";
    protected final String BACKGROUND_MUSIC_PATH = "assets/Musics/";

    protected Player player;
    protected StatPanel statPanel;
    protected GamePanel gamePanel;
    protected GameOverPanel gameOverPanel;
    protected MainMenuPanel mainMenuPanel;
    protected PauseGamePanel pauseGamePanel;
    protected List<Zombie> zombies = new ArrayList<>();
    protected List<Bullet> bullets = new ArrayList<>();
    protected List<Drop> drops = new ArrayList<>();
    protected List<Animation> animations = new ArrayList<>();
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
        startBackgroundMusic();
        player = new Player(0, 0, selectedCharacter);

        setDifficulty(GameDifficulty.NORMAL);

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
        loadSettings();
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
        return zombiesKilledLastWave + currentZombiesPerWave + 
               (currentZombiesPerWave * currentZombieIncreasePercent * (currentWave - 1) / 100);
    }
    
    public void setDifficulty(GameDifficulty difficulty) {
        this.currentDifficulty = difficulty;
        
        if (difficulty == GameDifficulty.HARD) {
            currentZombieSpawnRate = HARD_ZOMBIE_SPAWN_RATE;
            currentZombiesPerWave = HARD_ZOMBIES_PER_WAVE;
            currentZombieIncreasePercent = HARD_ZOMBIE_INCREASE_PERCENT;
            currentZombieSpeedMultiplier = HARD_ZOMBIE_SPEED_MULTIPLIER;
            for (Zombie zombie : zombies) {
                zombie.moveSpeed *= HARD_ZOMBIE_SPEED_MULTIPLIER / NORMAL_ZOMBIE_SPEED_MULTIPLIER;
            }
        } else {
            currentZombieSpawnRate = NORMAL_ZOMBIE_SPAWN_RATE;
            currentZombiesPerWave = NORMAL_ZOMBIES_PER_WAVE;
            currentZombieIncreasePercent = NORMAL_ZOMBIE_INCREASE_PERCENT;
            currentZombieSpeedMultiplier = NORMAL_ZOMBIE_SPEED_MULTIPLIER;
            for (Zombie zombie : zombies) {
                zombie.moveSpeed *= NORMAL_ZOMBIE_SPEED_MULTIPLIER / HARD_ZOMBIE_SPEED_MULTIPLIER;
            }
        }
        
        // Update the timer if it exists
        if (zombieSpawnTimer != null) {
            zombieSpawnTimer.setDelay(currentZombieSpawnRate);
        }
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
        if (currentDifficulty == GameDifficulty.HARD) {
            currentZombieSpeedMultiplier = HARD_ZOMBIE_SPEED_MULTIPLIER;
        } else {
            currentZombieSpeedMultiplier = NORMAL_ZOMBIE_SPEED_MULTIPLIER;
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
        if (zombieSpawnTimer != null) {
            zombieSpawnTimer.setDelay(currentZombieSpawnRate);
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
            state.difficulty = this.currentDifficulty;
            
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
            this.currentDifficulty = state.difficulty;

            if (this.currentDifficulty == GameDifficulty.HARD) {
                this.currentZombieSpeedMultiplier = HARD_ZOMBIE_SPEED_MULTIPLIER;
            } else {
                this.currentZombieSpeedMultiplier = NORMAL_ZOMBIE_SPEED_MULTIPLIER;
            }
            
            for (Zombie zombie : this.zombies) {
                if (state.difficulty == GameDifficulty.HARD) {
                    zombie.moveSpeed *= HARD_ZOMBIE_SPEED_MULTIPLIER / NORMAL_ZOMBIE_SPEED_MULTIPLIER;
                }
            }
            
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

    public boolean saveSettings() {
        try {
            File saveDir = new File("saves");
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            
            FileOutputStream fileOut = new FileOutputStream("saves/settings.dat");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            
            // Create a settings object with all settings
            HashMap<String, Object> allSettings = new HashMap<>();
            allSettings.put("keyBindings", keyBindings);
            allSettings.put("musicVolume", MusicPlayer.getMusicVolume());
            allSettings.put("sfxVolume", MusicPlayer.getSfxVolume());
    
            // Save all settings
            out.writeObject(allSettings);
            out.close();
            fileOut.close();
            
            return true;
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public boolean loadSettings() {
        try {
            File keybindsFile = new File("saves/settings.dat");
            if (!keybindsFile.exists()) {
                return false;
            }
            
            FileInputStream fileIn = new FileInputStream(keybindsFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            
            // Load key bindings hashmap
            HashMap<String, Object> allSettings = (HashMap<String, Object>) in.readObject();
            in.close();
            fileIn.close();
        
            if (allSettings != null) {
                // Load key bindings
                if (allSettings.containsKey("keyBindings")) {
                    keyBindings = (HashMap<String, Integer>) allSettings.get("keyBindings");
                }
                
                // Load music volume
                if (allSettings.containsKey("musicVolume")) {
                    float savedVolume = (Float) allSettings.get("musicVolume");
                    MusicPlayer.setMusicVolume(savedVolume);
                }

                // Load sfx volume
                if (allSettings.containsKey("sfxVolume")) {
                    float savedVolume = (Float) allSettings.get("sfxVolume");
                    MusicPlayer.setSfxVolume(savedVolume);
                }
                
                return true;
            }
            return false;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading settings: " + e.getMessage());
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
        GameDifficulty difficulty;
    }

    public static void playSound(String soundPath) {
        if (soundPath != null) {
            new Thread(() -> {
                AudioInputStream audioStream = null;
                Clip clip = null;
                try {
                    audioStream = AudioSystem.getAudioInputStream(new File(soundPath));
                    clip = AudioSystem.getClip();
                    clip.open(audioStream);

                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    if (gainControl != null) {
                        float dB = (float) (Math.log(MusicPlayer.getSfxVolume()) / Math.log(10.0) * 20.0);
                        gainControl.setValue(dB);
                    }

                    // Add a listener to close resources when playback completes
                    final Clip finalClip = clip;
                    final AudioInputStream finalStream = audioStream;
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            finalClip.close();
                            try {
                                finalStream.close();
                            } catch (IOException e) {
                                System.err.println("Error closing audio stream: " + e.getMessage());
                            }
                        }
                    });
                    
                    clip.start();
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                    System.err.println("Error playing sound: " + e.getMessage());
                    // Clean up resources in case of error
                    if (clip != null) clip.close();
                    if (audioStream != null) {
                        try {
                            audioStream.close();
                        } catch (IOException ex) {
                            System.err.println("Error closing audio stream: " + ex.getMessage());
                        }
                    }
                }
            }).start();
        }
    }
    
    public void startBackgroundMusic() {
        MusicPlayer.initializePlaylist(BACKGROUND_MUSIC_PATH);
        MusicPlayer.playRandomMusic();
    }

    public void stopBackgroundMusic() {
        MusicPlayer.stopBackgroundMusic();
    }
}
