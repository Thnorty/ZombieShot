import java.io.File;

public class RocketLauncher extends Weapon {
    public static final double BLAST_RADIUS = 200;

    public RocketLauncher() {
        super(0, 0, 10, 0, 80, 1, 20);
        this.appearanceImagePath = "assets/Weapons/rocket_launcher.png";
        setImage(new File(appearanceImagePath));
    }
}
