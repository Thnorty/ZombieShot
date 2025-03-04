import java.io.File;
import java.util.ArrayList;

public class Player extends Entity {
    private static final int PLAYER_WIDTH = 64;
    private static final int PLAYER_HEIGHT = 64;
    public static final int PLAYER_HEALTH = 100;

    protected ArrayList<Weapon> weapons = new ArrayList<Weapon>();
    protected Weapon currentWeapon;
    public boolean facingLeft = false;
    protected double health = PLAYER_HEALTH;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = PLAYER_WIDTH;
        this.height = PLAYER_HEIGHT;
        this.appearanceImagePath = "assets/Player/player.png";
        setImage(new File(appearanceImagePath));
        weapons.add(new Pistol());
        weapons.add(new Rifle());
        weapons.add(new Shotgun());
        weapons.add(new Sniper());
        weapons.add(new RocketLauncher());
        currentWeapon = weapons.get(0);
    }

    public void reload() {
        if (currentWeapon.currentTotalAmmo > 0) {
            int ammoNeeded = currentWeapon.ammo - currentWeapon.currentAmmo;
            if (currentWeapon.currentTotalAmmo >= ammoNeeded) {
                currentWeapon.currentAmmo += ammoNeeded;
                currentWeapon.currentTotalAmmo -= ammoNeeded;
            } else {
                currentWeapon.currentAmmo += currentWeapon.currentTotalAmmo;
                currentWeapon.currentTotalAmmo = 0;
            }
        }
    }

    public void updateGunPosition() {
        if (currentWeapon != null) {
            // Position the gun relative to player facing direction
            double offsetX = 50;
            
            // Calculate gun position based on player rotation
            double angle = Math.toRadians(rotation);
            
            // If player is facing left, adjust the gun position
            if (facingLeft) {
                // Mirror the gun position when player is facing left
                angle = Math.PI - angle;
            }
            
            double gunX = getCenterX() + Math.cos(angle) * offsetX - currentWeapon.width / 2;
            double gunY = getCenterY() + Math.sin(angle) * offsetX - currentWeapon.height / 2;
            
            currentWeapon.x = gunX;
            currentWeapon.y = gunY;
            
            // Set gun rotation to match player rotation
            currentWeapon.rotation = rotation;
        }
    }
}
