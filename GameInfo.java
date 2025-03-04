import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

public class GameInfo {
    protected final int ZOMBIES_PER_WAVE = 20;
    protected final int ZOMBIE_INCREASE_PERCENT = 25;
    protected final float HEALTH_DROP_CHANCE = 0.01f;
    protected final float AMMO_DROP_CHANCE = 0.3f;
    protected final int ZOMBIE_SPAWN_RATE = 400;
    protected final float PLAYER_SPEED = 5.0f;
    protected final float BULLET_SPEED = 20.0f;

    protected Player player;
    protected StatPanel statPanel;
    protected GamePanel gamePanel;
    protected GameOverPanel gameOverPanel;
    protected MainMenuPanel mainMenuPanel;
    protected List<Zombie> zombies = new ArrayList<>();
    protected List<Bullet> bullets = new ArrayList<>();
    protected List<Drop> drops = new ArrayList<>();
    protected Timer gameTimer;
    protected Timer zombieSpawnTimer;

    private int zombiesKilledLastWave = 0;
    protected int currentWave = 1;
    protected int score = 0;
    protected int zombiesKilled = 0;
    protected int zombiesSpawned = 0;
    protected int selectedCharacter = 1;

    public GameInfo() {
        player = new Player(0, 0, selectedCharacter);
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

        if (gameTimer.isRunning())
            gameTimer.stop();
        if (zombieSpawnTimer.isRunning())
            zombieSpawnTimer.stop();
        gameTimer.start();
        zombieSpawnTimer.start();  

        gamePanel.requestFocus();
    }
}
