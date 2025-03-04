import java.io.File;

public class Shotgun extends Weapon {
    public Shotgun() {
        super(0, 0, 60, 0, 30, 5, 15);
        this.appearanceImagePath = "assets/Weapons/shotgun.png";
        setImage(new File(appearanceImagePath));
    }
}
