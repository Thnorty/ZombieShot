import java.io.File;

public class Pistol extends Weapon {
    public Pistol() {
        // TODO: make shotsPerMinute 120
        super(0, 0, 1200, 0, 20, 12, Integer.MAX_VALUE);
        this.appearanceImagePath = "assets/Weapons/pistol.png";
        this.currentAmmo = 12;
        this.currentTotalAmmo = Integer.MAX_VALUE;
        setImage(new File(appearanceImagePath));
    }
}
