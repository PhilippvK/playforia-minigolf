package com.aapeli.applet;

import com.aapeli.client.ImageManager;
import com.aapeli.client.Parameters;
import com.aapeli.client.SoundManager;
import com.aapeli.client.StringDraw;
import com.aapeli.client.TextManager;
import com.aapeli.connection.SocketConnection;
import com.aapeli.tools.QuickTimer;
import com.aapeli.tools.QuickTimerListener;
import com.aapeli.tools.Tools;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public abstract class AApplet extends Applet implements Runnable, ActionListener, QuickTimerListener {

    public static final int TEXT_CENTER = 0;
    public static final int TEXT_LOWERLEFT = 1;
    public static final int TEXT_LOWERMIDDLE = 2;
    public static final int END_ERROR_MATCH = 1;
    public static final int END_ERROR_CONNECTION = 2;
    public static final int END_ERROR_VERSION = 3;
    public static final int END_ERROR_SERVERFULL = 4;
    public static final int END_QUIT = 5;
    public static final int END_OTHER = 6;
    public static final int END_QUIT_REGISTER = 7;
    public static final int END_QUIT_BUYCOINS = 8;
    public static final int END_ERROR_KICK_NOW = 9;
    public static final int END_ERROR_KICKBAN_NOW = 10;
    public static final int END_ERROR_BAN_INIT = 11;
    public static final int END_ERROR_REGLOGIN_FAILED = 12;
    public static final int END_ERROR_TOOMANYIP_INIT = 13;
    public static final int END_THROWABLE = 14;
    private static final Font fontDialog15 = new Font("Dialog", Font.PLAIN, 15);
    private static final Font fontDialog12b = new Font("Dialog", Font.BOLD, 12);
    private static final Font fontDialog12 = new Font("Dialog", Font.PLAIN, 12);
    private static final Font fontDialog11 = new Font("Dialog", Font.PLAIN, 12);
    public int appletWidth;
    public int appletHeight;
    public Parameters param;
    public TextManager textManager;
    public ImageManager imageManager;
    public SoundManager soundManager;
    private ContentPanel contentPanel;
    private LoadingPanel loadingPanel;
    private String backgroundImageKey;
    private int backgroundXOffset;
    private int backgroundYOffset;
    private int endTextLocation;
    private int endState;
    private String endTextCustom;
    private Throwable aThrowable2553;
    private boolean drawTextOutline;
    private boolean notStarted;
    private boolean destroyed;
    private boolean ready;
    private RetryCanvas retryCanvas;
    private Image splashImage;
    private long splashTimestamp;
    private QuickTimer popupTimer;
    private SocketConnection socketConnection;
    private Image appletImage;
    private Graphics appletGraphics;
    private boolean verbose;

    public void init() {
        System.out.println("\n" + this.getAppletInfo() + "\n");
        this.appletWidth = 735;
        this.appletHeight = 525;
        this.backgroundImageKey = null;
        this.backgroundXOffset = 0;
        this.backgroundYOffset = 0;
        this.endTextLocation = 0;
        this.endState = 0;
        this.endTextCustom = null;
        this.aThrowable2553 = null;
        this.drawTextOutline = false;
        this.ready = false;
        this.notStarted = true;
        this.destroyed = false;
        this.verbose = false;
    }

    public void start() {
        if (this.notStarted && !this.destroyed) {
            this.notStarted = false;
            Thread t = new Thread(this);
            t.start();
        }
    }

    public void stop() {}

    public void destroy() {
        this.destroyed = true;
        this.removeLoadingPanel();
        this.setEndState(END_QUIT);

        try {
            this.destroyApplet();
        } catch (Exception e) {
        }

        if (this.contentPanel != null) {
            this.contentPanel.destroy();
            this.contentPanel = null;
        }

        if (this.soundManager != null) {
            this.soundManager.destroy();
        }

        if (this.imageManager != null) {
            this.imageManager.destroy();
        }

        if (this.textManager != null) {
            this.textManager.destroy();
        }

        if (this.param != null) {
            this.param.destroy();
        }

        this.soundManager = null;
        this.imageManager = null;
        this.textManager = null;
        this.param = null;
        if (this.appletGraphics != null) {
            this.appletGraphics.dispose();
            this.appletGraphics = null;
        }

        if (this.appletImage != null) {
            this.appletImage.flush();
            this.appletImage = null;
        }

        this.backgroundImageKey = null;
    }

    public String getAppletInfo() {
        return "-= Playforia Applet =-\nCopyright (c) Playforia (www.playforia.info)\nProgramming: Pasi Laaksonen";
    }

    public void paint(Graphics graphics) {
        this.update(graphics);
    }

    public void update(Graphics graphics) {
        if (!this.destroyed) {
            if (this.appletImage == null) {
                this.appletImage = this.createImage(this.appletWidth, this.appletHeight);
                this.appletGraphics = this.appletImage.getGraphics();
            }

            if (this.splashImage != null) {
                this.appletGraphics.drawImage(this.splashImage, 0, 0, this);
            } else {
                Color backgroundColor = this.getBackground();
                this.appletGraphics.setColor(backgroundColor);
                this.appletGraphics.fillRect(0, 0, this.appletWidth, this.appletHeight);
                if (this.imageManager != null && this.backgroundImageKey != null) {
                    Image image = this.imageManager.getGameImage(this.backgroundImageKey);
                    if (image != null) {
                        this.appletGraphics.drawImage(image, this.backgroundXOffset, this.backgroundYOffset, this);
                    }
                }

                if (this.textManager != null) {
                    this.appletGraphics.setColor(this.getForeground());
                    Color outlineColor = this.drawTextOutline ? backgroundColor : null;
                    if (this.endState == END_ERROR_CONNECTION) {
                        byte textYOffset = -20;
                        this.appletGraphics.setFont(fontDialog15);
                        StringDraw.drawOutlinedString(
                                this.appletGraphics,
                                outlineColor,
                                this.textManager.getShared("Message_CE_ConnectionError"),
                                40,
                                80 + textYOffset,
                                -1);
                        this.appletGraphics.setFont(fontDialog12);
                        StringDraw.drawOutlinedString(
                                this.appletGraphics,
                                outlineColor,
                                this.textManager.getShared("Message_CE_PossibleReasons"),
                                40,
                                125 + textYOffset,
                                -1);
                        if (!this.ready) {
                            this.appletGraphics.setFont(fontDialog12);
                            StringDraw.drawOutlinedString(
                                    this.appletGraphics,
                                    outlineColor,
                                    "- " + this.textManager.getShared("Message_CE0_1_Short"),
                                    40,
                                    160 + textYOffset,
                                    -1);
                            this.appletGraphics.setFont(fontDialog11);
                            StringDraw.drawOutlinedStringWithMaxWidth(
                                    this.appletGraphics,
                                    outlineColor,
                                    this.textManager.getShared(
                                            "Message_CE0_1_Long",
                                            this.param.getServerIp(),
                                            "" + this.param.getServerPort()),
                                    50,
                                    180 + textYOffset,
                                    -1,
                                    this.appletWidth - 50 - 50);
                            this.appletGraphics.setFont(fontDialog12);
                            StringDraw.drawOutlinedString(
                                    this.appletGraphics,
                                    outlineColor,
                                    "- " + this.textManager.getShared("Message_CE0_2_Short"),
                                    40,
                                    245 + textYOffset,
                                    -1);
                            this.appletGraphics.setFont(fontDialog11);
                            StringDraw.drawOutlinedStringWithMaxWidth(
                                    this.appletGraphics,
                                    outlineColor,
                                    this.textManager.getShared("Message_CE0_2_Long"),
                                    50,
                                    265 + textYOffset,
                                    -1,
                                    this.appletWidth - 50 - 50);
                            this.appletGraphics.setFont(fontDialog12);
                            StringDraw.drawOutlinedString(
                                    this.appletGraphics,
                                    outlineColor,
                                    "- " + this.textManager.getShared("Message_CE0_3_Short"),
                                    40,
                                    305 + textYOffset,
                                    -1);
                            this.appletGraphics.setFont(fontDialog11);
                            StringDraw.drawOutlinedStringWithMaxWidth(
                                    this.appletGraphics,
                                    outlineColor,
                                    this.textManager.getShared("Message_CE0_3_Long"),
                                    50,
                                    325 + textYOffset,
                                    -1,
                                    this.appletWidth - 50 - 50);
                        } else {
                            this.appletGraphics.setFont(fontDialog12);
                            StringDraw.drawOutlinedString(
                                    this.appletGraphics,
                                    outlineColor,
                                    "- " + this.textManager.getShared("Message_CE1_1_Short"),
                                    40,
                                    160 + textYOffset,
                                    -1);
                            this.appletGraphics.setFont(fontDialog11);
                            StringDraw.drawOutlinedStringWithMaxWidth(
                                    this.appletGraphics,
                                    outlineColor,
                                    this.textManager.getShared("Message_CE1_1_Long"),
                                    50,
                                    180 + textYOffset,
                                    -1,
                                    this.appletWidth - 50 - 50);
                            this.appletGraphics.setFont(fontDialog12);
                            StringDraw.drawOutlinedString(
                                    this.appletGraphics,
                                    outlineColor,
                                    "- " + this.textManager.getShared("Message_CE1_2_Short"),
                                    40,
                                    235 + textYOffset,
                                    -1);
                            this.appletGraphics.setFont(fontDialog11);
                            StringDraw.drawOutlinedStringWithMaxWidth(
                                    this.appletGraphics,
                                    outlineColor,
                                    this.textManager.getShared("Message_CE1_2_Long"),
                                    50,
                                    255 + textYOffset,
                                    -1,
                                    this.appletWidth - 50 - 50);
                            this.appletGraphics.setFont(fontDialog12);
                            StringDraw.drawOutlinedString(
                                    this.appletGraphics,
                                    outlineColor,
                                    "- " + this.textManager.getShared("Message_CE1_3_Short"),
                                    40,
                                    305 + textYOffset,
                                    -1);
                            this.appletGraphics.setFont(fontDialog11);
                            StringDraw.drawOutlinedStringWithMaxWidth(
                                    this.appletGraphics,
                                    outlineColor,
                                    this.textManager.getShared("Message_CE1_3_Long"),
                                    50,
                                    325 + textYOffset,
                                    -1,
                                    this.appletWidth - 50 - 50);
                        }
                    } else if (this.endState == END_THROWABLE) {
                        this.appletGraphics.setFont(fontDialog15);
                        StringDraw.drawOutlinedString(
                                this.appletGraphics,
                                outlineColor,
                                this.textManager.getShared("Message_PE_ProgramError"),
                                50,
                                100,
                                -1);
                        this.appletGraphics.setFont(fontDialog12);
                        StringDraw.drawOutlinedStringWithMaxWidth(
                                this.appletGraphics,
                                outlineColor,
                                this.textManager.getShared("Message_PE_GameClosed"),
                                50,
                                150,
                                -1,
                                this.appletWidth - 70 - 50);
                        this.appletGraphics.setFont(fontDialog12b);
                        StringDraw.drawOutlinedString(
                                this.appletGraphics,
                                outlineColor,
                                this.textManager.getShared("Message_PE_ErrorDesc", this.aThrowable2553.toString()),
                                50,
                                235,
                                -1);
                    } else {
                        String endText = this.textManager.getShared("Message_WaitWhile");
                        String endTextHelp = null;
                        if (this.endState == END_ERROR_MATCH) {
                            endText = this.textManager.getShared("Match_MessageError");
                            endTextHelp = this.textManager.getShared("Match_MessageErrorHelp");
                        } else if (this.endState == END_ERROR_VERSION) {
                            endText = this.textManager.getShared("Message_VersionError");
                            endTextHelp = this.textManager.getShared("Message_VersionErrorHelp");
                        } else if (this.endState == END_ERROR_SERVERFULL) {
                            endText = this.textManager.getShared("Message_ServerFullError");
                            endTextHelp = this.textManager.getShared("Message_ServerFullErrorHelp");
                        } else if (this.endState == END_QUIT) {
                            endText = this.textManager.getShared("Message_QuitGame");
                        } else if (this.endState == END_QUIT_REGISTER) {
                            endText = this.textManager.getShared("Message_QuitGame_ToRegister");
                        } else if (this.endState == END_QUIT_BUYCOINS) {
                            endText = this.textManager.getShared("Message_QuitGame_ToBuyCoins");
                        } else if (this.endState == END_OTHER) {
                            endText = this.endTextCustom;
                        } else if (this.endState == END_ERROR_KICK_NOW) {
                            endText = this.textManager.getShared("Message_KickedNow");
                            endTextHelp = this.textManager.getShared("Message_KickedNowHelp");
                        } else if (this.endState == END_ERROR_KICKBAN_NOW) {
                            endText = this.textManager.getShared("Message_BannedNow");
                            endTextHelp = this.textManager.getShared("Message_BannedNowHelp");
                        } else if (this.endState == END_ERROR_BAN_INIT) {
                            endText = this.textManager.getShared("Message_BannedInitially");
                            endTextHelp = this.textManager.getShared("Message_BannedInitiallyHelp");
                        } else if (this.endState == END_ERROR_REGLOGIN_FAILED) {
                            endText = this.textManager.getShared("Message_LoginFailedReg");
                            endTextHelp = this.textManager.getShared("Message_LoginFailedRegHelp");
                        } else if (this.endState == END_ERROR_TOOMANYIP_INIT) {
                            endText = this.textManager.getShared("Message_TooManySameIP");
                            endTextHelp = this.textManager.getShared("Message_TooManySameIPHelp");
                        }

                        this.appletGraphics.setFont(fontDialog15);
                        if (this.endTextLocation == TEXT_CENTER) {
                            StringDraw.drawOutlinedString(
                                    this.appletGraphics,
                                    outlineColor,
                                    endText,
                                    this.appletWidth / 2,
                                    this.appletHeight / 2 - 10,
                                    0);
                        } else if (this.endTextLocation == TEXT_LOWERLEFT) {
                            StringDraw.drawOutlinedString(
                                    this.appletGraphics,
                                    outlineColor,
                                    endText,
                                    this.appletWidth / 12,
                                    this.appletHeight - 120,
                                    -1);
                        } else if (this.endTextLocation == TEXT_LOWERMIDDLE) {
                            StringDraw.drawOutlinedString(
                                    this.appletGraphics,
                                    outlineColor,
                                    endText,
                                    this.appletWidth / 2,
                                    this.appletHeight - 120,
                                    0);
                        }

                        if (endTextHelp != null) {
                            this.appletGraphics.setFont(fontDialog12);
                            if (this.endTextLocation == TEXT_CENTER) {
                                StringDraw.drawOutlinedStringWithMaxWidth(
                                        this.appletGraphics,
                                        outlineColor,
                                        endTextHelp,
                                        this.appletWidth / 2,
                                        this.appletHeight / 2 + 30,
                                        0,
                                        (int) ((double) this.appletWidth * 0.8D));
                            } else if (this.endTextLocation == TEXT_LOWERLEFT) {
                                StringDraw.drawOutlinedStringWithMaxWidth(
                                        this.appletGraphics,
                                        outlineColor,
                                        endTextHelp,
                                        this.appletWidth / 12,
                                        this.appletHeight - 80,
                                        -1,
                                        (int) ((double) this.appletWidth * 0.6D));
                            } else if (this.endTextLocation == TEXT_LOWERMIDDLE) {
                                StringDraw.drawOutlinedStringWithMaxWidth(
                                        this.appletGraphics,
                                        outlineColor,
                                        endTextHelp,
                                        this.appletWidth / 2,
                                        this.appletHeight - 80,
                                        0,
                                        (int) ((double) this.appletWidth * 0.5D));
                            }
                        }
                    }
                }
            }

            int x = (this.getWidth() - appletImage.getWidth(null)) / 2;
            int y = (this.getHeight() - appletImage.getHeight(null)) / 2;
            graphics.drawImage(this.appletImage, x, y, this);
        }
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        this.setLayout(new GridBagLayout());
        this.loadingPanel = new LoadingPanel(this);
        this.add(this.loadingPanel);
        this.revalidate();
        this.loadingPanel.start();
        this.param = new Parameters(this, this.isDebug());
        String initMessage = this.param.getParameter("initmessage");
        this.verbose = Boolean.parseBoolean(this.param.getParameter("verbose"));
        if (initMessage != null && initMessage.indexOf('|') == -1) {
            this.loadingPanel.setLoadingMessage(initMessage);
        }

        this.initApplet(this.param);
        this.loadingPanel.setBackground(this.getBackground());
        this.callJavaScriptJSON("{\"loading\":\"started\"}");
        if (this.endState == 0 && !this.destroyed) {
            int time1 = (int) (System.currentTimeMillis() - startTime);
            boolean startupDebug = false;
            String startupDebugParameter = this.param.getParameter("startupdebug");
            if (startupDebugParameter != null && Tools.getBoolean(startupDebugParameter)) {
                startupDebug = true;
                this.printSUD("StartUp Debug enabled!");
            }

            AdCanvas adCanvas = AdCanvas.create(this, this.param);
            if (adCanvas != null) {
                if (startupDebug) {
                    this.printSUD("Loading ad-image...");
                }

                this.loadingPanel.setActualProgress(0.25D);
                adCanvas.method212();

                while (!adCanvas.method213()) {
                    Tools.sleep(50L);
                    if (this.destroyed) {
                        adCanvas.method217();
                        return;
                    }
                }

                this.loadingPanel.method466(adCanvas, Tools.getBoolean(this.param.getParameter("ad_clicktocontinue")));
                if (startupDebug) {
                    this.printSUD("...done");
                }
            } else if (startupDebug) {
                this.printSUD("No ad-image");
            }

            int time2 = (int) (System.currentTimeMillis() - startTime);
            if (startupDebug) {
                this.printSUD("Creating text manager");
            }

            this.loadingPanel.setActualProgress(0.5D);
            this.textManager = new TextManager(this.param, true, this.isDebug());
            this.loadingPanel.init(this.param, this.textManager);
            if (startupDebug) {
                this.printSUD("Loading texts...");
            }

            this.textManager.waitLoadingFinished();
            this.textsLoadedNotify(this.textManager);
            if (!this.destroyed) {
                if (startupDebug) {
                    this.printSUD("...done");
                }

                String adInfo = null;
                if (adCanvas != null && adCanvas.method216()) {
                    adInfo = " " + this.textManager.getShared("Loader_AdClickNote");
                }

                int time3 = (int) (System.currentTimeMillis() - startTime);
                if (System.currentTimeMillis() < startTime + 3000L) {
                    this.loadingPanel.method468(2.0D);
                }

                this.callJavaScriptJSON("{\"loading\":\"inprogress\"}");
                if (startupDebug) {
                    this.printSUD("Creating sound manager");
                }

                this.loadingPanel.setLoadingMessage(
                        this.textManager.getShared("Loader_LoadingGfxSfx") + (adInfo != null ? adInfo : ""));
                this.soundManager = new SoundManager(true, this.isDebug());
                if (startupDebug) {
                    this.soundManager.enableSUD();
                }

                this.loadingPanel.addProgress(0.15D);
                if (startupDebug) {
                    this.printSUD("Defining sounds...");
                }

                this.defineSounds(this.soundManager);
                if (!this.destroyed) {
                    int time4 = (int) (System.currentTimeMillis() - startTime);
                    if (startupDebug) {
                        this.printSUD("...done");
                    }

                    if (startupDebug) {
                        this.printSUD("Creating image manager");
                    }

                    this.imageManager = new ImageManager(this.isDebug());

                    this.loadingPanel.addProgress(0.05D);
                    this.defineImages(this.imageManager);
                    if (!this.destroyed) {
                        if (startupDebug) {
                            this.printSUD("Loading images...");
                        }

                        this.loadingPanel.setActualProgress(0.7D + this.imageManager.getImageLoadProgress() * 0.15D);

                        int time5 = (int) (System.currentTimeMillis() - startTime);
                        if (startupDebug) {
                            this.printSUD("...done");
                        }

                        if (startupDebug) {
                            this.printSUD("Creating images...");
                        }

                        this.loadingPanel.addProgress(0.05D);
                        this.createImages();
                        if (startupDebug) {
                            this.printSUD("...done");
                        }

                        if (startupDebug) {
                            this.printSUD("Defining secondary images");
                        }

                        this.defineSecImages(this.imageManager, this.param.getSiteName());
                        if (!this.destroyed) {
                            this.soundManager.startLoading();
                            if (System.currentTimeMillis() < startTime + 7000L) {
                                this.loadingPanel.method468(2.0D);
                            }

                            if (!this.destroyed) {
                                int time6 = (int) (System.currentTimeMillis() - startTime);
                                if (startupDebug) {
                                    this.printSUD("Connecting to server...");
                                }

                                this.loadingPanel.setLoadingMessage(this.textManager.getShared("Message_Connecting")
                                        + (adInfo != null ? adInfo : ""));
                                this.loadingPanel.setActualProgress(1.0D);
                                this.connectToServer();
                                if (startupDebug) {
                                    this.printSUD("...done");
                                }

                                if (this.endState == 0) {
                                    int readyTime = (int) (System.currentTimeMillis() - startTime);
                                    this.ready = true;
                                    if (startupDebug) {
                                        this.printSUD("Waiting loader screen to finish...");
                                    }

                                    this.callJavaScriptJSON("{\"loading\":\"finished\"}");
                                    this.loadingPanel.method468(5.0D);
                                    this.loadingPanel.method470();

                                    LoadingPanel loadingPanel;
                                    do {
                                        loadingPanel = this.loadingPanel;
                                        if (this.destroyed || loadingPanel == null) {
                                            return;
                                        }

                                        Tools.sleep(50L);
                                    } while (!loadingPanel.isLoaded());

                                    int finishedTime = (int) (System.currentTimeMillis() - startTime);
                                    if (startupDebug) {
                                        this.printSUD("...done");
                                    }

                                    this.sendLoadTimes(
                                            readyTime, finishedTime, time1, time2, time3, time4, time5, time6);
                                    this.writeMetadataLog1(
                                            "clientconnect",
                                            "loadtime:i:" + readyTime + "^loadertime:i:" + finishedTime);
                                    this.loadingPanel.displayButtons();
                                    if (this.endState == 0 && !this.destroyed) {
                                        this.remove(this.loadingPanel);
                                        this.loadingPanel.destroy();
                                        this.loadingPanel = null;
                                        if (!this.destroyed) {
                                            if (startupDebug) {
                                                this.printSUD("Adding applet content...");
                                            }

                                            this.contentPanel = new ContentPanel(this);
                                            if (this.backgroundImageKey != null) {
                                                this.contentPanel.setBackground(
                                                        this.imageManager,
                                                        this.backgroundImageKey,
                                                        this.backgroundXOffset,
                                                        this.backgroundYOffset);
                                            }

                                            this.contentPanel.setVisible(false);
                                            this.add(this.contentPanel);
                                            if (startupDebug) {
                                                this.printSUD("...done");
                                            }

                                            if (startupDebug) {
                                                this.printSUD("Moving control to game itself");
                                            }

                                            this.appletReady();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void actionPerformed(ActionEvent action) {
        if (action.getSource() == this.retryCanvas) {
            try {
                this.retryCanvas.setVisible(false);
                this.destroy();
                this.init();
                this.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void qtFinished() {
        this.allowExternalPopups();
    }

    public void setBackground(String imageKey) {
        this.setBackground(imageKey, 0, 0);
    }

    public void setBackground(String imageKey, int xOffset, int yOffset) {
        this.backgroundImageKey = imageKey;
        this.backgroundXOffset = xOffset;
        this.backgroundYOffset = yOffset;
        if (this.contentPanel != null) {
            this.contentPanel.setBackground(this.imageManager, imageKey, xOffset, yOffset);
        }

        this.repaint();
    }

    public void setTextLocation(int textLocation) {
        this.endTextLocation = textLocation;
        this.repaint();
    }

    public void setTextOutline(boolean drawTextOutline) {
        this.drawTextOutline = drawTextOutline;
        this.repaint();
    }

    public void setEndState(int state) {
        if (this.endState == 0) {
            this.endState = state;
            this.removeLoadingPanel();
            if (this.contentPanel != null) {
                this.contentPanel.destroy();
            }

            int x = (this.getWidth() - this.appletWidth) / 2;
            int y = (this.getHeight() - this.appletHeight) / 2;
            if (state == END_ERROR_CONNECTION) {
                this.retryCanvas = new RetryCanvas(this.textManager.getShared("Message_CE_RetryButton"), 120, 20, this);
                this.retryCanvas.setLocation(x + 40, y + 360);
                this.add(this.retryCanvas);
            } else if (state == END_THROWABLE) {
                this.retryCanvas = new RetryCanvas(this.textManager.getShared("Message_PE_RetryButton"), 120, 20, this);
                this.retryCanvas.setLocation(x + 50, y + 360);
                this.add(this.retryCanvas);
            }

            this.splashImage = null;
            this.repaint();
        }
    }

    public void setEndState(String endMessage) {
        this.endTextCustom = endMessage;
        this.setEndState(END_OTHER);
    }

    public void setEndState(Throwable var1) {
        var1.printStackTrace();
        this.aThrowable2553 = var1;
        this.setEndState(END_THROWABLE);
    }

    public void clearContent() {
        if (this.endState == 0) {
            this.contentPanel.destroy();
        }
    }

    public void addToContent(Component component) {
        if (this.endState == 0) {
            this.contentPanel.add(component);
        }
    }

    public void contentReady() {
        if (this.endState == 0) {
            this.contentPanel.makeVisible();
            this.revalidate();
        }
    }

    /** SUD == startup debug */
    public void printSUD(String var1) {
        System.out.println("SUD(" + System.currentTimeMillis() + "): " + var1);
    }

    public abstract void initApplet(Parameters parameters);

    public void textsLoadedNotify(TextManager var1) {}

    public abstract void defineSounds(SoundManager soundManager);

    public abstract void defineImages(ImageManager imageManager);

    public abstract void createImages();

    public void defineSecImages(ImageManager imageManager, String var2) {}

    public abstract void connectToServer();

    public abstract void appletReady();

    public abstract void destroyApplet();

    public abstract boolean isDebug();

    public void showSplash(Image img) {
        this.splashImage = img;
        this.splashTimestamp = System.currentTimeMillis();
    }

    public void waitAndRemoveSplash(int millis, boolean noRepaint) {
        if (this.splashImage != null) {
            long var3 = this.splashTimestamp + (long) millis;

            while (System.currentTimeMillis() < var3) {
                Tools.sleep(100L);
            }

            this.splashImage = null;
            if (!noRepaint) {
                this.repaint();
            }
        }
    }

    public boolean callJavaScriptJSON(String json) {
        return this.param.callJavaScriptJSON(json);
    }

    public void blockExternalPopups() {
        this.resetPopupTimer();
        this.callJavaScriptJSON("{\"block\":\"true\"}");
    }

    public void blockExternalPopups(int var1) {
        this.blockExternalPopups();
        this.popupTimer = new QuickTimer(var1, this);
    }

    public void allowExternalPopups() {
        this.resetPopupTimer();
        this.callJavaScriptJSON("{\"block\":\"false\"}");
    }

    public void setConnectionReference(SocketConnection var1) {
        this.socketConnection = var1;
    }

    public void writeMetadataLog0(String dataType, String data) {
        if (this.socketConnection != null) {
            this.socketConnection.writeMetadataLog(0, dataType, data);
        }
    }

    public void writeMetadataLog1(String dataType, String data) {
        if (this.socketConnection != null) {
            this.socketConnection.writeMetadataLog(1, dataType, data);
        }
    }

    private void removeLoadingPanel() {
        LoadingPanel var1 = this.loadingPanel;
        if (var1 != null) {
            this.remove(var1);
            var1.destroy();
            var1 = null;
            this.loadingPanel = null;
        }
    }

    private void sendLoadTimes(
            int readyTime, int finishedTime, int time1, int time2, int time3, int time4, int time5, int time6) {
        if (this.isDebug()) {
            System.out.println("AApplet.sendLoadTimes(" + readyTime + "," + finishedTime + ")");
        }

        try {
            String loadTimesPage = this.param.getParameter("ld_page");
            if (loadTimesPage == null) {
                return;
            }

            if (!loadTimesPage.toLowerCase().startsWith("javascript:")) {
                return;
            }

            String javaVersion = this.getSystemProperty("java.version");
            String javaVendor = this.getSystemProperty("java.vendor");
            if (javaVendor.length() > 128) {
                javaVendor = javaVendor.substring(0, 128);
            }

            String queryUrl = Tools.replaceFirst(loadTimesPage, "%v", javaVersion);
            queryUrl = Tools.replaceFirst(queryUrl, "%w", javaVendor);
            queryUrl = Tools.replaceFirst(queryUrl, "%r", "" + readyTime);
            queryUrl = Tools.replaceFirst(queryUrl, "%f", "" + finishedTime);
            queryUrl = Tools.replaceFirst(queryUrl, "%1", "" + time1);
            queryUrl = Tools.replaceFirst(queryUrl, "%2", "" + time2);
            queryUrl = Tools.replaceFirst(queryUrl, "%3", "" + time3);
            queryUrl = Tools.replaceFirst(queryUrl, "%4", "" + time4);
            queryUrl = Tools.replaceFirst(queryUrl, "%5", "" + time5);
            queryUrl = Tools.replaceFirst(queryUrl, "%6", "" + time6);
            URL url = new URL(queryUrl);
            if (this.isDebug()) {
                System.out.println("AApplet.sendLoadTimes(...): Displaying page \"" + url + "\"");
            }

            this.getAppletContext().showDocument(url);
        } catch (Exception e) {
        }
    }

    private String getSystemProperty(String key) {
        try {
            String result = System.getProperty(key);
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
        } catch (Error e) {
        }

        return "";
    }

    private void resetPopupTimer() {
        QuickTimer var1 = this.popupTimer;
        this.popupTimer = null;
        if (var1 != null) {
            var1.stopAll();
        }
    }
}
