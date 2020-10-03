package agolf.lobby;

import agolf.GameApplet;
import agolf.GameContainer;
import com.aapeli.colorgui.Choicer;
import com.aapeli.multiuser.UserListItem;
import org.moparforia.client.Launcher;

import java.awt.Graphics;
import java.awt.Panel;

public class LobbyPanel extends Panel {

    protected static final int[] gameTimeLimits = new int[]{10, 20, 30, 45, 60, 90, 120};
    private static final int numGameTimeLimits = gameTimeLimits.length;
    private GameContainer gameContainer;
    private int width;
    private int height;
    private int activeLobby;
    private LobbySinglePlayerPanel lobbySinglePlayerPanel;
    //private LobbyDualPlayerPanel lobbyDualPlayerPanel;
    private LobbyMultiPlayerPanel lobbyMultiPlayerPanel;
    private LobbyChatPanel lobbyChatPanelSingle;
    //private LobbyChatPanel lobbyChatPanelDual;
    private LobbyChatPanel lobbyChatPanelMulti;
    private LobbyControlPanel lobbyControlPanel;
    private LobbyTrackListAdminPanel lobbyTrackListAdminPanel;
    public static boolean aBoolean465;


    public LobbyPanel(GameContainer gameContainer, int width, int height) {
        this.gameContainer = gameContainer;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
    }

    public void addNotify() {
        super.addNotify();
        this.repaint();
    }

    public void paint(Graphics var1) {
        this.update(var1);
    }

    public void update(Graphics var1) {
        var1.setColor(GameApplet.colourGameBackground);
        var1.fillRect(0, 0, this.width, this.height);
    }

    public void selectLobby(int lobbyId, int lobbyExtra) {
        this.activeLobby = lobbyId;

        this.setVisible(false);
        this.removeAll();
        if (lobbyId == 1) {
            if (this.lobbySinglePlayerPanel == null) {
                this.lobbySinglePlayerPanel = new LobbySinglePlayerPanel(this.gameContainer, this.width, this.height - 130);
                this.lobbySinglePlayerPanel.setLocation(0, 0);
            }

            this.add(this.lobbySinglePlayerPanel);
            if (lobbyExtra == 1) {
                if (this.lobbyChatPanelSingle == null) {
                    this.lobbyChatPanelSingle = new LobbyChatPanel(this.gameContainer, this.width - 100, 126, 1);
                    this.lobbyChatPanelSingle.setLocation(0, this.height - 126);
                }
                this.add(this.lobbyChatPanelSingle);
            }
        }

        /*if (lobbyId == 2) {
            if (this.lobbyDualPlayerPanel == null) {
                this.lobbyDualPlayerPanel = new LobbyDualPlayerPanel(this.gameContainer, this.width, this.height - 230);
                this.lobbyDualPlayerPanel.setLocation(0, 0);
            }

            this.add(this.lobbyDualPlayerPanel);
            if (this.lobbyChatPanelDual == null) {
                this.lobbyChatPanelDual = new LobbyChatPanel(this.gameContainer, this.width - 100, 225, 2);
                this.lobbyChatPanelDual.setLocation(0, this.height - 225);
            }

            this.add(this.lobbyChatPanelDual);
        }*/

        if (lobbyId == 3) {
            if (this.lobbyMultiPlayerPanel == null) {
                this.lobbyMultiPlayerPanel = new LobbyMultiPlayerPanel(this.gameContainer, this.width, this.height - 130);
                this.lobbyMultiPlayerPanel.setLocation(0, 0);
            }

            this.add(this.lobbyMultiPlayerPanel);
            if (this.lobbyChatPanelMulti == null) {
                this.lobbyChatPanelMulti = new LobbyChatPanel(this.gameContainer, this.width - 100, 126, 3);
                this.lobbyChatPanelMulti.setLocation(0, this.height - 126);
            }

            this.add(this.lobbyChatPanelMulti);
        }

        if (lobbyId == -1) {
            if (this.lobbyTrackListAdminPanel == null) {
                this.lobbyTrackListAdminPanel = new LobbyTrackListAdminPanel(this.gameContainer, this.width, this.height, lobbyExtra == 1);
                this.lobbyTrackListAdminPanel.setLocation(0, 0);
            }

            this.add(this.lobbyTrackListAdminPanel);
        }

        if (lobbyId > 0) {
            if (this.lobbyControlPanel == null) {
                this.lobbyControlPanel = new LobbyControlPanel(this.gameContainer, 90, 125);
                this.lobbyControlPanel.setLocation(this.width - 90, this.height - 125);
            }

            this.lobbyControlPanel.setState(lobbyId);
            this.add(this.lobbyControlPanel);
        }

        this.setVisible(true);
    }

