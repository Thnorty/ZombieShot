import java.io.File;

public class Sniper extends Weapon {
    public Sniper() {
        super(0, 0, 55, 0, 60, 10, "assets/WeaponSounds/Firing/sniper.wav", "assets/WeaponSounds/Reload/sniper.wav");
        this.appearanceImagePath = "assets/Weapons/sniper.png";
        setImage(new File(appearanceImagePath));
    }
}
