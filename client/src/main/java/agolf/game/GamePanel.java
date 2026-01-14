package agolf.game;

import agolf.GameContainer;
import agolf.GolfGameFrame;
import agolf.SynchronizedBool;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.StringTokenizer;
import org.moparforia.client.Launcher;

public class GamePanel extends Panel {

    private GameContainer gameContainer;
    private int width;
    private int height;
    private PlayerInfoPanel playerInfoPanel;
    private GameCanvas gameCanvas;
    private ChatPanel chatPanel;
    private TrackInfoPanel trackInfoPanel;
    private GameControlPanel gameControlPanel;
    protected int state;
    private int playerCount;
    private boolean isSinglePlayerGame;
    private boolean aBoolean363;
    private long aLong364;
    private final Object canStrokeLock;
    private boolean isWaitingForTurnStart;

    public GamePanel(GameContainer gameContainer, int width, int height, Image image) {
        this.gameContainer = gameContainer;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.create(image);
        this.aBoolean363 = true;
        this.canStrokeLock = new Object();
        this.state = -1;
        this.setState(0);
    }

    public void addNotify() {
        super.addNotify();
        this.repaint();
    }

    public void paint(Graphics var1) {
        this.update(var1);
    }

    public void update(Graphics var1) {
        var1.setColor(GolfGameFrame.colourGameBackground);
        var1.fillRect(0, 0, this.width, this.height);
    }

