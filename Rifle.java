import java.io.File;

public class Rifle extends Weapon {
    public Rifle() {
        super(0, 0, 600, 30, 30, 30, "assets/WeaponSounds/Firing/rifle.wav", "assets/WeaponSounds/Reload/rifle.wav");
        this.appearanceImagePath = "assets/Weapons/rifle.png";
        setImage(new File(appearanceImagePath));
    }
    
}
