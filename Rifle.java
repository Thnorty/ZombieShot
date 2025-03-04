import java.io.File;

public class Rifle extends Weapon {
    public Rifle() {
        super(0, 0, 600, 30, 30, 30, 120);
        this.appearanceImagePath = "assets/Weapons/rifle.png";
        setImage(new File(appearanceImagePath));
    }
    
}
