import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicPlayer {
    private static Clip musicClip;
    private static float volume = 0.25f;
    private static ExecutorService audioExecutor = Executors.newSingleThreadExecutor();
    private static boolean isMuted = false;
    private static List<String> musicPlaylist = new ArrayList<>();
    private static String currentTrack = null;
    private static Random random = new Random();

	public static void initializePlaylist(String musicDirectory) {
        musicPlaylist.clear();
        
        File directory = new File(musicDirectory);
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Music directory not found: " + musicDirectory);
            return;
        }
        
        File[] musicFiles = directory.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".wav"));
        
        if (musicFiles == null || musicFiles.length == 0) {
            System.err.println("No music files found in " + musicDirectory);
            return;
        }
        
        for (File file : musicFiles) {
            musicPlaylist.add(file.getPath());
        }
    }
	
    public static void playRandomMusic() {
        if (musicPlaylist.isEmpty()) {
            System.err.println("Music playlist is empty!");
            return;
        }
        
        // Select a random track that's different from the current one if possible
        String nextTrack;
        if (musicPlaylist.size() > 1 && currentTrack != null) {
            do {
                nextTrack = musicPlaylist.get(random.nextInt(musicPlaylist.size()));
            } while (nextTrack.equals(currentTrack) && musicPlaylist.size() > 1);
        } else {
            nextTrack = musicPlaylist.get(random.nextInt(musicPlaylist.size()));
        }
        
        playBackgroundMusic(nextTrack);
    }

    public static void playBackgroundMusic(String musicPath) {
        stopBackgroundMusic();
        
        audioExecutor.submit(() -> {
            try {
                File musicFile = new File(musicPath);
                if (!musicFile.exists()) {
                    System.err.println("Music file not found: " + musicPath);
                    return;
                }
                
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
                musicClip = AudioSystem.getClip();
                musicClip.open(audioStream);
                
                setVolume(volume);

				musicClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP && !musicClip.isRunning()) {
                        playRandomMusic();
                    }
                });
                
                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
                
                musicClip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Error playing background music: " + e.getMessage());
            }
        });
    }
    
    public static void stopBackgroundMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.close();
        }
    }
    
    public static void setVolume(float volume) {
        if (volume < 0f || volume > 1f)
            return;
            
        MusicPlayer.volume = volume;
        
        if (musicClip != null) {
            FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            if (gainControl != null) {
                // Convert volume to decibels
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            }
        }
    }
	
    public static float getVolume() {
        return volume;
    }
    
    public static void toggleMute() {
        isMuted = !isMuted;
        
        if (musicClip != null) {
            FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            if (gainControl != null) {
                if (isMuted) {
                    gainControl.setValue(gainControl.getMinimum());
                } else {
                    float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                    gainControl.setValue(dB);
                }
            }
        }
    }

    public static boolean isMuted() {
        return isMuted;
    }
    
    public static void dispose() {
        stopBackgroundMusic();
        audioExecutor.shutdown();
    }
}