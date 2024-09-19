package agolf;

import agolf.game.GameBackgroundCanvas;
import agolf.game.GamePanel;
import agolf.lobby.LobbyPanel;
import com.aapeli.client.AutoPopups;
import com.aapeli.client.BadWordFilter;
import com.aapeli.client.ImageManager;
import com.aapeli.client.Parameters;
import com.aapeli.client.SoundManager;
import com.aapeli.client.TextManager;
import com.aapeli.tools.Tools;

public class GameContainer {// some kind of a container for everything

    public GameApplet gameApplet;
    public Parameters params;
    public TextManager textManager;
    public SoundManager soundManager;
    public ImageManager imageManager;
    public SpriteManager spriteManager;
    public BadWordFilter badWordFilter;
    public AutoPopups autoPopup;
    public TrackCollection trackCollection;
    public GolfConnection connection;
    public LobbySelectPanel lobbySelectionPanel;
    public LobbyPanel lobbyPanel;
    public String defaultLobby;
    public boolean disableSinglePlayer;
    public SynchronizedBool synchronizedTrackTestMode;
    public boolean safeMode;
    protected String[] adverts;
    public GamePanel gamePanel;
    public int graphicsQualityIndex;


    public GameContainer(GameApplet gameApplet, Parameters params) {
        this.gameApplet = gameApplet;
        this.params = params;
        this.graphicsQualityIndex = 3;
        this.synchronizedTrackTestMode = new SynchronizedBool();
        this.init();
    }

    protected void destroy() {
        if (this.autoPopup != null) {
            this.autoPopup.close();
        }

        if (this.connection != null) {
            this.connection.disconnect();
        }

    }

    private void init() {
        this.disableSinglePlayer = this.params.getParameter("disablespgames") != null;
        if (!this.disableSinglePlayer) {
            this.disableSinglePlayer = this.params.getParameter("disablespgame") != null;
        }

        this.adverts = new String[GameBackgroundCanvas.trackAdvertSize];

        for (int var1 = 0; var1 < GameBackgroundCanvas.trackAdvertSize; ++var1) {
            this.adverts[var1] = this.params.getParameter("ad-" + GameBackgroundCanvas.aStringArray80[var1]);
        }

        this.defaultLobby = this.params.getParameter("lobby");
        this.synchronizedTrackTestMode.set(Tools.getBoolean(this.params.getParameter("tracktestmode")));
    }
}
