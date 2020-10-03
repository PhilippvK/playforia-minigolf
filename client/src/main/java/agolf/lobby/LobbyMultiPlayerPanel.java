package agolf.lobby;

import agolf.GameApplet;
import agolf.GameContainer;
import com.aapeli.client.FilterTextField;
import com.aapeli.client.InputTextField;
import com.aapeli.client.StringDraw;
import com.aapeli.colorgui.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class LobbyMultiPlayerPanel extends Panel implements ItemListener, ActionListener, MultiColorListListener {

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
            int[] gameData = this.getSelectedGameData();
            if (gameData != null) {
                int yPos2 = 220;
                this.graphics.setFont(GameApplet.fontDialog11);
                if (gameData[3] != 0) {
                    StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_TrackTypes"), this.width - 170, yPos2, 1);
                    this.graphics.drawString(this.gameContainer.textManager.getIfAvailable("LobbyReal_TrackTypes" + gameData[3], this.gameContainer.textManager.getGame("LobbyReal_TrackTypesTest")), this.width - 165, yPos2);
                    yPos2 += 15;
                }

                if (gameData[4] != 20) {
                    StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_MaxStrokes"), this.width - 170, yPos2, 1);
                    this.graphics.drawString(gameData[4] == 0 ? this.gameContainer.textManager.getGame("LobbyReal_MaxStrokesUnlimited") : String.valueOf(gameData[4]), this.width - 165, yPos2);
                    yPos2 += 15;
                }

                if (gameData[5] > 0) {
                    StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_TimeLimit"), this.width - 170, yPos2, 1);
                    this.graphics.drawString(this.gameContainer.lobbyPanel.getTime(gameData[5]), this.width - 165, yPos2);
                    yPos2 += 15;
                }

                if (gameData[6] == 1) {
                    StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_WaterEvent"), this.width - 170, yPos2, 1);
                    this.graphics.drawString(this.gameContainer.textManager.getGame("LobbyReal_WaterEvent2"), this.width - 165, yPos2);
                    yPos2 += 15;
                }

                if (gameData[7] == 0) {
                    StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_Collision"), this.width - 170, yPos2, 1);
                    this.graphics.drawString(this.gameContainer.textManager.getGame("LobbyReal_Collision1"), this.width - 165, yPos2);
                    yPos2 += 15;
                }

                if (gameData[8] == 1) {
                    StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_Scoring"), this.width - 170, yPos2, 1);
                    this.graphics.drawString(this.gameContainer.textManager.getGame("LobbyReal_Scoring2"), this.width - 165, yPos2);
                    yPos2 += 15;
                }

                if (gameData[9] > 0) {
                    StringDraw.drawString(this.graphics, this.gameContainer.textManager.getGame("LobbyReal_ScoringEnd"), this.width - 170, yPos2, 1);
                    this.graphics.drawString(this.gameContainer.textManager.getGame("LobbyReal_ScoringEnd" + gameData[9]), this.width - 165, yPos2);
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
                    gamePassword = "-";
                }

                this.gameContainer.lobbyPanel.writeData("cmpt\t" + gameName + "\t" + gamePassword + "\t" + this.choicerPermission.getSelectedIndex() + "\t" + (this.choicerNumPlayers.getSelectedIndex() + 2) + "\t" + (this.choicerNumTracks.getSelectedIndex() + 1) + "\t" + this.choicerTrackTypes.getSelectedIndex() + "\t" + (this.choicerMaxStrokes.getSelectedIndex() + 1) * 5 + "\t" + LobbyPanel.gameTimeLimits[this.choicerTimeLimit.getSelectedIndex()] + "\t" + this.choicerWaterEvent.getSelectedIndex() + "\t" + this.choicerCollision.getSelectedIndex() + "\t" + this.choicerScoring.getSelectedIndex() + "\t" + this.choicerScoringEnd.getSelectedIndex());
            } else {
                if (evtSource == this.buttonJoin) {
                    this.joinError = 0;
                    this.repaint();
                    int[] gameData = this.getSelectedGameData();
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

    protected void joinMultiPlayerGame(int gameId, String password) {
        this.setVisible(false);
        this.remove(this.lobbyGamePasswordPanel);
        this.lobbyGamePasswordPanel = null;
        this.add(this.trackList);
        this.add(this.buttonJoin);
        this.setVisible(true);
        if (password != null) {
            this.gameContainer.gameApplet.setGameState(0);
            this.gameContainer.lobbyPanel.writeData("jmpt\t" + gameId + "\t" + password);
        }
    }

    protected boolean handlePacket(String[] args) {
        if (args[1].equals("gamelist")) {
            if (args[2].equals("full")) {
                this.gameListFull(args);
                this.repaint();
                return true;
            }

            if (args[2].equals("add")) {
                this.gameListAdd(args, 3, this.trackList.getItemCount() == 0 ? Integer.parseInt(args[3]) : -1);
                this.repaint();
                return true;
            }

            if (args[2].equals("change")) {
                this.gameListChange(args);
                return true;
            }

            if (args[2].equals("remove")) {
                this.gameListRemove(args);
                this.repaint();
                return true;
            }
        }

        return false;
    }

    protected void create() {
        this.setLayout((LayoutManager) null);
        this.choicerNumPlayers = new Choicer();

        for (int num = 2; num <= 10; ++num) {
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

        this.textFieldGamePassword = new InputTextField("-", 15);
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

    private void askPassword(int gameId) {
        this.setVisible(false);
        this.remove(this.trackList);
        this.remove(this.buttonJoin);
        this.lobbyGamePasswordPanel = new LobbyGamePasswordPanel(this.gameContainer, this, gameId);
        this.lobbyGamePasswordPanel.setLocation(this.width * 3 / 4 - 100, this.height / 2 - 30 + 10);
        this.add(this.lobbyGamePasswordPanel);
        this.setVisible(true);
    }

    private void gameListFull(String[] args) {
        // lobby	gamelist	full	1	1595122	test	f	0	3	-1	10	0	25	90	0	1	0	1	1
        // lobby	gamelist	full	2	1595122	test	f	0	3	-1	10	0	25	90	0	1	0	1	1	1595167	sdm	t	0	3	-1	6	1	20	120	0	0	0	0	1
        int numGames = Integer.parseInt(args[3]);
        int defaultGameId = numGames != 1 ? this.getSelectedGameId() : Integer.parseInt(args[4]);
        this.trackList.removeAllItems();

        for (int game = 0; game < numGames; ++game) {
            this.gameListAdd(args, game * 15 + 4, defaultGameId);
        }

        this.joinError = 0;
        this.repaint();
    }

    private void gameListAdd(String[] args, int offset, int defaultGameId) {
        // lobby	gamelist	add	1595163	#1595163	f	0	3	-1	10	1	20	60	0	1	0	0	1
        int id = Integer.parseInt(args[offset]);
        String name = args[offset + 1];
        boolean passworded = args[offset + 2].equals("t");
        int permission = Integer.parseInt(args[offset + 3]);
        int maxPlayers = Integer.parseInt(args[offset + 4]);
        int numTracks = Integer.parseInt(args[offset + 6]);
        int trackTypes = Integer.parseInt(args[offset + 7]);// unsure
        int maxStrokes = Integer.parseInt(args[offset + 8]);// unsure
        int timeLimit = Integer.parseInt(args[offset + 9]);// unsure
        int waterEvent = Integer.parseInt(args[offset + 10]);// unsure
        int collision = Integer.parseInt(args[offset + 11]);// unsure
        int scoring = Integer.parseInt(args[offset + 12]);// unsure
        int scoringEnd = Integer.parseInt(args[offset + 13]);// unsure
        int numPlayers = Integer.parseInt(args[offset + 14]);
        //int trackCategory = isUsingCustomServer ? Integer.parseInt(args[offset + 15]) : -1;
        byte colourIndex = MultiColorListItem.COLOR_BLACK;
        boolean bold = false;
        String[] cols = new String[4];
        if (passworded) {
            cols[0] = this.gameContainer.textManager.getGame("LobbyReal_ListPassword");
            colourIndex = MultiColorListItem.COLOR_RED;
        } else if (permission == 2) {
            cols[0] = this.gameContainer.textManager.getGame("LobbyReal_ListVipOnly");
            colourIndex = MultiColorListItem.COLOR_GREEN;
            bold = true;
        } else if (permission == 1) {
            cols[0] = this.gameContainer.textManager.getGame("LobbyReal_ListRegOnly");
            bold = true;
        }

        cols[1] = name;
        cols[2] = this.gameContainer.textManager.getGame("LobbyReal_ListPlayers", numPlayers, maxPlayers);
        cols[3] = this.gameContainer.textManager.getGame("LobbyReal_ListTracks", numTracks);
        int[] trackInfo;
        /*if(isUsingCustomServer) {
            trackInfo = new int[]{id, passworded ? 1 : 0, permission, trackTypes, maxStrokes, timeLimit, waterEvent, collision, scoring, scoringEnd, trackCategory};
        }
        else {*/
            trackInfo = new int[]{id, passworded ? 1 : 0, permission, trackTypes, maxStrokes, timeLimit, waterEvent, collision, scoring, scoringEnd};
        //}
        MultiColorListItem track = new MultiColorListItem(colourIndex, bold, cols, trackInfo, id == defaultGameId);
        this.trackList.addItem(track);
    }

    private void gameListChange(String[] args) {
        // lobby	gamelist	change	1595163	#1595163	f	0	3	-1	10	1	20	60	0	1	0	0	1
        int selectedGameId = this.getSelectedGameId();
        this.removeTrack(Integer.parseInt(args[3]));
        this.gameListAdd(args, 3, selectedGameId);
    }

    private void gameListRemove(String[] args) {
        int selectedGameId = this.getSelectedGameId();
        int gameId = Integer.parseInt(args[3]);
        this.removeTrack(gameId);
        if (selectedGameId == gameId) {
            this.joinError = 0;
            this.repaint();
        }

    }

    private void removeTrack(int var1) {
        synchronized (trackList) {
            MultiColorListItem[] tracks = this.trackList.getAllItems();
            if (tracks != null) {
                int tracksLen = tracks.length;

                for (int index = 0; index < tracksLen; ++index) {
                    int[] trackData = (int[]) tracks[index].getData();
                    if (trackData[0] == var1) {
                        this.trackList.removeItem(tracks[index]);
                        return;
                    }
                }

            }
        }
    }

    private int getSelectedGameId() {
        int[] var1 = this.getSelectedGameData();
        return var1 != null ? var1[0] : -1;
    }

    private int[] getSelectedGameData() {
        MultiColorListItem var1 = this.trackList.getSelectedItem();
        return var1 == null ? null : (int[]) var1.getData();
    }

    @Override
    public void mouseDoubleClicked(MultiColorListItem clickedItem) {
        this.joinError = 0;
        this.repaint();
        int[] gameData = (int[]) clickedItem.getData();
        if (gameData == null) {
            return;
        }

        attemptJoinGame(gameData);
    }

    private void attemptJoinGame(int[] gameData) {
        if (gameData[1] == 1) {
            this.askPassword(gameData[0]);
            return;
        }

        if ((gameData[2] == 1 || gameData[2] == 2) && !this.gameContainer.gameApplet.isEmailVerified()) {
            this.joinError = 4;
            this.repaint();
            return;
        }

        this.gameContainer.gameApplet.setGameState(0);
        this.gameContainer.lobbyPanel.writeData("jmpt\t" + gameData[0]);
    }
}
