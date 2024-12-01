package com.aapeli.client;

import java.util.Hashtable;
import javax.sound.sampled.Clip;

public final class SoundManager implements Runnable {
    private static final String[] methodLookup = {"stop", "play", "loop"};
    private final boolean debug;
    private boolean startupDebug;
    private Hashtable<Integer, SoundClip> clientSounds;
    private Hashtable<String, SoundClip> sharedSounds;
    private boolean clipLoaderThreadRunning;
    public int audioChoicerIndex;

    public SoundManager() {
        this(true, false);
    }

    public SoundManager(boolean debug) {
        this(true, debug);
    }

    public SoundManager(boolean shouldLoadClips, boolean debug) {
        this.debug = debug;
        this.audioChoicerIndex = 0;
        this.defineClientSounds();

        this.sharedSounds = new Hashtable<>();
        this.clipLoaderThreadRunning = false;
        if (shouldLoadClips) {
            this.start();
        }
    }

    @Override
    public void run() {
        if (this.debug) {
            System.out.println("SoundManager.run(): Thread started");
        }

        for (SoundClip soundClip : this.clientSounds.values()) {
            try {
                if (!soundClip.isLoaded()) {
                    soundClip.load();
                }
            } catch (Exception e) {
                System.out.println("SoundManager.run(): Failed to define clip " + soundClip.getUrl() + ": " + e);
            }
        }

        for (SoundClip soundClip : this.sharedSounds.values()) {
            try {
                if (!soundClip.isLoaded()) {
                    soundClip.load();
                }
            } catch (Exception e) {
                System.out.println("SoundManager.run(): Failed to define clip " + soundClip.getUrl() + ": " + e);
            }
        }

        this.clipLoaderThreadRunning = false;
        if (this.debug) {
            System.out.println("SoundManager.run(): Thread finished");
        }
    }

    public void startLoading() {
        this.start();
    }

    public void play(String clipName) {
        this.handleSharedSoundClip(clipName, 1);
    }

    public void loop(String clipName) {
        this.handleSharedSoundClip(clipName, 2);
    }

    public void stop(String clipName) {
        this.handleSharedSoundClip(clipName, 0);
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
        this.sharedSounds.clear();
        this.sharedSounds = null;
        this.clientSounds.clear();
        this.clientSounds = null;
    }

    public void enableSUD() {
        this.startupDebug = true;
    }

    protected boolean isDebug() {
        return this.debug;
    }

    private void defineClientSounds() {
        try {
            this.clientSounds = new Hashtable<>();
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

    private void defineSoundClip(int id, String resourcePath) {
        this.clientSounds.put(id, new SoundClip(this.getClass().getResource(resourcePath), this.debug));
    }

    private synchronized void start() {
        if (!this.clipLoaderThreadRunning) {
            this.clipLoaderThreadRunning = true;
            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void playClip(int id) {
        SoundClip soundClip = this.clientSounds.get(id);
        if (soundClip != null && this.audioChoicerIndex != 1) {
            Clip clip = soundClip.getClip();
            if (clip != null) {
                clip.setFramePosition(0);
                clip.start();
            }
        }
    }

    private void handleSharedSoundClip(String clipName, int methodIndex) {
        try {
            if (this.debug) {
                System.out.println("SoundManager." + methodLookup[methodIndex] + "(\"" + clipName + "\")");
            }

            SoundClip soundClip = this.sharedSounds.get(clipName);
            if (soundClip != null) {
                Clip clip = soundClip.getClip();
                if (clip != null) {
                    if (methodIndex == 0) {
                        clip.stop();
                    } else if (methodIndex == 1) {
                        clip.setFramePosition(0);
                        clip.start();
                    } else if (methodIndex == 2) {
                        clip.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                } else if (this.debug) {
                    System.out.println("SoundManager."
                            + methodLookup[methodIndex]
                            + "(\""
                            + clipName
                            + "\"): AudioClip not ready!");
                }
            } else if (this.debug) {
                System.out.println(
                        "SoundManager." + methodLookup[methodIndex] + "(\"" + clipName + "\"): SoundClip not found!");
                Thread.dumpStack();
            }
        } catch (Exception e) {
            System.out.println("SoundManager: Unexpected exception \"" + e + "\" when playing \"" + clipName + "\"");
        } catch (Error e) {
            System.out.println("SoundManager: Unexpected error \"" + e + "\" when playing \"" + clipName + "\"");
        }
    }
}
