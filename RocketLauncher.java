import java.io.File;

public class RocketLauncher extends Weapon {
    public static final double BLAST_RADIUS = 200;
    protected String hitSoundPath = "assets/WeaponSounds/Hit/rocket_launcher.wav";

    public RocketLauncher() {
        super(0, 0, 25, 0, 80, 0, "assets/WeaponSounds/Firing/rocket_launcher.wav", "assets/WeaponSounds/Reload/rocket_launcher.wav");
        this.appearanceImagePath = "assets/Weapons/rocket_launcher.png";
        setImage(new File(appearanceImagePath));
    }
    
    @Override
    public boolean canShoot() {
        if (!canShoot) {
            long currentTime = System.currentTimeMillis();
            long fireDelay = 60000 / shotsPerMinute;
            long difference = currentTime - lastShotTime;
            if (difference >= fireDelay) {
                canShoot = true;
            }
        }
        return canShoot && currentAmmo > 0;
    }
    
    @Override
    public void reload() {
    }
    
    public void addAmmo(int amount) {
        currentAmmo += amount;
    }
}
