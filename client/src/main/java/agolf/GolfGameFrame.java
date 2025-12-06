package agolf;

import agolf.game.GamePanel;
import agolf.lobby.LobbyPanel;
import com.aapeli.client.BadWordFilter;
import com.aapeli.client.ImageManager;
import com.aapeli.client.Parameters;
import com.aapeli.client.SoundManager;
import com.aapeli.client.TextManager;
import com.aapeli.frame.AbstractGameFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import org.moparforia.client.Launcher;
import org.moparforia.shared.Language;

public class GolfGameFrame extends AbstractGameFrame {

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

    public GolfGameFrame(
            String server, int port, Language language, String username, boolean verbose, boolean norandom) {
        super(server, port, language, username, verbose, norandom);
    }

    public void initGame(Parameters parameters) {
        this.syncIsValidSite = new SynchronizedBool(this.isValidSite());
        this.setBackground(colourGameBackground);
        this.setForeground(colourTextBlack);
        this.gameContainer = new GameContainer(this, parameters);
        this.aBoolean3773 = false;
    }

    @Override
    public String getCopyrightInfo() {
        return "-= AGolf =-\nCopyright (c) 2002-2012 Playforia (www.playforia.info)\nProgramming: Pasi Laaksonen\nGraphics: Janne Matilainen";
    }

    public void textsLoadedNotify(TextManager textManager) {
        this.gameContainer.textManager = textManager;
    }

    public void defineSounds(SoundManager soundManager) {
        this.gameContainer.soundManager = soundManager;
    }

    public void defineImages(ImageManager imageManager) {
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
        imageManager.defineImage("ranking-icons.gif");
        imageManager.defineImage("language-flags.png");
        imageManager.defineImage("credit-background.jpg");
        imageManager.defineImage("tf-background.gif");
        imageManager.defineImage("game-cursor.png");
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

    public void gameReady() {
        // this.setGameSettings(false, 0, false, true); // disabled Bad Word Filter!
        this.setGameSettings(false, 0, true, true); // enabled Bad Word Filter!
        this.gameContainer.trackCollection = new TrackCollection();
        this.anImage3774 = this.createImage(735, 375);
        this.gameContainer.connection.sendVersion();
    }

    public void destroyGame() {
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
     * @param activePanel 0 == ?, 1 == login, 2 == lobby selection panel, 3 == in lobby, 4 == in
     *     game
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
                // System.out.println(hasSession() + " " +
                // gameContainer.synchronizedTrackTestMode.get());

                if (Launcher.isUsingCustomServer()) {
                    String username = param.getUsername();
                    if (username == null) {
                        TrackTestLoginPanel loginPanel =
                                new TrackTestLoginPanel(this, super.contentWidth, super.contentHeight);
                        loginPanel.setLocation(0, 0);
                        this.addToContent(loginPanel);
                    } else {
                        this.trackTestLogin(username, "");
                    }
                } else if (this.hasSession()) {
                    this.gameContainer.connection.writeData("login\t" + super.param.getSession());
                    this.activePanel = 0;
                } else if (!this.gameContainer.synchronizedTrackTestMode.get()) {
                    this.gameContainer.connection.writeData("login");
                    this.activePanel = 0;
                }
            }

            if (activePanel == 2) {
                if (this.gameContainer.lobbySelectionPanel == null) {
                    this.gameContainer.lobbySelectionPanel =
                            new LobbySelectPanel(this.gameContainer, super.contentWidth, super.contentHeight);
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
                    this.gameContainer.lobbyPanel =
                            new LobbyPanel(this.gameContainer, super.contentWidth, super.contentHeight);
                    this.gameContainer.lobbyPanel.setLocation(0, 0);
                }

                if (lobbyId == -1 || lobbyId >= 1) {
                    this.gameContainer.lobbyPanel.selectLobby(lobbyId, lobbyExtra);
                    if (lobbyId == 3 && lobbyExtra >= 0) {
                        this.gameContainer.lobbyPanel.setJoinError(lobbyExtra);
                    }
                }

                this.gameContainer.lobbyPanel.init();
                this.addToContent(this.gameContainer.lobbyPanel);
            }

            if (activePanel == 4) {
                this.gameContainer.gamePanel =
                        new GamePanel(this.gameContainer, super.contentWidth, super.contentHeight, this.anImage3774);
                this.gameContainer.gamePanel.setLocation(0, 0);
                this.addToContent(this.gameContainer.gamePanel);
            }

            if (activePanel == 5) {
                // super.param.showQuitPage();
                System.exit(0);
            } else {
                this.contentReady();
            }
        }
    }

    protected void setGameSettings(
            boolean emailUnconfirmed, int playerElevationLevel, boolean useBadWordFilter, boolean disableGuestChat) {
        this.syncUnknownBool = new SynchronizedBool(emailUnconfirmed);
        this.syncPlayerAccessLevel = new SynchronizedInteger(playerElevationLevel);
        this.gameContainer.badWordFilter = useBadWordFilter ? new BadWordFilter(super.textManager) : null;
        this.disableGuestChat = disableGuestChat;
    }

    protected void trackTestLogin(String username, String password) {
        this.setGameState(0);
        this.gameContainer.connection.writeData("ttlogin\t" + username + "\t" + password);
    }

    protected void trackTestLogin(String username, String password, Language language) {
        this.textManager.setLanguage(language);
        this.gameContainer.connection.writeData("language\t" + language);
        this.trackTestLogin(username, password);
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

    public void quit(String from) {
        this.setEndState(END_QUIT);
        this.gameContainer.connection.writeData((from != null ? from + "\t" : "") + "quit");
        this.setGameState(5);
    }

    private boolean isValidSite() {
        return true;
    }
}
