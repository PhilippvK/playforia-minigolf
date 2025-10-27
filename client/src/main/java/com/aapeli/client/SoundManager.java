package com.aapeli.client;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class SoundManager {
    private final boolean debug;
    private Map<Integer, Clip> sounds;
    private boolean clipLoaderThreadRunning;
    public int audioChoicerIndex;

    public SoundManager(boolean debug) {
        this.debug = debug;
        this.audioChoicerIndex = 0;
        this.defineSounds();
    }

    public void playChallenge() {
        if (this.debug) {
            System.out.println("SoundManager.playChallenge()");
        }

        this.playClip(1);
    }

    public void playGameMove() {
        if (this.debug) {
            System.out.println("SoundManager.playGameMove()");
        }

        this.playClip(2);
    }

    public void playNotify() {
        if (this.debug) {
            System.out.println("SoundManager.playNotify()");
        }

        this.playClip(3);
    }

    public void playIllegal() {
        if (this.debug) {
            System.out.println("SoundManager.playIllegal()");
        }

        this.playClip(4);
    }

    public void playTimeLow() {
        if (this.debug) {
            System.out.println("SoundManager.playTimeLow()");
        }

        this.playClip(5);
    }

    public void playGameWinner() {
        if (this.debug) {
            System.out.println("SoundManager.playGameWinner()");
        }

        this.playClip(6);
    }

    public void playGameLoser() {
        if (this.debug) {
            System.out.println("SoundManager.playGameLoser()");
        }

        this.playClip(7);
    }

    public void playGameDraw() {
        if (this.debug) {
            System.out.println("SoundManager.playGameDraw()");
        }

        this.playClip(8);
    }

    public void destroy() {
        this.sounds.clear();
        this.sounds = null;
    }

    protected boolean isDebug() {
        return this.debug;
    }

    private void defineSounds() {
        try {
            this.sounds = new Hashtable<>();
            this.defineSoundClip(1, "/sound/shared/challenge.au");
            this.defineSoundClip(2, "/sound/shared/gamemove.au");
            this.defineSoundClip(3, "/sound/shared/notify.au");
            this.defineSoundClip(4, "/sound/shared/illegal.au");
            this.defineSoundClip(5, "/sound/shared/timelow.au");
            this.defineSoundClip(6, "/sound/shared/game-winner.au");
            this.defineSoundClip(7, "/sound/shared/game-loser.au");
            this.defineSoundClip(8, "/sound/shared/game-draw.au");
        } catch (Exception e) {
            System.out.println("SoundManager.loadClientSounds(): failed to load sounds: " + e);
        }
    }

    private void defineSoundClip(int id, String resourcePath)
            throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        URL url = this.getClass().getResource(resourcePath);
        AudioInputStream sound = AudioSystem.getAudioInputStream(url);
        Clip clip = AudioSystem.getClip();
        clip.open(sound);
        this.sounds.put(id, clip);
    }

    private void playClip(int id) {
        Clip clip = this.sounds.get(id);
        if (clip != null && this.audioChoicerIndex != 1) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
}