    public void handlePacket(String[] args) {
        switch (args[1]) {
            case "gameinfo" -> {
                String gameName = args[2];
                boolean passworded = args[3].equals("t");
                int permission = Integer.parseInt(args[4]);
                this.playerCount = Integer.parseInt(args[5]);
                int trackCount = Integer.parseInt(args[6]);
                int trackTypes = Integer.parseInt(args[7]);
                int maxStrokes = Integer.parseInt(args[8]);
                int strokeTimeout = Integer.parseInt(args[9]);
                int waterEvent = Integer.parseInt(args[10]);
                int collision = Integer.parseInt(args[11]);
                int trackScoring = Integer.parseInt(args[12]);
                int trackScoringEnd = Integer.parseInt(args[13]);
                this.isSinglePlayerGame = args[14].equals("t"); // todo unsure

                // int trackCategory = Launcher.isUsingCustomServer() ? Integer.parseInt(args[15]) : -1;
                byte mode = 0;
                if (this.gameContainer.synchronizedTrackTestMode.get()) {
                    mode = 1;
                }

                if (this.playerCount > 1) {
                    mode = 2;
                }

                this.addMultiPlayerPanels(mode);
                this.playerInfoPanel.init(this.playerCount, trackCount, maxStrokes, strokeTimeout, trackScoring);
                this.trackInfoPanel.setNumTracks(trackCount);
                this.gameControlPanel.setPlayerCount(this.playerCount);
                this.gameCanvas.init(this.playerCount, waterEvent, collision);
                if (mode == 2) {
                    String settings = "";
                    if (passworded) {
                        settings = this.gameContainer.textManager.getText("GameChat_GS_Password") + ", ";
                    } else if (permission > 0) {
                        settings = this.gameContainer.textManager.getText(
                                        "GameChat_GS_" + (permission == 1 ? "Reg" : "Vip") + "Only")
                                + ", ";
                    }

                    settings =
                            settings + this.gameContainer.textManager.getText("GameChat_GS_Players", this.playerCount);
                    settings =
                            settings + ", " + this.gameContainer.textManager.getText("GameChat_GS_Tracks", trackCount);
                    if (trackTypes > 0) {
                        settings = settings
                                + " ("
                                + this.gameContainer.textManager.getIfAvailable(
                                        "LobbyReal_TrackTypes" + trackTypes,
                                        this.gameContainer.textManager.getText("LobbyReal_TrackTypesTest"))
                                + ")";
                    }

                    if (maxStrokes != 20) {
                        if (maxStrokes > 0) {
                            settings = settings
                                    + ", "
                                    + this.gameContainer.textManager.getText("GameChat_GS_MaxStrokes", maxStrokes);
                        } else {
                            settings = settings
                                    + ", "
                                    + this.gameContainer.textManager.getText(
                                            "GameChat_GS_MaxStrokesUnlimited", maxStrokes);
                        }
                    }

                    if (strokeTimeout > 0) {
                        settings = settings
                                + ", "
                                + this.gameContainer.textManager.getText(
                                        "GameChat_GS_TimeLimit" + (strokeTimeout < 60 ? "Sec" : "Min"),
                                        strokeTimeout < 60 ? strokeTimeout : strokeTimeout / 60);
                    }

                    if (waterEvent == 1) {
                        settings = settings + ", " + this.gameContainer.textManager.getText("GameChat_GS_WaterShore");
                    }

                    if (collision == 0) {
                        settings = settings + ", " + this.gameContainer.textManager.getText("GameChat_GS_NoCollision");
                    }

                    if (trackScoring == 1) {
                        settings = settings + ", " + this.gameContainer.textManager.getText("GameChat_GS_TrackScoring");
                    }

                    if (trackScoringEnd > 0) {
                        settings = settings
                                + ", "
                                + this.gameContainer.textManager.getText(
                                        "GameChat_GS_TrackScoringEnd" + trackScoringEnd);
                    }

                    /*if(trackCategory > -1) {
                        settings = settings + ", " + (trackCategory == 0 ? "official" : (trackCategory == 1 ? "custom" : "unknown")) + " maps";
                    }*/

                    this.chatPanel.addMessage(this.gameContainer.textManager.getText("GameChat_GameName", gameName));
                    this.chatPanel.addMessage(
                            this.gameContainer.textManager.getText("GameChat_GameSettings", settings));
                }
            }
            case "scoringmulti" -> {
                int len = args.length - 2;
                int[] trackScoresMultipliers = new int[len];

                for (int track = 0; track < len; ++track) {
                    trackScoresMultipliers[track] = Integer.parseInt(args[2 + track]);
                }

                this.playerInfoPanel.setTrackScoresMultipliers(trackScoresMultipliers);
            }
            case "players" -> {
                int len = (args.length - 2) / 3;
                int playerCountIndex = 2;

                for (int trackTypes = 0; trackTypes < len; ++trackTypes) {
                    int playerCount = Integer.parseInt(args[playerCountIndex]); // todo lol why u inside the loop tho
                    String clan = args[playerCountIndex + 2].equals("-") ? null : args[playerCountIndex + 2];
                    this.playerInfoPanel.addPlayer(playerCount, args[playerCountIndex + 1], clan, false);
                    this.chatPanel.setUserColour(args[playerCountIndex + 1], playerCount);
                    playerCountIndex += 3;
                }
            }
            case "owninfo" -> {
                int currentPlayerID = Integer.parseInt(args[2]);
                String currentPlayerClan = args[4].equals("-") ? null : args[4];
                this.playerInfoPanel.addPlayer(currentPlayerID, args[3], currentPlayerClan, true);
                this.chatPanel.setUserColour(args[3], currentPlayerID);
                this.aLong364 = System.currentTimeMillis();
            }
            case "join" -> {
                int playerId = Integer.parseInt(args[2]);
                String playerClan = args[4].equals("-") ? null : args[4];
                this.playerInfoPanel.addPlayer(playerId, args[3], playerClan, false);
                this.chatPanel.setUserColour(args[3], playerId);
                if (this.playerCount != 2 || playerId != 1) {
                    this.chatPanel.addMessage(
                            playerClan != null
                                    ? this.gameContainer.textManager.getText("GameChat_JoinClan", args[3], playerClan)
                                    : this.gameContainer.textManager.getText("GameChat_Join", args[3]));
                }
            }
            case "part" -> { // player left game
                int playerId = Integer.parseInt(args[2]);
                boolean changed = this.playerInfoPanel.setPlayerPartStatus(playerId, Integer.parseInt(args[3]));
                if (changed) {
                    this.gameControlPanel.method329();
                }

                String playerName = this.playerInfoPanel.playerNames[playerId];
                this.chatPanel.addMessage(this.gameContainer.textManager.getText("GameChat_Part", playerName));
                this.chatPanel.removeUserColour(playerName);
                this.gameControlPanel.refreshBackButton();
            }
            case "say" -> {
                int playerId = Integer.parseInt(args[2]);
                this.chatPanel.addSay(this.playerInfoPanel.playerNames[playerId], args[3], false);
            }
            case "cr" -> {
                StringTokenizer tokenizer = new StringTokenizer(args[2], ",");
                int tracks = tokenizer.countTokens();
                int[][] comparisonScores = new int[5][tracks];

                for (int comparisonType = 0; comparisonType < 5; ++comparisonType) {
                    for (int track = 0; track < tracks; ++track) {
                        comparisonScores[comparisonType][track] = Integer.parseInt(tokenizer.nextToken());
                    }

                    if (comparisonType < 4) {
                        tokenizer = new StringTokenizer(args[3 + comparisonType], ",");
                    }
                }

                this.playerInfoPanel.initResultsComparison(comparisonScores);
            }
            case "start" -> {
                if (this.playerCount > 1) {
                    if (this.aBoolean363) {
                        if (System.currentTimeMillis() > this.aLong364 + 1000L) {
                            this.gameContainer.soundManager.playNotify();
                            // this.requestFocus();//todo this is annoying as fuck
                        }

                        this.gameContainer.golfGameFrame.showPlayerList(this.playerInfoPanel.getPlayerNames());
                    } else {
                        this.gameContainer.golfGameFrame.removePlayerListWinnders();
                    }
                }

                this.aBoolean363 = false;
                this.gameCanvas.initializeGameArea();
                this.playerInfoPanel.reset();
                this.trackInfoPanel.resetCurrentTrack();
                this.setState(1);
            }
            case "starttrack" -> {
                // [1] = "startrack", (optional [2] == track test mode), [2 or 3] == player statuses, [3
                // or 4] == game id, [4 or 5] == track data
                /*
                * game
                * starttrack
                * t 1908821
                * V 1
                * A Tiikoni
                * N Three Passages III
                * T B3A12DBQARG20DBQARG12DE11DBTARBERQBAQQ20DBFRQBRARE11DE12DBAQQG20DFG12DE12DEE20DEE12DE12DEE20DEE12DE7DBQARE3DEE20DEE3DBQARE7DE6DBTARBERQBAQQ3DBGRQBRARE18DBTARBHRQF3DBFRQBRARE6DE7DBAQQG3DBSARG20DBSARG3DFG7DE7DEE6DBQARE14DBQARE6DEE7DE7DEE5DBTARBERQBAQQ14DBFRQBRARE5DEE7DE7DEE6DBAQQG14DFG6DEE7DE7DEE6DEE14DEE6DEE7DE7DEE6DEEDDBQARE10DEE6DEE7DE7DEE6DEEDBTARBERQBAQQ10DBGRQBRARE5DEE7DE7DEE6DEEDDBAQQG10DBSARG6DEE7DE7DEE6DEEDDEE13DBQARE3DEE7DE7DEE6DEEDDEE12DBTARBERQBAQQ3DBGRQBRARE6DE7DEE6DEEDDEEDDCBAE9DBAQQG3DBSARG7DE7DEE6DEEDDEE13DEE12DE7DEEDDCAAEDDEEDDEE13DEE12DE7DEE6DEEDDEE13DEE12DE7DEE6DEEDBTARBHRQF13DBGRQBRARE11DE7DEE6DEEDDBSARG13DBSARG12DE6DBTARBHRQF6DBGRQBRARE30DE7DBSARG6DBSARG31D,Ads:A3703B0101C4019	I 456956,1954871,2,17833	B Jerry,1087842155000	L abscission,1369657760469	R 1047,334,392,574,911,2281,1888,1543,1209,871,6559



                V 1
                A {AUTHOR OF TRACK}
                N {NAME OF TRACK}
                T B3A11DBEAQBAQQ11DBAMMDDBGQMBAQQ11DBAMMDDBGQMBAQQ3DEDDBIALBHLEBGFEBJAFE3DBEAQBGAQB3A10DEEDBGQMBGAQB3A10DEEDBGQMBGAQB3ADCAAEEDDBHKLBALABAFABGGFEDDBEAQBGAQI11DEEBGQMBGAQB3A11DEEBGQMBGAQB3A3DEEDDBGJKBAJABAHABHHGEDBEAQBGAQI12DEBGQMBGAQB3A12DEBGQMBGAQB3A4DEEDDBLAJBGIJBHIHBKAHEBEAQBGAQI5DBEAQBGAQE5DEBGAQB3A5DBEAQBGAQE5DEBGAQB3A5DEE6DBEAQBGAQI5DBEAQBGAQH5DBEAQBGAQI5DBEAQBGAQH5DBEAQBGAQI5DBEAQBGAQBJAME4DBEAQBGAQB3A5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQIBAMMBLMAEDDBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQIDEDDBJAMBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQIDDEDDBGQMBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI3DEDBGQMBGAQB3A5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI4DEBGQMBGAQB3A5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQI5DBEAQBGAQIDBIALBHLEBGFEBJAFBTMQBTQAB3A5DBTAQBTQAI5DBTAQBTQAI5DBTAQBTQAI5DBTAQBTQAI5DBTAQBTQAIDDBHKLBALABAFABGGFBKAMBHAQBFAQE5DBHAQBFAQE5DBHAQBFAQE5DBHAQBFAQE5DBHAQBFAQE5DBHAQBFAQEDBGJKBAJABAHABHHGB3ADFFE5DFFE5DFFE5DFFE5DFFE5DFFEBLAJBGIJBHIHBKAHEDDFFE5DFFE5DFFE5DFFE5DFFE5D3F3DE3DFFE5DFFE5DFFE5DFFE5DFFE5DFFEDBKMAE4DFFE5DFFE5DFFE5DFFE5DFFE5DFFBIAMBAMME5DFFE5DFFE5DFFE5DFFE5DFFE5DFBHQMEE6DFFE5DFFE5DFFE5DFFE5DFFE5DFFEDBIALBHLEBGFEBJAFEDFFEDCBAEDBEAQBAQQFE5DFE5DBEAQBAQQFE5DFE6DBRQAEDBHKLBALABAFABGGFEDDFFEDDBEAQBEQMBAMMBHMQFE10DBEAQBEQMBAMMBHMQFE11DBEAQBEQMEDBGJKBAJABAHABHHGE3DFFEBEAQBEQMBAMMDDFFE8DBEAQBEQMBAMMDDFFE9DBEAQBEQMBAMMEDBLAJBGIJBHIHBKAHE3DBIAMBHMQBSQABEQMBAMM4DFFE6DBEAQBEQMBAMM4DFFE7DBEAQBEQMBAMMDE8DBKMABAMMDBSMQG6DFBAQQ6DBEQMBAMM6DFBAQQ7DBEQMBAMMDD
                I {NUMBER OF PLAYERS TO COMLETE?},{NUMBER OF PEOPLE TO ATTEMPT?},{BEST NUMBER OF STROKES},{NUMBER OF PEOPLE THAT GOT BEST STROKE}
                B {FIRST BEST PAR PLAYER},{UNIX TIMESTAMP OF FIRST BEST PAR}000
                L {LAST BEST PAR PLAYER},{UNIX TIMESTAMP OF LAST BEST PAR}000
                R {RATING: 0},{RATING: 1},{RATING: 2},{RATING: 3},{RATING: 4},{RATING: 5},{RATING: 6},{RATING: 7},{RATING: 8},{RATING: 9},{RATING: 10}
                */
                this.gameCanvas.restartGame();
                boolean trackTestMode1 = args[2].equals("ttm1");
                boolean trackTestMode2 = args[2].equals("ttm2");
                boolean trackTestMode = trackTestMode1 || trackTestMode2;
                boolean hasPlayed = false;
                int startIndex = trackTestMode ? 5 : 4;
                int argsLen = args.length;
                String author = null;
                String name = null;
                String data = null;
                String fullInstruction = "";

                for (int commandIndex = startIndex; commandIndex < argsLen; ++commandIndex) {
                    char command = args[commandIndex].charAt(0);
                    if (command == 'A') {
                        author = args[commandIndex].substring(2);
                    }

                    if (command == 'N') {
                        name = args[commandIndex].substring(2);
                    }

                    if (command == 'T') {
                        data = args[commandIndex].substring(2);
                    }

                    if (command == 'T' && args[commandIndex].charAt(2) == '!') { // a track we already played?
                        args[commandIndex] = "T " + this.gameContainer.trackCollection.getTrack(author, name);
                        hasPlayed = true;
                    }

                    fullInstruction = fullInstruction + args[commandIndex];
                    if (commandIndex < argsLen - 1) {
                        fullInstruction = fullInstruction + '\n';
                    }
                }

                if (Launcher.debug()) System.out.println("FULL: " + fullInstruction);

                if (!hasPlayed) {
                    this.gameContainer.trackCollection.addTrack(author, name, data);
                }

                this.gameCanvas.init(
                        fullInstruction, args[trackTestMode ? 3 : 2], Integer.parseInt(args[trackTestMode ? 4 : 3]));

                /* trackinformation
                 [0]=author, [1]=trackname, [2]=firstbest, [3]=lastbest

                 statistics:
                  var15[0][0]= number completeed
                  var15[0][1]= total attempts
                  var15[0][2]= best par (stroke count)
                  var15[0][3]= number of best par strokes
                  var15[1][0]= number of ratings: 0
                  var15[1][1]= number of ratings: 1
                  var15[1][2]= number of ratings: 2
                  var15[1][3]= number of ratings: 3
                */
                String[] trackInformation = this.gameCanvas.trackStats.generateTrackInformation();
                int[][] trackStats = this.gameCanvas.trackStats.generateTrackStatistics();

                this.trackInfoPanel.parseTrackInfoStats(
                        trackInformation[0],
                        trackInformation[1],
                        trackStats[0],
                        trackStats[1],
                        trackInformation[2],
                        trackInformation[3],
                        trackTestMode1,
                        trackTestMode2,
                        this.gameCanvas.hasCoordinates());

                int trackScoreMultiplier = this.playerInfoPanel.startNextTrack();
                if (trackScoreMultiplier > 1) {
                    this.chatPanel.addMessage(
                            gameContainer.textManager.getText("GameChat_ScoreMultiNotify", trackScoreMultiplier));
                }

                this.gameControlPanel.displaySkipButton(); // checks if you can skip on first shot

                if (this.gameContainer.synchronizedTrackTestMode.get()) {
                    this.chatPanel.printSpecialSettingstoTextArea(
                            // First arg should be comment, but v2 tracks don't
                            // have comments
                            this.gameCanvas.track.getAuthor(),
                            this.gameCanvas.track.getSettings(),
                            this.gameCanvas.map.getLatestTileSpeciality());
                }
            }
            case "startturn" -> {
                this.isWaitingForTurnStart = false;
                int playerId = Integer.parseInt(args[2]);

                boolean canPlay = this.playerInfoPanel.startTurn(playerId);
                // canPlay = true;
                this.gameCanvas.startTurn(playerId, canPlay, !this.chatPanel.haveFocus());

                if (!this.isSinglePlayerGame) {
                    int trackCount = this.playerInfoPanel.method377();
                    if (trackCount >= 10 || trackCount >= this.trackInfoPanel.method385()) {
                        this.gameControlPanel.showSkipButton();
                    }
                }
            }
            case "beginstroke" -> {
                int playerId = Integer.parseInt(args[2]);
                this.playerInfoPanel.strokeStartedOrEnded(playerId, false);
                this.gameContainer.soundManager.playGameMove();
                this.playerInfoPanel.stopTimer();
                this.gameCanvas.decodeCoords(playerId, false, args[3]);
            }
            case "changescore" -> {
                int numScores = args.length - 3;
                int[] trackScores = new int[numScores];

                for (int trackCount = 0; trackCount < numScores; ++trackCount) {
                    trackScores[trackCount] = Integer.parseInt(args[3 + trackCount]);
                }

                this.playerInfoPanel.setScores(Integer.parseInt(args[2]), trackScores);
            }
            case "voteskip" -> this.playerInfoPanel.voteSkip(Integer.parseInt(args[2]));
            case "resetvoteskip" -> {
                this.playerInfoPanel.voteSkipReset();
                if (!this.gameCanvas.getSynchronizedBool(this.playerInfoPanel.playerId)) {
                    this.gameControlPanel.showSkipButton();
                }
            }
            case "rfng" -> this.playerInfoPanel.readyForNewGame(Integer.parseInt(args[2]));
            case "end" -> {
                this.gameCanvas.endGame();
                int len = args.length - 2;
                if (len > 0) {
                    int[] gameOutcome = new int[len];
                    boolean[] isWinner = new boolean[len];

                    for (int i = 0; i < len; ++i) {
                        gameOutcome[i] = Integer.parseInt(args[2 + i]);
                        isWinner[i] = gameOutcome[i] == 1;
                    }

                    this.playerInfoPanel.setGameOutcome(gameOutcome);
                    this.gameContainer.golfGameFrame.showPlayerListWinners(isWinner);
                } else {
                    this.playerInfoPanel.setGameOutcome(null);
                }

                this.setState(2); // game state?

                if (this.isSinglePlayerGame) {
                    this.gameContainer.lobbyPanel.requestTrackSetList();
                }
            }
        }
    }

