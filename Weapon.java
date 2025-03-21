import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Weapon extends Entity {
    private static final int WEAPON_SIZE = 64;

    protected double shootingAngle;
    protected double damage;
    protected int maxAmmoInClip;
    protected int currentAmmo;
    protected int currentTotalAmmo;
    protected boolean canShoot = true;
    protected long lastShotTime = 0;
    protected boolean isReloading = false;
    protected long reloadStartTime = 0;
    protected long reloadTimeMs = 2000;
    protected String firingSoundPath;
    protected String reloadSoundPath;
    protected long fireDelay;

    public Weapon(int x, int y, int shotsPerMinute, double shootingAngle, double damage, int maxAmmoInClip, String firingSoundPath, String reloadSoundPath) {
        this.x = x;
        this.y = y;
        this.width = WEAPON_SIZE;
        this.height = WEAPON_SIZE;
        this.shootingAngle = shootingAngle;
        this.damage = damage;
        this.maxAmmoInClip = maxAmmoInClip;
        this.firingSoundPath = firingSoundPath;
        this.reloadSoundPath = reloadSoundPath;
        this.fireDelay = 60000 / shotsPerMinute;
        calculateReloadTimeFromSound();
    }
    
    protected void calculateReloadTimeFromSound() {
        if (reloadSoundPath != null) {
            try {
                File soundFile = new File(reloadSoundPath);
                if (soundFile.exists()) {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                    AudioFormat format = audioInputStream.getFormat();
                    long frames = audioInputStream.getFrameLength();
                    reloadTimeMs = (long) (frames / format.getFrameRate() * 1000);
                    audioInputStream.close();
                }
            } catch (UnsupportedAudioFileException | IOException e) {
                System.err.println("Error calculating sound duration: " + e.getMessage());
                // Keep the default reload time if there's an error
            }
        }
    }

    public void reload() {
        if (!isReloading && currentAmmo < maxAmmoInClip && currentTotalAmmo > 0) {
            isReloading = true;
            reloadStartTime = System.currentTimeMillis();
            playReloadSound();
        }
    }

    public boolean canShoot() {
        if (!canShoot) {
            if (System.currentTimeMillis() - lastShotTime >= fireDelay) {
                canShoot = true;
            }
        }
        return canShoot && currentAmmo > 0 && !isReloading;
    }

    public void shoot() {
        if (canShoot) {
            canShoot = false;
            lastShotTime = System.currentTimeMillis();
            playFiringSound();
        }
    }
    
    public void playFiringSound() {
        GameInfo.playSound(firingSoundPath);
    }

    public void playReloadSound() {
        GameInfo.playSound(reloadSoundPath);
    }
}
