import java.util.Random;

public class AmmoDrop extends Drop {
    private int ammoAmount;
    private Weapon sourceWeapon;
    private static final Random random = new Random();
    private static final double VARIATION_PERCENTAGE = 0.3;

    public AmmoDrop(double x, double y, int ammoAmount, Weapon sourceWeapon) {
        super(x, y, sourceWeapon.appearanceImagePath);
        this.ammoAmount = ammoAmount;
        this.sourceWeapon = sourceWeapon;
    }

    public AmmoDrop(double x, double y, Weapon sourceWeapon, GameInfo gameInfo) {
        super(x, y, sourceWeapon.appearanceImagePath);
        int baseAmmo = sourceWeapon.maxAmmoInClip;
        int minAmount = (int)(baseAmmo * (1 - VARIATION_PERCENTAGE));
        int maxAmount = (int)(baseAmmo * (1 + VARIATION_PERCENTAGE));
        
        minAmount = Math.max(1, minAmount);
        
        this.ammoAmount = minAmount + random.nextInt(maxAmount - minAmount + 1);
        
        if (gameInfo.currentDifficulty == GameInfo.GameDifficulty.HARD) {
            this.ammoAmount *= 2;
        }
        
        this.sourceWeapon = sourceWeapon;
    }

    public int getAmmoAmount() {
        return ammoAmount;
    }

    public Weapon getSourceWeapon() {
        return sourceWeapon;
    }
}
