package com.aapeli.client;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

class SoundClip {
    private URL url;
    private boolean debug;
    private Clip clip;
    private boolean loaded;

    protected SoundClip(URL url, boolean debug) {
        this.debug = debug;
        this.loaded = false;
        this.url = url;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public URL getUrl() {
        return url;
    }

    protected void load() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        if (!this.loaded) {
            if (this.debug) {
                System.out.println("SoundClip.load(): 'url'=\"" + this.url + "\"");
            }

            AudioInputStream sound = AudioSystem.getAudioInputStream(url);
            this.clip = AudioSystem.getClip();
            clip.open(sound);
            this.loaded = true;
        }
    }

    protected Clip getClip() {
        return this.clip;
    }
}
