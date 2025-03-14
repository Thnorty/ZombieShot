import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import java.awt.Dimension;

public class GameFrame extends JFrame {
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    protected static final String GAME_TITLE = "ZombieShot";

    public GameFrame() {
        setTitle(GAME_TITLE);
        setResizable(false);

        // Use a layered pane for overlays
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // Create game components
        GameInfo gameInfo = new GameInfo();

        StatPanel statPanel = new StatPanel(gameInfo);
        gameInfo.statPanel = statPanel;

        GamePanel gamePanel = new GamePanel(gameInfo);
        gameInfo.gamePanel = gamePanel;

        GameOverPanel gameOverPanel = new GameOverPanel(gameInfo);
        gameInfo.gameOverPanel = gameOverPanel;

        MainMenuPanel mainMenuPanel = new MainMenuPanel(gameInfo);
        gameInfo.mainMenuPanel = mainMenuPanel;

        PauseGamePanel pauseGamePanel = new PauseGamePanel(gameInfo);
        gameInfo.pauseGamePanel = pauseGamePanel;

        // Set bounds for each panel
        gamePanel.setBounds(0, StatPanel.HEIGHT, WIDTH, HEIGHT - StatPanel.HEIGHT);
        statPanel.setBounds(0, 0, WIDTH, StatPanel.HEIGHT);
        gameOverPanel.setBounds(0, 0, WIDTH, HEIGHT);
        mainMenuPanel.setBounds(0, 0, WIDTH, HEIGHT);
        pauseGamePanel.setBounds(0, 0, WIDTH, HEIGHT);

        // Add panels to layered pane with different depths
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(statPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(gameOverPanel, JLayeredPane.MODAL_LAYER);
        layeredPane.add(mainMenuPanel, JLayeredPane.MODAL_LAYER);
        layeredPane.add(pauseGamePanel, JLayeredPane.POPUP_LAYER);

        add(layeredPane);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}