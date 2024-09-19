package agolf;

import agolf.game.GameBackgroundCanvas;
import agolf.game.GamePanel;
import agolf.lobby.LobbyPanel;
import com.aapeli.applet.AApplet;
import com.aapeli.client.AutoPopups;
import com.aapeli.client.BadWordFilter;
import com.aapeli.client.ImageManager;
import com.aapeli.client.Parameters;
import com.aapeli.client.SoundManager;
import com.aapeli.client.TextManager;
import org.moparforia.client.Launcher;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

public class GameApplet extends AApplet {

    public SynchronizedBool syncIsValidSite;
    public static final Color colourGameBackground = new Color(153, 255, 153);
    public static final Color colourTextBlack = new Color(0, 0, 0);
    public static final Color colourTextDarkGreen = new Color(64, 128, 64);
    public static final Color colourTextRed = new Color(128, 0, 0);
    public static final Color colourButtonGreen = new Color(144, 224, 144);
    public static final Color colourButtonYellow = new Color(224, 224, 144);
    public static final Color colourButtonRed = new Color(224, 144, 144);
    public static final Color colourButtonBlue = new Color(144, 144, 224);
    public static final Font fontSerif26b = new Font("Serif", Font.BOLD, 26);
    public static final Font fontSerif20 = new Font("Serif", Font.PLAIN, 20);
    public static final Font fontDialog14b = new Font("Dialog", Font.BOLD, 14);
    public static final Font fontDialog12 = new Font("Dialog", Font.PLAIN, 12);
    public static final Font fontDialog11 = new Font("Dialog", Font.PLAIN, 11);
    private GameContainer gameContainer;
    private int activePanel;
    private SynchronizedBool syncUnknownBool;
    private SynchronizedInteger syncPlayerAccessLevel;
    private boolean disableGuestChat;
    private boolean aBoolean3773;
    private Image anImage3774;
    private boolean verbose = false;


    public void initApplet(Parameters parameters) {
        this.syncIsValidSite = new SynchronizedBool(this.isValidSite());
        this.setBackground(colourGameBackground);
        this.setForeground(colourTextBlack);
        this.gameContainer = new GameContainer(this, parameters);
        this.aBoolean3773 = false;
    }

    public void textsLoadedNotify(TextManager textManager) {
        this.gameContainer.textManager = textManager;
    }

    public void defineSounds(SoundManager soundManager) {
        this.gameContainer.soundManager = soundManager;
    }

    public void defineImages(ImageManager imageManager, String var2) {
        this.gameContainer.imageManager = imageManager;
        imageManager.defineImage("bg-lobbyselect.gif");
        imageManager.defineImage("bg-lobby-single.gif");
        imageManager.defineImage("bg-lobby-single-fade.jpg");
        imageManager.defineImage("bg-lobby-dual.gif");
        imageManager.defineImage("bg-lobby-multi.gif");
        imageManager.defineImage("bg-lobby-multi-fade.jpg");
        imageManager.defineImage("bg-lobby-password.gif");
        imageManager.defineImage("shapes.gif");
        imageManager.defineImage("elements.gif");
        imageManager.defineImage("special.gif");
        imageManager.defineImage("balls.gif");
        imageManager.defineSharedImage("ranking-icons.gif");// TODO
        imageManager.defineSharedImage("language-flags.png"); // TODO
        imageManager.defineSharedImage("credit-background.jpg"); // TODO
        imageManager.defineSharedImage("bigtext.gif"); // TODO
        imageManager.defineSharedImage("tf-background.gif"); // TODO

        for (int i = 0; i < GameBackgroundCanvas.trackAdvertSize; ++i) {
            if (this.gameContainer.adverts[i] != null) {
                imageManager.defineImage("ad" + i, this.gameContainer.adverts[i]);
            }
        }

    }

    public void createImages() {
        this.gameContainer.spriteManager = new SpriteManager(super.imageManager);
        this.gameContainer.spriteManager.loadSprites();
    }

    public void connectToServer() {
        this.gameContainer.connection = new GolfConnection(this.gameContainer);
        if (!this.gameContainer.connection.openSocketConnection()) {
            this.setEndState(END_ERROR_CONNECTION);
        }

    }

    public void appletReady() {
        this.gameContainer.autoPopup = new AutoPopups(this);
        //this.setGameSettings(false, 0, false, true); // disabled Bad Word Filter!
        this.setGameSettings(false, 0, true, true); // enabled Bad Word Filter!
        this.gameContainer.trackCollection = new TrackCollection();
        this.anImage3774 = this.createImage(735, 375);
        this.gameContainer.connection.sendVersion();
    }

    public void destroyApplet() {
        this.gameContainer.destroy();
    }

    public boolean isDebug() {
        return verbose;
    }

    protected int getActivePanel() {
        return this.activePanel;
    }

    public void setGameState(int state) {
        this.setGameState(state, 0, 0);
    }

    protected void setGameState(int state, int lobbyId) {
        this.setGameState(state, lobbyId, 0);
    }

