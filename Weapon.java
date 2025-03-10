public class Weapon extends Entity {
    private static final int WEAPON_SIZE = 64;

    protected int shotsPerMinute;
    protected double shootingAngle;
    protected double damage;
    protected int ammo;
    protected int totalAmmo;
    protected int currentAmmo = 500;
    protected int currentTotalAmmo;
    protected boolean canShoot = true;
    protected long lastShotTime = 0;
    protected boolean isReloading = false;
    protected long reloadStartTime = 0;
    protected long reloadTimeMs = 2000;

    public Weapon(int x, int y, int shotsPerMinute, double shootingAngle, double damage, int ammo, int totalAmmo) {
        this.x = x;
        this.y = y;
        this.width = WEAPON_SIZE;
        this.height = WEAPON_SIZE;
        this.shotsPerMinute = shotsPerMinute;
        this.shootingAngle = shootingAngle;
        this.damage = damage;
        this.ammo = ammo;
        this.totalAmmo = totalAmmo;
    }

    public boolean canShoot() {
        if (!canShoot) {
            // Calculate if enough time has passed since last shot
            long currentTime = System.currentTimeMillis();
            long fireDelay = 60000 / shotsPerMinute;
            long difference = currentTime - lastShotTime;
            if (difference >= fireDelay) {
                canShoot = true;
            }
        }
        return canShoot && currentAmmo > 0 && !isReloading;
    }

    public void shoot() {
        if (canShoot) {
            canShoot = false;
            lastShotTime = System.currentTimeMillis();
        }
    }
}
