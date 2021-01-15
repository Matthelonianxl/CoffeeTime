package gargoyle.ct.sound;

import gargoyle.ct.log.Log;
import gargoyle.ct.util.Defend;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AudioClipImpl implements AudioClip {
    boolean playing;
    private Clip clip;
    private boolean released;

    public AudioClipImpl(URL url) {
        Defend.notNull(url, "null audio clip");
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(stream);
            clip.addLineListener(new AudioClipListener());
            released = true;
        } catch (@NotNull IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
            Log.error(exc.getLocalizedMessage(), exc);
            released = false;
        }
    }

    public boolean isReleased() {
        return released;
    }

    @Override
    public void loop() {
        play();
    }

    @Override
    public void play() {
        play(true);
    }

    public void play(boolean breakOld) {
        if (released) {
            if (breakOld) {
                clip.stop();
                clip.setFramePosition(0);
                clip.start();
                playing = true;
            } else if (!playing) {
                clip.setFramePosition(0);
                clip.start();
                playing = true;
            }
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void stop() {
        if (playing) {
            clip.stop();
        }
    }

    private class AudioClipListener implements LineListener {
        @Override
        public void update(@NotNull LineEvent event) {
            if (event.getType() == LineEvent.Type.STOP) {
                playing = false;
            }
        }
    }
}

