package com.aapeli.applet;

import com.aapeli.client.ImageManager;
import com.aapeli.client.Parameters;
import com.aapeli.client.SoundManager;
import com.aapeli.client.StringDraw;
import com.aapeli.client.TextManager;
import com.aapeli.connection.Connection;
import com.aapeli.credit.Product;
import com.aapeli.tools.QuickTimer;
import com.aapeli.tools.QuickTimerListener;
import com.aapeli.tools.Tools;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Optional;

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
    private static final Font fontDialog15 = new Font("Dialog", 0, 15);
    private static final Font fontDialog12b = new Font("Dialog", 1, 12);
    private static final Font fontDialog12 = new Font("Dialog", 0, 12);
    private static final Font fontDialog11 = new Font("Dialog", 0, 12);
    public int appletWidth;
    public int appletHeight;
    public Parameters param;
    public TextManager textManager;
    public ImageManager imageManager;
    public SoundManager soundManager;
    private ContentPanel contentPanel;
    private LoadingPanel loadingPanel;
    private String aString2547;
    private int anInt2548;
    private int anInt2549;
    private int endTextLocation;
    private int endState;
    private String endTextCustom;
    private Throwable aThrowable2553;
    private boolean aBoolean2554;
    private boolean aBoolean2555;
    private boolean destroyed;
    private boolean aBoolean2557;
    private RetryCanvas retryCanvas;
    private Image splashImage;
    private long splashTimestamp;
    private QuickTimer popupTimer;
    private Connection connection;
    private Image appletImage;
    private Graphics appletGraphics;
    private boolean verbose;

    public void init() {
        System.out.println("\n" + this.getAppletInfo() + "\n");
        Dimension dim = this.getSize();
        this.appletWidth = dim.width;
        this.appletHeight = dim.height;
        this.aString2547 = null;
        this.anInt2548 = this.anInt2549 = 0;
        this.endTextLocation = 0;
        this.endState = 0;
        this.endTextCustom = null;
        this.aThrowable2553 = null;
        this.aBoolean2554 = false;
        this.aBoolean2557 = false;
        this.aBoolean2555 = true;
        this.destroyed = false;
        this.verbose = false;
    }

    public void start() {
        if (this.aBoolean2555 && !this.destroyed) {
            this.aBoolean2555 = false;
            Thread t = new Thread(this);
            t.start();
        }

    }

    public void stop() {
    }

    public void destroy() {
        this.destroyed = true;
        this.removeLoadingPanel();
        this.setEndState(END_QUIT);

        try {
            this.destroyApplet();
        } catch (Exception var2) {
            ;
        }

        if (this.contentPanel != null) {
            this.contentPanel.dispose();
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

        this.aString2547 = null;
    }

    public String getAppletInfo() {
        return "-= Playforia Applet =-\nCopyright (c) Playforia (www.playforia.info)\nProgramming: Pasi Laaksonen";
    }

    public void paint(Graphics var1) {
        this.update(var1);
    }

    public void update(Graphics var1) {
        if (!this.destroyed) {
            if (this.appletImage == null) {
                this.appletImage = this.createImage(this.appletWidth, this.appletHeight);
                this.appletGraphics = this.appletImage.getGraphics();
            }

            if (this.splashImage != null) {
                this.appletGraphics.drawImage(this.splashImage, 0, 0, this);
            } else {
                Color var2 = this.getBackground();
                this.appletGraphics.setColor(var2);
                this.appletGraphics.fillRect(0, 0, this.appletWidth, this.appletHeight);
                if (this.imageManager != null && this.aString2547 != null) {
                    Image var3 = this.imageManager.getIfAvailable(this.aString2547);
                    if (var3 != null) {
                        this.appletGraphics.drawImage(var3, this.anInt2548, this.anInt2549, this);
                    }
                }

                if (this.textManager != null) {
                    this.appletGraphics.setColor(this.getForeground());
                    Color var6 = this.aBoolean2554 ? var2 : null;
                    if (this.endState == END_ERROR_CONNECTION) {
                        byte var4 = -20;
                        this.appletGraphics.setFont(fontDialog15);
                        StringDraw.drawOutlinedString(this.appletGraphics, var6, this.textManager.getShared("Message_CE_ConnectionError"), 40, 80 + var4, -1);
                        this.appletGraphics.setFont(fontDialog12);
                        StringDraw.drawOutlinedString(this.appletGraphics, var6, this.textManager.getShared("Message_CE_PossibleReasons"), 40, 125 + var4, -1);
                        if (!this.aBoolean2557) {
                            this.appletGraphics.setFont(fontDialog12);
                            StringDraw.drawOutlinedString(this.appletGraphics, var6, "- " + this.textManager.getShared("Message_CE0_1_Short"), 40, 160 + var4, -1);
                            this.appletGraphics.setFont(fontDialog11);
                            StringDraw.drawOutlinedStringWithMaxWidth(this.appletGraphics, var6, this.textManager.getShared("Message_CE0_1_Long", this.param.getServerIp(), "" + this.param.getServerPort()), 50, 180 + var4, -1, this.appletWidth - 50 - 50);
                            this.appletGraphics.setFont(fontDialog12);
                            StringDraw.drawOutlinedString(this.appletGraphics, var6, "- " + this.textManager.getShared("Message_CE0_2_Short"), 40, 245 + var4, -1);
                            this.appletGraphics.setFont(fontDialog11);
                            StringDraw.drawOutlinedStringWithMaxWidth(this.appletGraphics, var6, this.textManager.getShared("Message_CE0_2_Long"), 50, 265 + var4, -1, this.appletWidth - 50 - 50);
                            this.appletGraphics.setFont(fontDialog12);
                            StringDraw.drawOutlinedString(this.appletGraphics, var6, "- " + this.textManager.getShared("Message_CE0_3_Short"), 40, 305 + var4, -1);
                            this.appletGraphics.setFont(fontDialog11);
                            StringDraw.drawOutlinedStringWithMaxWidth(this.appletGraphics, var6, this.textManager.getShared("Message_CE0_3_Long"), 50, 325 + var4, -1, this.appletWidth - 50 - 50);
                        } else {
                            this.appletGraphics.setFont(fontDialog12);
                            StringDraw.drawOutlinedString(this.appletGraphics, var6, "- " + this.textManager.getShared("Message_CE1_1_Short"), 40, 160 + var4, -1);
                            this.appletGraphics.setFont(fontDialog11);
                            StringDraw.drawOutlinedStringWithMaxWidth(this.appletGraphics, var6, this.textManager.getShared("Message_CE1_1_Long"), 50, 180 + var4, -1, this.appletWidth - 50 - 50);
                            this.appletGraphics.setFont(fontDialog12);
                            StringDraw.drawOutlinedString(this.appletGraphics, var6, "- " + this.textManager.getShared("Message_CE1_2_Short"), 40, 235 + var4, -1);
                            this.appletGraphics.setFont(fontDialog11);
                            StringDraw.drawOutlinedStringWithMaxWidth(this.appletGraphics, var6, this.textManager.getShared("Message_CE1_2_Long"), 50, 255 + var4, -1, this.appletWidth - 50 - 50);
                            this.appletGraphics.setFont(fontDialog12);
                            StringDraw.drawOutlinedString(this.appletGraphics, var6, "- " + this.textManager.getShared("Message_CE1_3_Short"), 40, 305 + var4, -1);
                            this.appletGraphics.setFont(fontDialog11);
                            StringDraw.drawOutlinedStringWithMaxWidth(this.appletGraphics, var6, this.textManager.getShared("Message_CE1_3_Long"), 50, 325 + var4, -1, this.appletWidth - 50 - 50);
                        }
                    } else if (this.endState == END_THROWABLE) {
                        this.appletGraphics.setFont(fontDialog15);
                        StringDraw.drawOutlinedString(this.appletGraphics, var6, this.textManager.getShared("Message_PE_ProgramError"), 50, 100, -1);
                        this.appletGraphics.setFont(fontDialog12);
                        StringDraw.drawOutlinedStringWithMaxWidth(this.appletGraphics, var6, this.textManager.getShared("Message_PE_GameClosed"), 50, 150, -1, this.appletWidth - 70 - 50);
                        this.appletGraphics.setFont(fontDialog12b);
                        StringDraw.drawOutlinedString(this.appletGraphics, var6, this.textManager.getShared("Message_PE_ErrorDesc", this.aThrowable2553.toString()), 50, 235, -1);
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
                            StringDraw.drawOutlinedString(this.appletGraphics, var6, endText, this.appletWidth / 2, this.appletHeight / 2 - 10, 0);
                        } else if (this.endTextLocation == TEXT_LOWERLEFT) {
                            StringDraw.drawOutlinedString(this.appletGraphics, var6, endText, this.appletWidth / 12, this.appletHeight - 120, -1);
                        } else if (this.endTextLocation == TEXT_LOWERMIDDLE) {
                            StringDraw.drawOutlinedString(this.appletGraphics, var6, endText, this.appletWidth / 2, this.appletHeight - 120, 0);
                        }

                        if (endTextHelp != null) {
                            this.appletGraphics.setFont(fontDialog12);
                            if (this.endTextLocation == TEXT_CENTER) {
                                StringDraw.drawOutlinedStringWithMaxWidth(this.appletGraphics, var6, endTextHelp, this.appletWidth / 2, this.appletHeight / 2 + 30, 0, (int) ((double) this.appletWidth * 0.8D));
                            } else if (this.endTextLocation == TEXT_LOWERLEFT) {
                                StringDraw.drawOutlinedStringWithMaxWidth(this.appletGraphics, var6, endTextHelp, this.appletWidth / 12, this.appletHeight - 80, -1, (int) ((double) this.appletWidth * 0.6D));
                            } else if (this.endTextLocation == TEXT_LOWERMIDDLE) {
                                StringDraw.drawOutlinedStringWithMaxWidth(this.appletGraphics, var6, endTextHelp, this.appletWidth / 2, this.appletHeight - 80, 0, (int) ((double) this.appletWidth * 0.5D));
                            }
                        }
                    }
                }
            }

            var1.drawImage(this.appletImage, 0, 0, this);
        }
    }

    public void run() {
        long var1 = System.currentTimeMillis();
        this.setLayout((LayoutManager) null);
        this.loadingPanel = new LoadingPanel(this);
        this.loadingPanel.setBounds(0, 0, this.appletWidth, this.appletHeight);
        this.add(this.loadingPanel);
        this.loadingPanel.method463();
        this.param = new Parameters(this, this.isDebug());
        String var3 = this.param.getParameter("initmessage");
        this.verbose = Boolean.parseBoolean(this.param.getParameter("verbose"));
        if (var3 != null && var3.indexOf(124) == -1) {
            this.loadingPanel.setLoadingMessage(var3);
        }

        this.initApplet(this.param);
        this.loadingPanel.setBackground(this.getBackground());
        this.callJavaScriptJSON("{\"loading\":\"started\"}");
        if (this.endState == 0 && !this.destroyed) {
            int var4 = (int) (System.currentTimeMillis() - var1);
            boolean var5 = false;
            String var6 = this.param.getParameter("startupdebug");
            if (var6 != null && Tools.getBoolean(var6)) {
                var5 = true;
                this.printSUD("StartUp Debug enabled!");
            }

            AdCanvas var16 = AdCanvas.method211(this, this.param);
            if (var16 != null) {
                if (var5) {
                    this.printSUD("Loading ad-image...");
                }

                this.loadingPanel.method467(0.25D);
                var16.method212();

                while (!var16.method213()) {
                    Tools.sleep(50L);
                    if (this.destroyed) {
                        var16.method217();
                        return;
                    }
                }

                this.loadingPanel.method466(var16, Tools.getBoolean(this.param.getParameter("ad_clicktocontinue")));
                if (var5) {
                    this.printSUD("...done");
                }
            } else if (var5) {
                this.printSUD("No ad-image");
            }

            int var7 = (int) (System.currentTimeMillis() - var1);
            if (var5) {
                this.printSUD("Creating text manager");
            }

            this.loadingPanel.method467(0.5D);
            this.textManager = new TextManager(this.param, true, this.isDebug());
            this.loadingPanel.method462(this.param, this.textManager);
            if (var5) {
                this.printSUD("Loading texts...");
            }

            this.textManager.waitLoadingFinished();
            this.textsLoadedNotify(this.textManager);
            if (!this.destroyed) {
                if (var5) {
                    this.printSUD("...done");
                }

                String var8 = null;
                if (var16 != null && var16.method216()) {
                    var8 = " " + this.textManager.getShared("Loader_AdClickNote");
                }

                int var9 = (int) (System.currentTimeMillis() - var1);
                if (System.currentTimeMillis() < var1 + 3000L) {
                    this.loadingPanel.method468(2.0D);
                }

                this.callJavaScriptJSON("{\"loading\":\"inprogress\"}");
                if (var5) {
                    this.printSUD("Creating sound manager");
                }

                this.loadingPanel.setLoadingMessage(this.textManager.getShared("Loader_LoadingGfxSfx") + (var8 != null ? var8 : ""));
                this.soundManager = new SoundManager(this, false, this.isDebug());
                if (var5) {
                    this.soundManager.enableSUD(this);
                }

                this.loadingPanel.method465(0.15D);
                if (var5) {
                    this.printSUD("Defining sounds...");
                }

                this.defineSounds(this.soundManager);
                if (!this.destroyed) {
                    int var10 = (int) (System.currentTimeMillis() - var1);
                    if (var5) {
                        this.printSUD("...done");
                    }

                    if (var5) {
                        this.printSUD("Creating image manager");
                    }

                    this.imageManager = new ImageManager(this, this.isDebug());
                    if (var5) {
                        this.imageManager.enableSUD(this);
                    }

                    this.imageManager.setImageAliases(this.param.getImageAliases());
                    this.loadingPanel.method465(0.05D);
                    this.defineImages(this.imageManager, this.param.getSiteName());
                    if (!this.destroyed) {
                        this.imageManager.startLoadingImages();
                        if (var5) {
                            this.printSUD("Loading images...");
                        }

                        while (!this.imageManager.isLoadingFinished()) {
                            Tools.sleep(50L);
                            if (this.destroyed) {
                                return;
                            }

                            this.loadingPanel.method467(0.7D + this.imageManager.getImageLoadProgress() * 0.15D);
                        }

                        int var11 = (int) (System.currentTimeMillis() - var1);
                        if (var5) {
                            this.printSUD("...done");
                        }

                        if (var5) {
                            this.printSUD("Creating images...");
                        }

                        this.loadingPanel.method465(0.05D);
                        this.createImages();
                        if (var5) {
                            this.printSUD("...done");
                        }

                        if (var5) {
                            this.printSUD("Defining secondary images");
                        }

                        this.defineSecImages(this.imageManager, this.param.getSiteName());
                        if (!this.destroyed) {
                            this.imageManager.startLoadingImages();
                            this.soundManager.startLoading();
                            if (System.currentTimeMillis() < var1 + 7000L) {
                                this.loadingPanel.method468(2.0D);
                            }

                            if (!this.destroyed) {
                                int var12 = (int) (System.currentTimeMillis() - var1);
                                if (var5) {
                                    this.printSUD("Connecting to server...");
                                }

                                this.loadingPanel.setLoadingMessage(this.textManager.getShared("Message_Connecting") + (var8 != null ? var8 : ""));
                                this.loadingPanel.method467(1.0D);
                                this.connectToServer();
                                if (var5) {
                                    this.printSUD("...done");
                                }

                                if (this.endState == 0) {
                                    int var13 = (int) (System.currentTimeMillis() - var1);
                                    this.aBoolean2557 = true;
                                    if (var5) {
                                        this.printSUD("Waiting loader screen to finish...");
                                    }

                                    this.callJavaScriptJSON("{\"loading\":\"finished\"}");
                                    this.loadingPanel.method468(5.0D);
                                    this.loadingPanel.method470();

                                    LoadingPanel var14;
                                    do {
                                        var14 = this.loadingPanel;
                                        if (this.destroyed || var14 == null) {
                                            return;
                                        }

                                        Tools.sleep(50L);
                                    } while (!var14.method471());

                                    int var15 = (int) (System.currentTimeMillis() - var1);
                                    if (var5) {
                                        this.printSUD("...done");
                                    }

                                    this.sendLoadTimes(var13, var15, var4, var7, var9, var10, var11, var12);
                                    this.writeThriftDebug("clientconnect", "loadtime:i:" + var13 + "^loadertime:i:" + var15);
                                    this.loadingPanel.method472();
                                    if (this.endState == 0 && !this.destroyed) {
                                        this.remove(this.loadingPanel);
                                        this.loadingPanel.destroy();
                                        this.loadingPanel = null;
                                        if (!this.destroyed) {
                                            if (var5) {
                                                this.printSUD("Adding applet content...");
                                            }

                                            this.contentPanel = new ContentPanel(this);
                                            this.contentPanel.setBounds(0, 0, this.appletWidth, this.appletHeight);
                                            if (this.aString2547 != null) {
                                                this.contentPanel.setBackground(this.imageManager, this.aString2547, this.anInt2548, this.anInt2549);
                                            }

                                            this.contentPanel.setVisible(false);
                                            this.add(this.contentPanel);
                                            if (var5) {
                                                this.printSUD("...done");
                                            }

                                            if (var5) {
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

    public void actionPerformed(ActionEvent var1) {
        if (var1.getSource() == this.retryCanvas) {
            try {
                String var2 = this.getDocumentBase().toString();
                if (var2.indexOf(63) == -1) {
                    var2 = var2 + "?retry=1";
                } else if (var2.indexOf("retry=") == -1) {
                    var2 = var2 + "&retry=1";
                }

                this.getAppletContext().showDocument(new URL(var2));
            } catch (Exception var3) {
                this.retryCanvas.setVisible(false);
            }
        }

    }

    public void qtFinished() {
        this.allowExternalPopups();
    }

    public void setBackground(String var1) {
        this.setBackground(var1, 0, 0);
    }

    public void setBackground(String var1, int var2, int var3) {
        this.aString2547 = var1;
        this.anInt2548 = var2;
        this.anInt2549 = var3;
        if (this.contentPanel != null) {
            this.contentPanel.setBackground(this.imageManager, var1, var2, var3);
        }

        this.repaint();
    }

    public void setTextLocation(int var1) {
        this.endTextLocation = var1;
        this.repaint();
    }

    public void setTextOutline(boolean var1) {
        this.aBoolean2554 = var1;
        this.repaint();
    }

    public void setEndState(int var1) {
        if (this.endState == 0) {
            this.endState = var1;
            this.removeLoadingPanel();
            if (this.contentPanel != null) {
                this.contentPanel.dispose();
            }

            if (var1 == END_ERROR_CONNECTION) {
                this.retryCanvas = new RetryCanvas(this.textManager.getShared("Message_CE_RetryButton"), 120, 20, this);
                this.retryCanvas.setLocation(40, 360);
                this.add(this.retryCanvas);
            } else if (var1 == END_THROWABLE) {
                this.retryCanvas = new RetryCanvas(this.textManager.getShared("Message_PE_RetryButton"), 120, 20, this);
                this.retryCanvas.setLocation(50, 360);
                this.add(this.retryCanvas);
            }

            this.splashImage = null;
            this.repaint();
        }
    }

    public void setEndState(String var1) {
        this.endTextCustom = var1;
        this.setEndState(END_OTHER);
    }

    public void setEndState(Throwable var1) {
        var1.printStackTrace();
        this.aThrowable2553 = var1;
        this.setEndState(END_THROWABLE);
    }

    public void clearContent() {
        if (this.endState == 0) {
            this.contentPanel.dispose();
        }

    }

    public void addToContent(Component var1) {
        if (this.endState == 0) {
            this.contentPanel.add(var1);
        }

    }

    public void contentReady() {
        if (this.endState == 0) {
            this.contentPanel.makeVisible();
        }

    }

    public void printSUD(String var1) {
        System.out.println("SUD(" + System.currentTimeMillis() + "): " + var1);
    }

    public abstract void initApplet(Parameters var1);

    public void textsLoadedNotify(TextManager var1) {
    }

    public abstract void defineSounds(SoundManager var1);

    public abstract void defineImages(ImageManager var1, String var2);

    public abstract void createImages();

    public void defineSecImages(ImageManager var1, String var2) {
    }

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

    public boolean callJavaScriptJSON(String var1) {
        return this.param.callJavaScriptJSON(var1);
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

    public void callJavaScriptJsonBuyConfirmNeeded(Product var1) {
        String var2 = "{\"buy\":{\"status\":\"cneeded\",\"product\":\"";
        var2 = var2 + var1.getProductName();
        var2 = var2 + "}}";
        this.callJavaScriptJSON(var2);
    }

    public void callJavaScriptJsonBuyConfirmAnswer(Product var1, boolean var2) {
        String var3 = "{\"buy\":{\"status\":\"canswer\",\"product\":\"";
        var3 = var3 + var1.getProductName();
        var3 = var3 + "\",\"answer\":\"";
        var3 = var3 + (var2 ? "yes" : "no");
        var3 = var3 + "\"}}";
        this.callJavaScriptJSON(var3);
    }

    public void callJavaScriptJsonBuyStarted(Product var1) {
        this.callJavaScriptJsonBuyStarted(var1.getProductName());
    }

    public void callJavaScriptJsonBuyStarted(String var1) {
        String var2 = "{\"buy\":{\"status\":\"started\",\"product\":\"";
        var2 = var2 + var1;
        var2 = var2 + "\"}}";
        this.callJavaScriptJSON(var2);
    }

    public void callJavaScriptJsonBuyFinished(Product var1, int var2) {
        this.callJavaScriptJsonBuyFinished(var1.getProductName(), var2);
    }

    public void callJavaScriptJsonBuyFinished(String var1, int var2) {
        String var3 = "{\"buy\":{\"status\":\"finished\",\"product\":\"";
        var3 = var3 + var1;
        var3 = var3 + "\",\"result\":\"";
        if (var2 == 1) {
            var3 = var3 + "ok";
        } else if (var2 == 0) {
            var3 = var3 + "nobalance";
        } else if (var2 == -1) {
            var3 = var3 + "error";
        }

        var3 = var3 + "\"}}";
        this.callJavaScriptJSON(var3);
    }

    public void setConnectionReference(Connection var1) {
        this.connection = var1;
    }

    public void writeThriftLog(String var1, String var2) {
        if (this.connection != null) {
            this.connection.writeThriftLog(0, var1, var2);
        }

    }

    public void writeThriftDebug(String var1, String var2) {
        if (this.connection != null) {
            this.connection.writeThriftLog(1, var1, var2);
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

    private void sendLoadTimes(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
        if (this.isDebug()) {
            System.out.println("AApplet.sendLoadTimes(" + var1 + "," + var2 + ")");
        }

        try {
            String var9 = this.param.getParameter("ld_page");
            if (var9 == null) {
                return;
            }

            if (!var9.toLowerCase().startsWith("javascript:")) {
                return;
            }

            String var10 = this.getSystemProperty("java.version");
            String var11 = this.getSystemProperty("java.vendor");
            if (var11.length() > 128) {
                var11 = var11.substring(0, 128);
            }

            String var12 = Tools.replaceFirst(var9, "%v", var10);
            var12 = Tools.replaceFirst(var12, "%w", var11);
            var12 = Tools.replaceFirst(var12, "%r", "" + var1);
            var12 = Tools.replaceFirst(var12, "%f", "" + var2);
            var12 = Tools.replaceFirst(var12, "%1", "" + var3);
            var12 = Tools.replaceFirst(var12, "%2", "" + var4);
            var12 = Tools.replaceFirst(var12, "%3", "" + var5);
            var12 = Tools.replaceFirst(var12, "%4", "" + var6);
            var12 = Tools.replaceFirst(var12, "%5", "" + var7);
            var12 = Tools.replaceFirst(var12, "%6", "" + var8);
            URL var13 = new URL(var12);
            if (this.isDebug()) {
                System.out.println("AApplet.sendLoadTimes(...): Displaying page \"" + var13.toString() + "\"");
            }

            this.getAppletContext().showDocument(var13);
        } catch (Exception var14) {
            ;
        }

    }

    private String getSystemProperty(String var1) {
        try {
            String var2 = System.getProperty(var1);
            if (var2 != null) {
                return var2;
            }
        } catch (Exception var3) {
            ;
        } catch (Error var4) {
            ;
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

    public URL getServerBase() {
        if (!Boolean.parseBoolean(this.param.getParameter("is_applet"))) {
            return this.getCodeBase();
        } else {
            try {
                String server = this.param.getServerIp();
                System.out.println("this.server="+server+"|...="+"http://" + server + "/AGolf/");
                return new URL("http://" + server + "/AGolf/");
            } catch (Exception ex) {
                System.err.println("getdocumentbase exc eption");
                return null;
            }
        }
    }
}