    protected void sendChatMessage(String message) {
        String var2 = "say\t" + message;
        this.gameContainer.connection.writeData("game\t" + var2);
        this.chatPanel.addSay(this.playerInfoPanel.playerNames[this.playerInfoPanel.playerId], message, true);
    }

    protected void setBeginStroke(int playerId, int x, int y, int shootingMode) {
        this.trackInfoPanel.method384();
        this.playerInfoPanel.strokeStartedOrEnded(playerId, false);
        String data = "beginstroke\t" + this.encodeCoords(x, y, shootingMode);
        this.gameContainer.connection.writeData("game\t" + data);
        this.gameContainer.soundManager.playGameMove();
    }

    protected void method336() {
        String coords = this.gameCanvas.encodeCoordinates();
        if (coords != null) {
            this.playerInfoPanel.strokeStartedOrEnded(0, false);
            String var2 = "beginstroke\t" + coords;
            this.gameContainer.connection.writeData("game\t" + var2);
            this.gameCanvas.decodeCoords(0, true, coords);
        }
    }

    protected boolean isValidPlayerID(int player) {
        return this.playerInfoPanel.isOverStrokeLimit(player);
    }

    protected void sendEndStroke(int playerid, SynchronizedBool[] settings, int var3) {
        String data = "endstroke\t" + playerid + "\t";

        for (int index = 0; index < settings.length; ++index) {
            if (var3 == index && !settings[index].get()) {
                this.playerInfoPanel.strokeStartedOrEnded(index, true);
                data = data + "p";
            } else {
                data = data + (settings[index].get() ? "t" : "f");
            }
        }

        this.gameContainer.connection.writeData("game\t" + data);
    }

