package engine.util;

import game.Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

// plays the theme song
public class Sounds {
    private static Clip theme_song;

    public static void loopThemeSong(int speed) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    new File(Main.RES_SOUNDS_DIR + Main.MAIN_THEME_NAME + speed + Main.WAV_FILE).getAbsoluteFile());
            long current_position = 0;
            // if the song was playing remember the current pos for entering song with speed up at the same pos
            if (theme_song != null) {
                current_position = (long) (theme_song.getMicrosecondPosition() * (1 + 0.1 * (speed - 1)) / (1 + 0.1 * speed));
                theme_song.stop();
            }
            // loop song
            theme_song = AudioSystem.getClip();
            theme_song.open(audioInputStream);
            theme_song.setMicrosecondPosition(current_position);
            theme_song.start();
            theme_song.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception ex) {
            System.err.println("Error playing sound: " + Main.MAIN_THEME_NAME);
            ex.printStackTrace();
        }
    }

    // stop song if game is to be restarted
    public static void stopThemeSong() {
        theme_song.stop();
        theme_song = null;
    }
}