    /**
     * @param activePanel 0 == ?, 1 == login, 2 == lobby selection panel, 3 == in lobby, 4 == in game
     * @param lobbyId game type, single player == 1, dual player == 2, multiplayer == 3
     */
    protected void setGameState(int activePanel, int lobbyId, int lobbyExtra) {
        if (activePanel != this.activePanel && this.syncIsValidSite.get()) {
            this.activePanel = activePanel;
            if (this.gameContainer.lobbySelectionPanel != null) {
                this.gameContainer.lobbySelectionPanel.destroyNumberOfPlayersFetcher();
            }

            this.clearContent();
            if (activePanel == 1) {
                if (this.aBoolean3773) {
                    super.param.removeSession();
                } else {
                    this.aBoolean3773 = true;
                }
                //System.out.println(hasSession() + " " + gameContainer.synchronizedTrackTestMode.get());

                if (Launcher.isUsingCustomServer()) {
                    String username = param.getUsername();
                    if (username == null) {
                        TrackTestLoginPanel loginPanel = new TrackTestLoginPanel(this, super.appletWidth, super.appletHeight);
                        loginPanel.setLocation(0, 0);
                        this.addToContent(loginPanel);
                    } else {
                        this.trackTestLogin(username, "");
                    }
                } else if (this.hasSession()) {
                    super.param.noGuestAutoLogin();
                    this.gameContainer.connection.writeData("login\t" + super.param.getSession());
                    this.activePanel = 0;
                } else if (!this.gameContainer.synchronizedTrackTestMode.get()) {
                    this.gameContainer.connection.writeData("login");
                    this.activePanel = 0;
                } else {

                }
            }

            if (activePanel == 2) {
                if (this.gameContainer.lobbySelectionPanel == null) {
                    this.gameContainer.lobbySelectionPanel = new LobbySelectPanel(this.gameContainer, super.appletWidth, super.appletHeight);
                    this.gameContainer.lobbySelectionPanel.setLocation(0, 0);
                }

                boolean var5 = false;
                if (this.gameContainer.defaultLobby != null) {
                    if (this.gameContainer.defaultLobby.equalsIgnoreCase("singlehidden")) {
                        var5 = this.gameContainer.lobbySelectionPanel.selectLobby(1, true);
                    } else if (this.gameContainer.defaultLobby.equalsIgnoreCase("single")) {
                        var5 = this.gameContainer.lobbySelectionPanel.selectLobby(1, false);
                    } else if (this.gameContainer.defaultLobby.equalsIgnoreCase("dual")) {
                        var5 = this.gameContainer.lobbySelectionPanel.selectLobby(2, false);
                    } else if (this.gameContainer.defaultLobby.equalsIgnoreCase("multi")) {
                        var5 = this.gameContainer.lobbySelectionPanel.selectLobby(3, false);
                    }

                    this.gameContainer.defaultLobby = null;
                }

                if (!var5) {
                    this.addToContent(this.gameContainer.lobbySelectionPanel);
                    this.gameContainer.lobbySelectionPanel.resetNumberOfPlayersFetcher();
                }
            }

            if (activePanel == 3) {
                this.gameContainer.gamePanel = null;
                if (this.gameContainer.lobbyPanel == null) {
                    this.gameContainer.lobbyPanel = new LobbyPanel(this.gameContainer, super.appletWidth, super.appletHeight);
                    this.gameContainer.lobbyPanel.setLocation(0, 0);
                }

                if (lobbyId == -1 || lobbyId >= 1) {
                    this.gameContainer.lobbyPanel.selectLobby(lobbyId, lobbyExtra);
                    if (lobbyId == 3 && lobbyExtra >= 0) {
                        this.gameContainer.lobbyPanel.setJoinError(lobbyExtra);
                    }
                }

                this.gameContainer.lobbyPanel.method395();
                this.addToContent(this.gameContainer.lobbyPanel);
            }

            if (activePanel == 4) {
                this.gameContainer.gamePanel = new GamePanel(this.gameContainer, super.appletWidth, super.appletHeight, this.anImage3774);
                this.gameContainer.gamePanel.setLocation(0, 0);
                this.addToContent(this.gameContainer.gamePanel);
            }

            if (activePanel == 5) {
                //super.param.showQuitPage();
                System.exit(0);
            } else {
                this.contentReady();
            }
        }
    }

    protected void setGameSettings(boolean emailUnconfirmed, int var2, boolean useBadWordFilter, boolean var4) {
        this.syncUnknownBool = new SynchronizedBool(emailUnconfirmed);
        this.syncPlayerAccessLevel = new SynchronizedInteger(var2);
        this.gameContainer.badWordFilter = useBadWordFilter ? new BadWordFilter(super.textManager) : null;
        this.disableGuestChat = var4;
    }

    protected void trackTestLogin(String username, String password) {
        this.setGameState(0);
        this.gameContainer.connection.writeData("ttlogin\t" + username + "\t" + password);
    }

    public boolean isEmailVerified() {
        return this.syncUnknownBool.get();
    }

    public int getPlayerAccessLevel() {
        return this.syncPlayerAccessLevel.get();
    }

    public boolean isGuestChatDisabled() {
        return this.disableGuestChat;
    }

    protected boolean hasSession() {
        return super.param.getSession() != null;
    }

    public boolean showPlayerCard(String name) {
        return super.param.showPlayerCard(name);
    }

    public void showPlayerList(String[] names) {
        super.param.showPlayerList(names);
    }

    public void showPlayerListWinners(boolean[] var1) {
        super.param.showPlayerListWinners(var1);
    }

    public void removePlayerListWinnders() {
        super.param.removePlayerListWinners();
    }

    public void removePlayerList() {
        super.param.removePlayerList();
    }

    public void gameFinished(boolean var1) {
        this.gameContainer.autoPopup.gameFinished(var1);
    }

    public void quit(String from) {
        this.setEndState(END_QUIT);
        this.gameContainer.connection.writeData((from != null ? from + "\t" : "") + "quit");
        this.setGameState(5);
    }

    private boolean isValidSite() {
        return true;
    }

    private boolean containsDomain(String host, String domain, String[] tld) {
        for (String text : tld) {
            if (host.equals(domain + "." + text)) {
                return true;
            }

            if (host.endsWith("." + domain + "." + text)) {
                return true;
            }
        }

        return false;
    }
}
