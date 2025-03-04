public class AmmoDrop extends Drop {
    private int ammoAmount;
    private Weapon sourceWeapon;

    public AmmoDrop(double x, double y, int ammoAmount, Weapon sourceWeapon) {
        super(x, y, sourceWeapon.appearanceImagePath);
        this.ammoAmount = ammoAmount;
        this.sourceWeapon = sourceWeapon;
    }

    public int getAmmoAmount() {
        return ammoAmount;
    }

    public Weapon getSourceWeapon() {
        return sourceWeapon;
    }
}
