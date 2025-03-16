import java.io.File;

public class Shotgun extends Weapon {
    public Shotgun() {
        super(0, 0, 80, 0, 30, 7, "assets/WeaponSounds/Firing/shotgun.wav", "assets/WeaponSounds/Reload/shotgun.wav");
        this.appearanceImagePath = "assets/Weapons/shotgun.png";
        setImage(new File(appearanceImagePath));
    }
}
