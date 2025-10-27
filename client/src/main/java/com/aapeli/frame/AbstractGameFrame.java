package com.aapeli.frame;

import com.aapeli.client.ImageManager;
import com.aapeli.client.Parameters;
import com.aapeli.client.SoundManager;
import com.aapeli.client.StringDraw;
import com.aapeli.client.TextManager;
import com.aapeli.connection.SocketConnection;
import com.aapeli.tools.Tools;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import org.moparforia.shared.Language;

public abstract class AbstractGameFrame extends JFrame implements Runnable, ActionListener {

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
    public int contentWidth;
    public int contentHeight;
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
    private SocketConnection socketConnection;
    private Image image;
    private Graphics graphics;
    private boolean verbose;

    public AbstractGameFrame(
            String server, int port, Language language, String username, boolean verbose, boolean norandom) {
        super();
        this.verbose = verbose;
        this.param = this.getParameters(server, language, username, port, verbose, norandom);
        this.setSize(WIDTH, HEIGHT);
        this.init();
        this.start();
        this.setSize(1280, 720);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setTitle("Minigolf");
        try {
            this.setIconImage(ImageIO.read(this.getClass().getResource("/icons/playforia.png")));
        } catch (Exception e) {
        }
    }

    public void init() {
        System.out.println("\n" + this.getCopyrightInfo() + "\n");
        this.contentWidth = 735;
        this.contentHeight = 525;
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

    public void destroy() {
        this.destroyed = true;
        this.removeLoadingPanel();
        this.setEndState(END_QUIT);

        try {
            this.destroyGame();
        } catch (Exception e) {
        }

        if (this.contentPanel != null) {
            this.contentPanel.destroy();
            this.contentPanel = null;
        }

        if (this.soundManager != null) {
            this.soundManager.destroy();
        }

        if (this.textManager != null) {
            this.textManager.destroy();
        }

        this.soundManager = null;
        this.imageManager = null;
        this.textManager = null;
        if (this.graphics != null) {
            this.graphics.dispose();
            this.graphics = null;
        }

        if (this.image != null) {
            this.image.flush();
            this.image = null;
        }

        this.backgroundImageKey = null;
    }

    public abstract String getCopyrightInfo();

    public void paint(Graphics graphics) {
        this.update(graphics);
    }

    public void update(Graphics graphics) {
        if (!this.destroyed) {
            if (this.image == null) {
                this.image = this.createImage(this.contentWidth, this.contentHeight);
                this.graphics = this.image.getGraphics();
            }

            Color backgroundColor = this.getBackground();
            this.graphics.setColor(backgroundColor);
            this.graphics.fillRect(0, 0, this.contentWidth, this.contentHeight);
            if (this.imageManager != null && this.backgroundImageKey != null) {
                Image image = this.imageManager.getImage(this.backgroundImageKey);
                if (image != null) {
                    this.graphics.drawImage(image, this.backgroundXOffset, this.backgroundYOffset, this);
                }
            }

            if (this.textManager != null) {
                this.graphics.setColor(this.getForeground());
                Color outlineColor = this.drawTextOutline ? backgroundColor : null;
                if (this.endState == END_ERROR_CONNECTION) {
                    byte textYOffset = -20;
                    this.graphics.setFont(fontDialog15);
                    StringDraw.drawOutlinedString(
                            this.graphics,
                            outlineColor,
                            this.textManager.getText("Message_CE_ConnectionError"),
                            40,
                            80 + textYOffset,
                            -1);
                    this.graphics.setFont(fontDialog12);
                    StringDraw.drawOutlinedString(
                            this.graphics,
                            outlineColor,
                            this.textManager.getText("Message_CE_PossibleReasons"),
                            40,
                            125 + textYOffset,
                            -1);
                    if (!this.ready) {
                        this.graphics.setFont(fontDialog12);
                        StringDraw.drawOutlinedString(
                                this.graphics,
                                outlineColor,
                                "- " + this.textManager.getText("Message_CE0_1_Short"),
                                40,
                                160 + textYOffset,
                                -1);
                        this.graphics.setFont(fontDialog11);
                        StringDraw.drawOutlinedStringWithMaxWidth(
                                this.graphics,
                                outlineColor,
                                this.textManager.getText(
                                        "Message_CE0_1_Long",
                                        this.param.getServerIp(),
                                        "" + this.param.getServerPort()),
                                50,
                                180 + textYOffset,
                                -1,
                                this.contentWidth - 50 - 50);
                        this.graphics.setFont(fontDialog12);
                        StringDraw.drawOutlinedString(
                                this.graphics,
                                outlineColor,
                                "- " + this.textManager.getText("Message_CE0_2_Short"),
                                40,
                                245 + textYOffset,
                                -1);
                        this.graphics.setFont(fontDialog11);
                        StringDraw.drawOutlinedStringWithMaxWidth(
                                this.graphics,
                                outlineColor,
                                this.textManager.getText("Message_CE0_2_Long"),
                                50,
                                265 + textYOffset,
                                -1,
                                this.contentWidth - 50 - 50);
                        this.graphics.setFont(fontDialog12);
                        StringDraw.drawOutlinedString(
                                this.graphics,
                                outlineColor,
                                "- " + this.textManager.getText("Message_CE0_3_Short"),
                                40,
                                305 + textYOffset,
                                -1);
                        this.graphics.setFont(fontDialog11);
                        StringDraw.drawOutlinedStringWithMaxWidth(
                                this.graphics,
                                outlineColor,
                                this.textManager.getText("Message_CE0_3_Long"),
                                50,
                                325 + textYOffset,
                                -1,
                                this.contentWidth - 50 - 50);
                    } else {
                        this.graphics.setFont(fontDialog12);
                        StringDraw.drawOutlinedString(
                                this.graphics,
                                outlineColor,
                                "- " + this.textManager.getText("Message_CE1_1_Short"),
                                40,
                                160 + textYOffset,
                                -1);
                        this.graphics.setFont(fontDialog11);
                        StringDraw.drawOutlinedStringWithMaxWidth(
                                this.graphics,
                                outlineColor,
                                this.textManager.getText("Message_CE1_1_Long"),
                                50,
                                180 + textYOffset,
                                -1,
                                this.contentWidth - 50 - 50);
                        this.graphics.setFont(fontDialog12);
                        StringDraw.drawOutlinedString(
                                this.graphics,
                                outlineColor,
                                "- " + this.textManager.getText("Message_CE1_2_Short"),
                                40,
                                235 + textYOffset,
                                -1);
                        this.graphics.setFont(fontDialog11);
                        StringDraw.drawOutlinedStringWithMaxWidth(
                                this.graphics,
                                outlineColor,
                                this.textManager.getText("Message_CE1_2_Long"),
                                50,
                                255 + textYOffset,
                                -1,
                                this.contentWidth - 50 - 50);
                        this.graphics.setFont(fontDialog12);
                        StringDraw.drawOutlinedString(
                                this.graphics,
                                outlineColor,
                                "- " + this.textManager.getText("Message_CE1_3_Short"),
                                40,
                                305 + textYOffset,
                                -1);
                        this.graphics.setFont(fontDialog11);
                        StringDraw.drawOutlinedStringWithMaxWidth(
                                this.graphics,
                                outlineColor,
                                this.textManager.getText("Message_CE1_3_Long"),
                                50,
                                325 + textYOffset,
                                -1,
                                this.contentWidth - 50 - 50);
                    }
                } else if (this.endState == END_THROWABLE) {
                    this.graphics.setFont(fontDialog15);
                    StringDraw.drawOutlinedString(
                            this.graphics,
                            outlineColor,
                            this.textManager.getText("Message_PE_ProgramError"),
                            50,
                            100,
                            -1);
                    this.graphics.setFont(fontDialog12);
                    StringDraw.drawOutlinedStringWithMaxWidth(
                            this.graphics,
                            outlineColor,
                            this.textManager.getText("Message_PE_GameClosed"),
                            50,
                            150,
                            -1,
                            this.contentWidth - 70 - 50);
                    this.graphics.setFont(fontDialog12b);
                    StringDraw.drawOutlinedString(
                            this.graphics,
                            outlineColor,
                            this.textManager.getText("Message_PE_ErrorDesc", this.aThrowable2553.toString()),
                            50,
                            235,
                            -1);
                } else {
                    String endText = this.textManager.getText("Message_WaitWhile");
                    String endTextHelp = null;
                    if (this.endState == END_ERROR_MATCH) {
                        endText = this.textManager.getText("Match_MessageError");
                        endTextHelp = this.textManager.getText("Match_MessageErrorHelp");
                    } else if (this.endState == END_ERROR_VERSION) {
                        endText = this.textManager.getText("Message_VersionError");
                        endTextHelp = this.textManager.getText("Message_VersionErrorHelp");
                    } else if (this.endState == END_ERROR_SERVERFULL) {
                        endText = this.textManager.getText("Message_ServerFullError");
                        endTextHelp = this.textManager.getText("Message_ServerFullErrorHelp");
                    } else if (this.endState == END_QUIT) {
                        endText = this.textManager.getText("Message_QuitGame");
                    } else if (this.endState == END_QUIT_REGISTER) {
                        endText = this.textManager.getText("Message_QuitGame_ToRegister");
                    } else if (this.endState == END_OTHER) {
                        endText = this.endTextCustom;
                    } else if (this.endState == END_ERROR_KICK_NOW) {
                        endText = this.textManager.getText("Message_KickedNow");
                        endTextHelp = this.textManager.getText("Message_KickedNowHelp");
                    } else if (this.endState == END_ERROR_KICKBAN_NOW) {
                        endText = this.textManager.getText("Message_BannedNow");
                        endTextHelp = this.textManager.getText("Message_BannedNowHelp");
                    } else if (this.endState == END_ERROR_BAN_INIT) {
                        endText = this.textManager.getText("Message_BannedInitially");
                        endTextHelp = this.textManager.getText("Message_BannedInitiallyHelp");
                    } else if (this.endState == END_ERROR_REGLOGIN_FAILED) {
                        endText = this.textManager.getText("Message_LoginFailedReg");
                        endTextHelp = this.textManager.getText("Message_LoginFailedRegHelp");
                    } else if (this.endState == END_ERROR_TOOMANYIP_INIT) {
                        endText = this.textManager.getText("Message_TooManySameIP");
                        endTextHelp = this.textManager.getText("Message_TooManySameIPHelp");
                    }

                    this.graphics.setFont(fontDialog15);
                    if (this.endTextLocation == TEXT_CENTER) {
                        StringDraw.drawOutlinedString(
                                this.graphics,
                                outlineColor,
                                endText,
                                this.contentWidth / 2,
                                this.contentHeight / 2 - 10,
                                0);
                    } else if (this.endTextLocation == TEXT_LOWERLEFT) {
                        StringDraw.drawOutlinedString(
                                this.graphics,
                                outlineColor,
                                endText,
                                this.contentWidth / 12,
                                this.contentHeight - 120,
                                -1);
                    } else if (this.endTextLocation == TEXT_LOWERMIDDLE) {
                        StringDraw.drawOutlinedString(
                                this.graphics,
                                outlineColor,
                                endText,
                                this.contentWidth / 2,
                                this.contentHeight - 120,
                                0);
                    }

                    if (endTextHelp != null) {
                        this.graphics.setFont(fontDialog12);
                        if (this.endTextLocation == TEXT_CENTER) {
                            StringDraw.drawOutlinedStringWithMaxWidth(
                                    this.graphics,
                                    outlineColor,
                                    endTextHelp,
                                    this.contentWidth / 2,
                                    this.contentHeight / 2 + 30,
                                    0,
                                    (int) ((double) this.contentWidth * 0.8D));
                        } else if (this.endTextLocation == TEXT_LOWERLEFT) {
                            StringDraw.drawOutlinedStringWithMaxWidth(
                                    this.graphics,
                                    outlineColor,
                                    endTextHelp,
                                    this.contentWidth / 12,
                                    this.contentHeight - 80,
                                    -1,
                                    (int) ((double) this.contentWidth * 0.6D));
                        } else if (this.endTextLocation == TEXT_LOWERMIDDLE) {
                            StringDraw.drawOutlinedStringWithMaxWidth(
                                    this.graphics,
                                    outlineColor,
                                    endTextHelp,
                                    this.contentWidth / 2,
                                    this.contentHeight - 80,
                                    0,
                                    (int) ((double) this.contentWidth * 0.5D));
                        }
                    }
                }
            }
        }

        int x = (this.getWidth() - image.getWidth(null)) / 2;
        int y = (this.getHeight() - image.getHeight(null)) / 2;
        graphics.drawImage(this.image, x, y, this);
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        this.setLayout(new GridBagLayout());
        this.loadingPanel = new LoadingPanel(this);
        this.add(this.loadingPanel);
        this.revalidate();
        this.loadingPanel.start();
        String initMessage = this.param.getParameter("initmessage");
        if (initMessage != null && initMessage.indexOf('|') == -1) {
            this.loadingPanel.setLoadingMessage(initMessage);
        }

        this.initGame(this.param);
        this.loadingPanel.setBackground(this.getBackground());
        if (this.endState == 0 && !this.destroyed) {
            boolean startupDebug = false;
            String startupDebugParameter = this.param.getParameter("startupdebug");
            if (startupDebugParameter != null && Tools.getBoolean(startupDebugParameter)) {
                startupDebug = true;
                this.printSUD("StartUp Debug enabled!");
            }

            if (startupDebug) {
                this.printSUD("Creating text manager");
            }

            this.loadingPanel.setActualProgress(0.5D);
            this.textManager = new TextManager(this.param, this.isDebug());
            if (startupDebug) {
                this.printSUD("Loading texts...");
            }

            this.textsLoadedNotify(this.textManager);
            if (startupDebug) {
                this.printSUD("...done");
            }

            if (System.currentTimeMillis() < startTime + 3000L) {
                this.loadingPanel.method468(2.0D);
            }

            if (startupDebug) {
                this.printSUD("Creating sound manager");
            }

            this.loadingPanel.setLoadingMessage(this.textManager.getText("Loader_LoadingGfxSfx"));
            this.soundManager = new SoundManager(this.isDebug());

            this.loadingPanel.addProgress(0.15D);
            if (startupDebug) {
                this.printSUD("Defining sounds...");
            }

            this.defineSounds(this.soundManager);
            if (startupDebug) {
                this.printSUD("...done");
            }

            if (startupDebug) {
                this.printSUD("Creating image manager");
            }

            this.imageManager = new ImageManager(this.isDebug());

            this.loadingPanel.addProgress(0.05D);
            this.defineImages(this.imageManager);
            if (startupDebug) {
                this.printSUD("Loading images...");
            }

            this.loadingPanel.setActualProgress(0.7D + this.imageManager.getImageLoadProgress() * 0.15D);

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

            if (System.currentTimeMillis() < startTime + 7000L) {
                this.loadingPanel.method468(2.0D);
            }

            if (startupDebug) {
                this.printSUD("Connecting to server...");
            }

            this.loadingPanel.setLoadingMessage(this.textManager.getText("Message_Connecting"));
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

                if (this.isDebug()) {
                    System.out.println("AbstractGameFrame.sendLoadTimes(" + readyTime + "," + finishedTime + ")");
                }
                this.writeMetadataLog1("clientconnect", "loadtime:i:" + readyTime + "^loadertime:i:" + finishedTime);
                if (this.endState == 0 && !this.destroyed) {
                    this.remove(this.loadingPanel);
                    this.loadingPanel.destroy();
                    this.loadingPanel = null;
                    if (startupDebug) {
                        this.printSUD("Adding game content...");
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

                    this.gameReady();
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

            int x = (this.getWidth() - this.contentWidth) / 2;
            int y = (this.getHeight() - this.contentHeight) / 2;
            if (state == END_ERROR_CONNECTION) {
                this.retryCanvas = new RetryCanvas(this.textManager.getText("Message_CE_RetryButton"), 120, 20, this);
                this.retryCanvas.setLocation(x + 40, y + 360);
                this.add(this.retryCanvas);
            } else if (state == END_THROWABLE) {
                this.retryCanvas = new RetryCanvas(this.textManager.getText("Message_PE_RetryButton"), 120, 20, this);
                this.retryCanvas.setLocation(x + 50, y + 360);
                this.add(this.retryCanvas);
            }

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

    public abstract void initGame(Parameters parameters);

    public void textsLoadedNotify(TextManager var1) {}

    public abstract void defineSounds(SoundManager soundManager);

    public abstract void defineImages(ImageManager imageManager);

    public abstract void createImages();

    public abstract void connectToServer();

    public abstract void gameReady();

    public abstract void destroyGame();

    public abstract boolean isDebug();

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

    private Parameters getParameters(
            String server, Language language, String username, int port, boolean verbose, boolean norandom) {
        Map<String, String> params = new HashMap<>();
        if (server.indexOf(':') == -1) { // is ipv4
            params.put("server", server);
        } else { // is ipv6
            params.put("server", "[" + server + "]");
        }
        params.put("initmessage", "Loading game...");
        params.put("server", server + ":" + port);
        params.put("language", language.toString());
        params.put("registerpage", "http://www.playforia.com/account/create/");
        params.put("userinfopage", "http://www.playforia.com/community/user/");
        params.put("userinfotarget", "_blank");
        params.put("userlistpage", "javascript:Playray.GameFaceGallery('%n','#99FF99','agolf','%s')");
        params.put("verbose", Boolean.toString(verbose));
        params.put("norandom", Boolean.toString(norandom));
        params.put("username", username);
        return new Parameters(params);
    }

    private void removeLoadingPanel() {
        LoadingPanel var1 = this.loadingPanel;
        if (var1 != null) {
            this.remove(var1);
            var1.destroy();
            this.loadingPanel = null;
        }
    }
}