    public void method395() {
        if (this.activeLobby == 1) {
            this.lobbySinglePlayerPanel.requestTrackSetList();
        }

        /*if (this.activeLobby == 2) {
            this.lobbyDualPlayerPanel.update(0);
            this.lobbyDualPlayerPanel.allowChallenges();
        }*/

        if (this.activeLobby == -1) {
            this.lobbyTrackListAdminPanel.setRefreshTrackList();
        }

    }

    public void setJoinError(int var1) {
        if (this.lobbyMultiPlayerPanel != null) {
            this.lobbyMultiPlayerPanel.setJoinError(var1);
        }

    }

    public void requestTrackSetList() {
        this.lobbySinglePlayerPanel.setRequestTrackSetList();
    }

    public void handlePacket(String[] args) {
        boolean dummy = false;
        if (this.activeLobby > 0) {
            if (this.activeLobby == 1) {
                if (this.lobbySinglePlayerPanel.handlePacket(args)) {
                    dummy = true;
                }

                if (this.lobbyChatPanelSingle != null && this.lobbyChatPanelSingle.handlePacket(args)) {
                    dummy = true;
                }
            }

            /*if (this.activeLobby == 2) {
                if (this.lobbyDualPlayerPanel.handlePacket(args)) {
                    dummy = true;
                }

                if (this.lobbyChatPanelDual.handlePacket(args)) {
                    dummy = true;
                }
            }*/

            if (this.activeLobby == 3) {
                if (this.lobbyMultiPlayerPanel.handlePacket(args)) {
                    dummy = true;
                }

                if (this.lobbyChatPanelMulti.handlePacket(args)) {
                    dummy = true;
                }
            }
        }

        if (this.activeLobby == -1 && this.lobbyTrackListAdminPanel.handlePacket(args)) {
            dummy = true;
        }

        if (dummy) {
            ;
        }

    }

    public void broadcastMessage(String var1) {
        if (this.lobbyChatPanelSingle != null) {
            this.lobbyChatPanelSingle.broadcastMessage(var1);
        }

        /*if (this.lobbyChatPanelDual != null) {
            this.lobbyChatPanelDual.broadcastMessage(var1);
        }*/

        if (this.lobbyChatPanelMulti != null) {
            this.lobbyChatPanelMulti.broadcastMessage(var1);
        }

    }

    protected void writeData(String var1) {
        this.gameContainer.connection.writeData("lobby\t" + var1);
    }

    protected String getSelectedNickForChallenge() {
        //return this.lobbyChatPanelDual.getSelectedNickForChallenge();
        return "";
    }

    protected boolean isUserIgnored(String var1) {
        //return this.lobbyChatPanelDual.isUserIgnored(var1);
        return false;
    }

    protected void getUser(String name, boolean var2) {
        //this.lobbyChatPanelDual.getUser(name, var2);
        return;
    }

    protected boolean isNotAcceptingChallenges(String var1) {
        //UserListItem var2 = this.lobbyChatPanelDual.gui_userlist.getUser(var1);
        //return var2 != null ? var2.isNotAcceptingChallenges() : true;
        return false;
    }

    protected Choicer addChoicerNumTracks(Panel container, int x, int y, int width, int height) {
        Choicer c = new Choicer();

        for (int i = 1; i <= 20; ++i) {
            c.addItem(String.valueOf(i));
        }

        c.select(9);
        c.setBounds(x, y, width, height);
        container.add(c);
        return c;
    }

