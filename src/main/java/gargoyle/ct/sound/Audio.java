package gargoyle.ct.sound;

import org.jetbrains.annotations.NotNull;

import java.net.URL;

public final class Audio {
    private Audio() {
    }

    @NotNull
    public static AudioClip play(URL url) {
        AudioClip clip = load(url);
        clip.play();
        return clip;
    }

    public static AudioClip load(URL url) {
        return new AudioClipImpl(url);
    }
}
