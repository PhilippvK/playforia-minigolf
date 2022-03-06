package agolf.lobby;

import agolf.GameApplet;
import agolf.GameContainer;
import com.aapeli.client.FilterTextField;
import com.aapeli.client.InputTextField;
import com.aapeli.client.StringDraw;
import com.aapeli.colorgui.*;
import org.moparforia.shared.Colors;
import org.moparforia.shared.game.Lobby;
import org.moparforia.shared.game.WeightEnd;
import org.moparforia.shared.tracks.TrackCategory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class LobbyMultiPlayerPanel extends Panel implements ItemListener, ActionListener, MultiColorListListener {

    private GameContainer gameContainer;
    private int width;
    private int height;
    private FilterTextField textFieldGameName;
    private InputTextField textFieldGamePassword;
    private Choicer choicerNumPlayers;
    private Choicer choicerNumTracks;
    private Choicer choicerPermission;
    private Choicer choicerMaxStrokes;
    private Choicer choicerTimeLimit;
    private Choicer choicerTrackTypes;
    private Choicer choicerWaterEvent;
    private Choicer choicerCollision;
    private Choicer choicerScoring;
    private Choicer choicerScoringEnd;
    private ColorButton buttonCreate;
    private ColorButton buttonJoin;
    private MultiColorList trackList;
    private int joinError;
    private LobbyGamePasswordPanel lobbyGamePasswordPanel;
    private Image image;
    private Graphics graphics;
    //private boolean isUsingCustomServer;

    protected LobbyMultiPlayerPanel(GameContainer gameContainer, int width, int height) {
        //isUsingCustomServer = Launcher.isUsingCustomServer();
        this.gameContainer = gameContainer;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.create();
    }

    public void addNotify() {
        super.addNotify();
        this.repaint();
    }

    public void paint(Graphics var1) {
        this.update(var1);
    }

    public void update(Graphics g) {
        if (this.image == null) {
            this.image = this.createImage(this.width, this.height);
            this.graphics = this.image.getGraphics();
        }

        this.graphics.setColor(GameApplet.colourGameBackground);
        this.graphics.fillRect(0, 0, this.width, this.height);
        this.graphics.drawImage(this.gameContainer.imageManager.getImage("bg-lobby-multi"), 0, 0, this);
        Color var2 = new Color(76, 229, 255);
        this.graphics.setColor(GameApplet.colourTextBlack);
        this.graphics.setFont(GameApplet.fontSerif26b);
        StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbySelect_MultiPlayer"), this.width / 2, 35, 0);
        this.graphics.setFont(GameApplet.fontSerif20);
        StringDraw.drawOutlinedString(this.graphics, var2, this.gameContainer.textManager.getGame("LobbyReal_CreateTitle"), this.width / 2 - 185, 50, 1);
        StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_JoinTitle"), this.width * 3 / 4, 60, 0);
        byte yPos = 86;
        this.graphics.setFont(GameApplet.fontDialog12);
        StringDraw.drawOutlinedString(this.graphics, var2, this.gameContainer.textManager.getGame("LobbyReal_PlayerCount"), this.width / 2 - 185, yPos, 1);
        StringDraw.drawOutlinedString(this.graphics, var2, this.gameContainer.textManager.getGame("LobbyReal_TrackCount"), this.width / 2 - 185, yPos + 24, 1);
        if (this.gameContainer.gameApplet.isEmailVerified()) {
            StringDraw.drawOutlinedString(this.graphics, var2, this.gameContainer.textManager.getGame("LobbyReal_GameName"), this.width / 2 - 185, yPos + 50, 1);
        }

        StringDraw.drawOutlinedString(this.graphics, var2, this.gameContainer.textManager.getGame("LobbyReal_GamePassword"), this.width / 2 - 185, yPos + 72, 1);
        if (this.gameContainer.gameApplet.isEmailVerified()) {
            StringDraw.drawOutlinedString(this.graphics, var2, this.gameContainer.textManager.getGame("LobbyReal_OnlyFor"), this.width / 2 - 185, yPos + 94, 1);
        }

        StringDraw.drawOutlinedString(this.graphics, var2, this.gameContainer.textManager.getGame("LobbyReal_TrackTypes"), this.width / 2 - 185, yPos + 117, 1);
        StringDraw.drawOutlinedString(this.graphics, var2, this.gameContainer.textManager.getGame("LobbyReal_MaxStrokes"), this.width / 2 - 185, yPos + 140, 1);
        StringDraw.drawOutlinedString(this.graphics, var2, this.gameContainer.textManager.getGame("LobbyReal_TimeLimit"), this.width / 2 - 185, yPos + 163, 1);
        StringDraw.drawOutlinedString(this.graphics, var2, this.gameContainer.textManager.getGame("LobbyReal_WaterEvent"), this.width / 2 - 185, yPos + 186, 1);
        StringDraw.drawOutlinedString(this.graphics, var2, this.gameContainer.textManager.getGame("LobbyReal_Collision"), this.width / 2 - 185, yPos + 209, 1);
        StringDraw.drawOutlinedString(this.graphics, var2, this.gameContainer.textManager.getGame("LobbyReal_Scoring"), this.width / 2 - 185, yPos + 232, 1);
        StringDraw.drawOutlinedString(this.graphics, var2, this.gameContainer.textManager.getGame("LobbyReal_ScoringEnd"), this.width / 2 - 185, yPos + 255, 1);
        if (this.lobbyGamePasswordPanel == null) {
            Lobby gameData = this.getSelectedGameData();
            if (gameData != null) {
                int yPos2 = 220;
                this.graphics.setFont(GameApplet.fontDialog11);
                if (gameData.getTrackType() != TrackCategory.ALL) {
                    StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_TrackTypes"), this.width - 170, yPos2, 1);
                    this.graphics.drawString(this.gameContainer.textManager.getIfAvailable("LobbyReal_TrackTypes" + gameData.getTrackType().getId(), this.gameContainer.textManager.getGame("LobbyReal_TrackTypesTest")), this.width - 165, yPos2);
                    yPos2 += 15;
                }

                if (gameData.getMaxStrokes() != 20) {
                    StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_MaxStrokes"), this.width - 170, yPos2, 1);
                    this.graphics.drawString(gameData.getMaxStrokes() == 0 ? this.gameContainer.textManager.getGame("LobbyReal_MaxStrokesUnlimited") : String.valueOf(gameData.getMaxStrokes()), this.width - 165, yPos2);
                    yPos2 += 15;
                }

                if (gameData.getTimeLimit() > 0) {
                    StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_TimeLimit"), this.width - 170, yPos2, 1);
                    this.graphics.drawString(this.gameContainer.lobbyPanel.getTime(gameData.getTimeLimit()), this.width - 165, yPos2);
                    yPos2 += 15;
                }

                if (gameData.waterOnStart()) {
                    StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_WaterEvent"), this.width - 170, yPos2, 1);
                    this.graphics.drawString(this.gameContainer.textManager.getGame("LobbyReal_WaterEvent2"), this.width - 165, yPos2);
                    yPos2 += 15;
                }

                if (!gameData.hasCollision()) {
                    StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_Collision"), this.width - 170, yPos2, 1);
                    this.graphics.drawString(this.gameContainer.textManager.getGame("LobbyReal_Collision1"), this.width - 165, yPos2);
                    yPos2 += 15;
                }

                if (gameData.isStrokeScoring()) {
                    StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_Scoring"), this.width - 170, yPos2, 1);
                    this.graphics.drawString(this.gameContainer.textManager.getGame("LobbyReal_Scoring2"), this.width - 165, yPos2);
                    yPos2 += 15;
                }

                if (gameData.getScoringEnd() != WeightEnd.NO) {
                    StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_ScoringEnd"), this.width - 170, yPos2, 1);
                    this.graphics.drawString(this.gameContainer.textManager.getGame("LobbyReal_ScoringEnd" + gameData.getScoringEnd().getId()), this.width - 165, yPos2);
                    yPos2 += 15;
                }
            }
        }

        if (this.joinError > 0) {
            this.graphics.setColor(GameApplet.colourTextRed);
            StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_JoinError" + this.joinError), this.width * 3 / 4, this.height - 15, 0);
        }

        g.drawImage(this.image, 0, 0, this);
    }

    public void itemStateChanged(ItemEvent evt) {
        Object evtSource = evt.getSource();
        if (evtSource == this.choicerTrackTypes) {
            int index = this.choicerTrackTypes.getSelectedIndex();
            if (index == 4) {
                this.choicerMaxStrokes.select(1);
            }

            if (index == 5) {
                this.choicerMaxStrokes.select(2);
            }

            if (index == 6) {
                this.choicerMaxStrokes.select(4);
            }

            if (index < 4 || index > 6) {
                this.choicerMaxStrokes.select(3);
            }

        } else {
            if (evtSource == this.trackList) {
                this.repaint();
            }

        }
    }

    public void actionPerformed(ActionEvent evt) {
        if (this.lobbyGamePasswordPanel == null && this.gameContainer.gameApplet.syncIsValidSite.get()) {
            Object evtSource = evt.getSource();
            if (evtSource == this.buttonCreate) {
                this.gameContainer.gameApplet.setGameState(0);
                String gameName = this.textFieldGameName.getText().trim();
                String gamePassword = this.textFieldGamePassword.getText().trim();
                if (gameName.length() == 0) {
                    gameName = "-";
                }
                if (gamePassword.length() == 0) {
                    gamePassword = null;
                }
                Lobby lobby = new Lobby(
                        null,false, gamePassword != null, gameName,
                        this.choicerNumPlayers.getSelectedIndex() + 2, this.choicerNumTracks.getSelectedIndex() + 1,
                        TrackCategory.getByTypeId(this.choicerTrackTypes.getSelectedIndex()),  this.choicerMaxStrokes.getSelectedIndex() + 1,
                        LobbyPanel.gameTimeLimits[this.choicerTimeLimit.getSelectedIndex()], this.choicerWaterEvent.getSelectedIndex() > 0,
                        this.choicerCollision.getSelectedIndex() > 0, this.choicerScoring.getSelectedIndex() > 0,
                        WeightEnd.getByTypeId(this.choicerScoringEnd.getSelectedIndex())
                );
                this.gameContainer.gameApplet.connection.createLobby(lobby, gamePassword);
//                this.gameContainer.lobbyPanel.writeData("cmpt\t" + gameName + "\t" + gamePassword + "\t" + this.choicerPermission.getSelectedIndex() + "\t" + (this.choicerNumPlayers.getSelectedIndex() + 2) + "\t" + (this.choicerNumTracks.getSelectedIndex() + 1) + "\t" + this.choicerTrackTypes.getSelectedIndex() + "\t" + (this.choicerMaxStrokes.getSelectedIndex() + 1) * 5 + "\t" + LobbyPanel.gameTimeLimits[this.choicerTimeLimit.getSelectedIndex()] + "\t" + this.choicerWaterEvent.getSelectedIndex() + "\t" + this.choicerCollision.getSelectedIndex() + "\t" + this.choicerScoring.getSelectedIndex() + "\t" + this.choicerScoringEnd.getSelectedIndex());
            } else {
                if (evtSource == this.buttonJoin) {
                    this.joinError = 0;
                    this.repaint();
                    Lobby gameData = this.getSelectedGameData();
                    if (gameData == null) {
                        return;
                    }

                    attemptJoinGame(gameData);
                }

            }
        }
    }

    protected void setJoinError(int error) {
        this.joinError = error;
        this.repaint();
    }

//    protected boolean handlePacket(String[] args) {
//        if (args[1].equals("gamelist")) {
//            if (args[2].equals("full")) {
//                this.gameListFull(args);
//                this.repaint();
//                return true;
//            }
//
//            if (args[2].equals("add")) {
//                this.gameListAdd(args, 3, this.trackList.getItemCount() == 0 ? Integer.parseInt(args[3]) : -1);
//                this.repaint();
//                return true;
//            }
//
//            if (args[2].equals("change")) {
//                this.gameListChange(args);
//                return true;
//            }
//
//            if (args[2].equals("remove")) {
//                this.gameListRemove(args);
//                this.repaint();
//                return true;
//            }
//        }
//
//        return false;
//    }

    protected void create() {
        this.setLayout((LayoutManager) null);
        this.choicerNumPlayers = new Choicer();

        for (int num = 2; num <= 4; ++num) {
            this.choicerNumPlayers.addItem(String.valueOf(num));
        }

        this.choicerNumPlayers.select(1);
        this.choicerNumPlayers.setBounds(this.width / 2 - 170, 71, 50, 20);
        this.add(this.choicerNumPlayers);
        this.choicerNumTracks = this.gameContainer.lobbyPanel.addChoicerNumTracks(this, this.width / 2 - 170, 95, 50, 20);
        this.textFieldGameName = new FilterTextField(this.gameContainer.textManager, "-", 15);
        this.textFieldGameName.setBounds(this.width / 2 - 170, 121, 150, 20);
        this.textFieldGameName.setBackground(Color.white);
        if (this.gameContainer.gameApplet.isEmailVerified()) {
            this.add(this.textFieldGameName);
        }

        this.textFieldGamePassword = new InputTextField("", 15);
        this.textFieldGamePassword.setBounds(this.width / 2 - 170, 143, 150, 20);
        this.textFieldGamePassword.setBackground(Color.white);
        this.add(this.textFieldGamePassword);
        this.choicerPermission = new Choicer();
        this.choicerPermission.addItem(this.gameContainer.textManager.getGame("LobbyReal_OnlyForAll"));
        if (this.gameContainer.gameApplet.isEmailVerified()) {
            this.choicerPermission.addItem(this.gameContainer.textManager.getGame("LobbyReal_OnlyForReg"));
            this.choicerPermission.setBounds(this.width / 2 - 170, 165, 150, 20);
            this.add(this.choicerPermission);
        }

        this.choicerPermission.select(0);
        /*if(isUsingCustomServer) {
            this.choicerTrackCategory = this.gameContainer.lobbyPanel.addChoicerTrackCategory(this, this.width / 2 - 170, 188, 150, 20);
        }*/
        this.choicerTrackTypes = this.gameContainer.lobbyPanel.addChoicerTrackTypes(this, this.width / 2 - 170, /*isUsingCustomServer ? 211 :*/ 188, 150, 20);
        this.choicerTrackTypes.addItemListener(this);
        this.choicerMaxStrokes = this.gameContainer.lobbyPanel.addChoicerMaxStrokes(this, this.width / 2 - 170, /*isUsingCustomServer ? 234 :*/ 211, 100, 20);
        this.choicerTimeLimit = this.gameContainer.lobbyPanel.addChoicerTimeLimit(this, this.width / 2 - 170, /*isUsingCustomServer ? 257 :*/ 234, 100, 20);
        this.choicerWaterEvent = this.gameContainer.lobbyPanel.addChoicerWaterEvent(this, this.width / 2 - 170, /*isUsingCustomServer ? 280 :*/ 257, 150, 20);
        this.choicerCollision = this.gameContainer.lobbyPanel.addChoicerCollision(this, this.width / 2 - 170, /*isUsingCustomServer ? 303 :*/ 280, 100, 20);
        this.choicerScoring = this.gameContainer.lobbyPanel.addChoicerScoring(this, this.width / 2 - 170, /*isUsingCustomServer ? 326 :*/ 303, 150, 20);
        this.choicerScoringEnd = this.gameContainer.lobbyPanel.addChoicerScoringEnd(this, this.width / 2 - 170, /*isUsingCustomServer ? 349 :*/ 326, 100, 20);
        this.buttonCreate = new ColorButton(this.gameContainer.textManager.getGame("LobbyReal_CreateGame"));
        this.buttonCreate.setBackground(GameApplet.colourButtonGreen);
        this.buttonCreate.setBounds(this.width / 2 - 170, /*isUsingCustomServer ? 372 :*/ 365, 100, /*isUsingCustomServer ? 20 :*/ 25);
        this.buttonCreate.addActionListener(this);
        this.add(this.buttonCreate);
        String[] listTitles = new String[]{this.gameContainer.textManager.getGame("LobbyReal_ListTitleUserLimit"), this.gameContainer.textManager.getGame("LobbyReal_ListTitleGame"), this.gameContainer.textManager.getGame("LobbyReal_ListTitlePlayers"), this.gameContainer.textManager.getGame("LobbyReal_ListTitleTracks")};
        int[] var3 = new int[]{0, 0, 2, 3};
        this.trackList = new MultiColorList(listTitles, var3, 1, this.width / 2 - 40, 125);
        this.trackList.setLocation(this.width / 2 + 20, 75);
        this.trackList.setBackgroundImage(this.gameContainer.imageManager.getImage("bg-lobby-multi-fade"), this.width / 2 + 20, 75);
        this.trackList.setSelectable(1);
        this.trackList.addItemListener(this);
        trackList.setListListener(this);
        this.add(this.trackList);
        this.buttonJoin = new ColorButton(this.gameContainer.textManager.getGame("LobbyReal_JoinGame"));
        this.buttonJoin.setBackground(GameApplet.colourButtonGreen);
        this.buttonJoin.setBounds(this.width * 3 / 4 - 50, 330, 100, 25);
        this.buttonJoin.addActionListener(this);
        this.add(this.buttonJoin);
    }

    private void askPassword(Lobby lobby) {
        this.setVisible(false);
        this.remove(this.trackList);
        this.remove(this.buttonJoin);
        this.lobbyGamePasswordPanel = new LobbyGamePasswordPanel(this.gameContainer, this, lobby);
        this.lobbyGamePasswordPanel.setLocation(this.width * 3 / 4 - 100, this.height / 2 - 30 + 10);
        this.add(this.lobbyGamePasswordPanel);
        this.setVisible(true);
    }

    public void setLobbies(List<Lobby> lobbies) {
        // lobby	gamelist	full	1	1595122	test	f	0	3	-1	10	0	25	90	0	1	0	1	1
        // lobby	gamelist	full	2	1595122	test	f	0	3	-1	10	0	25	90	0	1	0	1	1	1595167	sdm	t	0	3	-1	6	1	20	120	0	0	0	0	1
//        int numGames = Integer.parseInt(args[3]);
//        int defaultGameId = numGames != 1 ? this.getSelectedGameId() : Integer.parseInt(args[4]);
        this.trackList.removeAllItems();

        for (Lobby lobby: lobbies) {
            this.addLobby(lobby);
        }

        this.joinError = 0;
        this.repaint();
    }

    public void addLobby(Lobby lobby) {
        // lobby	gamelist	add	1595163	#1595163	f	0	3	-1	10	1	20	60	0	1	0	0	1
        String name = lobby.getName();
        boolean passworded = lobby.hasPassword();
        int maxPlayers = lobby.getMaxPlayers();
        int numTracks = lobby.getNumTracks();
        int numPlayers = lobby.getPlayers().size();Color colour = Colors.BLACK;
        boolean bold = false;
        String[] cols = new String[4];
        if (passworded) {
            cols[0] = this.gameContainer.textManager.getGame("LobbyReal_ListPassword");
            colour = Colors.RED;
        }

        cols[1] = name;
        cols[2] = this.gameContainer.textManager.getGame("LobbyReal_ListPlayers", numPlayers, maxPlayers);
        cols[3] = this.gameContainer.textManager.getGame("LobbyReal_ListTracks", numTracks);
        /*if(isUsingCustomServer) {
            trackInfo = new int[]{id, passworded ? 1 : 0, permission, trackTypes, maxStrokes, timeLimit, waterEvent, collision, scoring, scoringEnd, trackCategory};
        }
        else {*/
//        trackInfo = new int[]{this.trackList.getItemCount(), passworded ? 1 : 0, permission, trackTypes, maxStrokes, timeLimit, waterOnStart, collision, strokeScoring, scoringEnd};
        //}
        MultiColorListItem track = new MultiColorListItem(colour, bold, cols, lobby, false);
        this.trackList.addItem(track);
    }

    public void updateLobby(Lobby lobby) {
        // lobby	gamelist	change	1595163	#1595163	f	0	3	-1	10	1	20	60	0	1	0	0	1
        this.removeLobby(lobby.getName());
        this.addLobby(lobby);
    }

    public void removeLobby(String name) {
        synchronized (trackList) {
            for (MultiColorListItem item : this.trackList.getAllItems()) {
                if (name.equals(item.getData().getName())) {
                    this.trackList.removeItem(item);
                }
            }
        }
    }


    private Lobby getSelectedGameData() {
        MultiColorListItem var1 = this.trackList.getSelectedItem();
        return var1 == null ? null : var1.getData();
    }

    @Override
    public void mouseDoubleClicked(MultiColorListItem clickedItem) {
        this.joinError = 0;
        this.repaint();
        Lobby gameData = clickedItem.getData();
        if (gameData == null) {
            return;
        }

        attemptJoinGame(gameData);
    }

    private void attemptJoinGame(Lobby lobby) {
        if (lobby.hasPassword()) {
            this.askPassword(lobby);
            return;
        }

//        if ((gameData[2] == 1 || gameData[2] == 2) && !this.gameContainer.gameApplet.isEmailVerified()) {
//            this.joinError = 4;
//            this.repaint();
//            return;
//        }

        this.gameContainer.gameApplet.setGameState(0);
        this.gameContainer.gameApplet.connection.joinLobby(lobby);
    }
}