    protected Choicer addChoicerTrackTypes(Panel container, int x, int y, int width, int height) {
        Choicer c = new Choicer();

        for (int i = 0; i < 7; ++i) {
            c.addItem(this.gameContainer.textManager.getGame("LobbyReal_TrackTypes" + i));
        }

        boolean b = this.gameContainer.gameApplet.getPlayerAccessLevel() == 2;
        if (b && !Launcher.isUsingCustomServer()) {//todo <--
            c.addItem(this.gameContainer.textManager.getGame("LobbyReal_TrackTypes7") + " (A)");
            c.addItem("Only best (A)");
            c.addItem("Only pend (A)");
        }

        //if(Launcher.isUsingCustomServer()) {
        //    c.addItem("Only custom");
        //}

        c.select(1);
        c.setBounds(x, y, width, height);
        container.add(c);
        return c;
    }

    protected Choicer addChoicerMaxStrokes(Panel container, int x, int y, int width, int height) {
        Choicer c = new Choicer();

        for (int i = 5; i <= 30; i += 5) {
            c.addItem(String.valueOf(i));
        }

        c.select(3);
        c.setBounds(x, y, width, height);
        container.add(c);
        return c;
    }

    protected Choicer addChoicerTimeLimit(Panel container, int x, int y, int width, int height) {
        Choicer c = new Choicer();

        for (int i = 0; i < numGameTimeLimits; ++i) {
            c.addItem(this.getTime(gameTimeLimits[i]));
        }

        c.select(4);
        c.setBounds(x, y, width, height);
        container.add(c);
        return c;
    }

    protected Choicer addChoicerWaterEvent(Panel container, int x, int y, int width, int height) {
        Choicer c = new Choicer();
        c.addItem(this.gameContainer.textManager.getGame("LobbyReal_WaterEvent1"));
        c.addItem(this.gameContainer.textManager.getGame("LobbyReal_WaterEvent2"));
        c.select(0);
        c.setBounds(x, y, width, height);
        container.add(c);
        return c;
    }

    protected Choicer addChoicerCollision(Panel container, int x, int y, int width, int height) {
        Choicer c = new Choicer();
        c.addItem(this.gameContainer.textManager.getGame("LobbyReal_Collision1"));
        c.addItem(this.gameContainer.textManager.getGame("LobbyReal_Collision2"));
        c.select(1);
        c.setBounds(x, y, width, height);
        container.add(c);
        return c;
    }

    protected Choicer addChoicerScoring(Panel container, int x, int y, int width, int height) {
        Choicer c = new Choicer();
        c.addItem(this.gameContainer.textManager.getGame("LobbyReal_Scoring1"));
        c.addItem(this.gameContainer.textManager.getGame("LobbyReal_Scoring2"));
        c.select(0);
        c.setBounds(x, y, width, height);
        container.add(c);
        return c;
    }

    protected Choicer addChoicerScoringEnd(Panel container, int x, int y, int width, int height) {
        Choicer c = new Choicer();
        c.addItem(this.gameContainer.textManager.getGame("LobbyReal_ScoringEnd0"));
        c.addItem(this.gameContainer.textManager.getGame("LobbyReal_ScoringEnd1"));
        c.addItem(this.gameContainer.textManager.getGame("LobbyReal_ScoringEnd2"));
        c.select(0);
        c.setBounds(x, y, width, height);
        container.add(c);
        return c;
    }

    protected Choicer addChoicerTrackCategory(Panel container, int x, int y, int width, int height) {
        Choicer c = new Choicer();
        c.addItem("Official");
        //c.addItem("Custom");
        c.select(0);
        c.setBounds(x, y, width, height);
        container.add(c);
        return c;
    }

    protected String getTime(int var1) {
        return var1 == 0 ? this.gameContainer.textManager.getGame("LobbyReal_TimeLimitNo") : this.gameContainer.textManager.getTime((long) var1);
    }

    protected void addMessage(String var1) {
        //this.lobbyChatPanelDual.addMessage(var1);
        return;
    }

    protected void quitLobby() {
        this.gameContainer.gameApplet.quit("lobby");
    }
}