    protected boolean skipButtonPressed(boolean isSinglePlayer) {
        if (this.state == 1) {
            if (!isSinglePlayer) {
                this.playerInfoPanel.voteSkip();
                if (this.playerInfoPanel.shouldSkipTrack() && this.gameCanvas.method137()) {
                    this.gameCanvas.restartGame();
                }

                this.gameContainer.connection.writeData("game\tvoteskip");
                return true;
            }

            if (this.gameCanvas.method137()) {
                this.gameCanvas.restartGame();
                this.playerInfoPanel.method372();
                this.gameContainer.connection.writeData("game\tskip");
                return true;
            }
        }

        return false;
    }

    protected void hideSkipButton() {
        this.gameControlPanel.hideSkipButton();
    }

    protected void requestNewGame() {
        this.gameContainer.connection.writeData("game\tnewgame");
    }

    protected void leaveGame() {
        this.gameCanvas.restartGame();
        this.playerInfoPanel.stopTimer();
        this.gameContainer.golfGameFrame.setGameState(0);
        this.gameContainer.connection.writeData("game\tback");
        this.gameContainer.golfGameFrame.removePlayerList();
    }

    protected void rateTrack(int track, int rating) {
        String message = "rate\t" + track + "\t" + rating;
        this.gameContainer.connection.writeData("game\t" + message);
    }

