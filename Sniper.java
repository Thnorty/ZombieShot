import java.io.File;

public class Sniper extends Weapon {
    public Sniper() {
        super(0, 0, 30, 0, 60, 5, 20);
        this.appearanceImagePath = "assets/Weapons/sniper.png";
        setImage(new File(appearanceImagePath));
    }
}