    protected void respondNewGame(int track, boolean accept) { // why track
        String message = "rejectaccept\t" + track + "\t" + (accept ? "t" : "f");
        this.gameContainer.connection.writeData("game\t" + message);
    }

    protected void backToPrivate(int currentTrack) {
        String message = "backtoprivate\t" + currentTrack;
        this.gameContainer.connection.writeData("game\t" + message);
    }

    protected boolean maxFps() {
        return this.gameControlPanel.maxFps();
    }

    protected String[] getPlayerName(int playerId) {
        return this.playerInfoPanel.getPlayerName(playerId);
    }

    protected void setPlayerNamesDisplayMode(int mode) {
        this.gameCanvas.setPlayerNamesDisplayMode(mode);
    }

    public void broadcastMessage(String message) {
        this.chatPanel.addBroadcastMessage(message);
    }

    protected boolean tryStroke(boolean didTimeout) {
        synchronized (canStrokeLock) {
            if (this.isWaitingForTurnStart) {
                return false;
            }

            this.isWaitingForTurnStart = true;
        }

        if (didTimeout) {
            this.gameCanvas.doZeroLengthStroke();
        } else {
            this.playerInfoPanel.stopTimer();
        }

        return true;
    }

    private void create(Image image) {
        if (this.gameContainer.golfGameFrame.syncIsValidSite.get()) {
            this.setLayout(null);
            this.playerInfoPanel = new PlayerInfoPanel(this.gameContainer, 735, 60);
            this.playerInfoPanel.setLocation(0, 0);
            this.add(this.playerInfoPanel);
            Image cursorImage = this.gameContainer.imageManager.getImage("game-cursor");
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Cursor c = toolkit.createCustomCursor(cursorImage, new Point(11, 11), "game-cursor");
            this.gameCanvas = new GameCanvas(this.gameContainer, image, c);
            this.gameCanvas.setLocation(0, 65);
            this.add(this.gameCanvas);
            this.gameControlPanel = new GameControlPanel(this.gameContainer, this.playerInfoPanel, 95, 80);
            this.gameControlPanel.setLocation(this.width - 95, 445);
            this.add(this.gameControlPanel);
        }
    }

    private void addMultiPlayerPanels(int mode) {
        if (this.gameContainer.golfGameFrame.syncIsValidSite.get()) {
            this.setVisible(false);
            int var2 = mode > 0 ? 265 : 400;
            this.chatPanel = new ChatPanel(this.gameContainer, this.width - 100 - 5 - var2 - 5, 80, mode);
            this.chatPanel.setLocation(0, 445);
            this.add(this.chatPanel);
            this.trackInfoPanel = new TrackInfoPanel(this.gameContainer, var2, 80, mode == 0);
            this.trackInfoPanel.setLocation(this.width - 100 - 5 - var2, 445);
            this.add(this.trackInfoPanel);
            this.setVisible(true);
        }
    }

    private void setState(int state) {
        if (state != this.state) {
            this.state = state;
            this.playerInfoPanel.setState(state);
            this.gameControlPanel.setState(state);
        }
    }

    private String encodeCoords(int x, int y, int shootingMode) {
        int var4 = x * 375 * 4 + y * 4 + shootingMode;

        String out = Integer.toString(var4, 36);
        while (out.length() < 4) {
            out = "0" + out;
        }

        return out;
    }
}
