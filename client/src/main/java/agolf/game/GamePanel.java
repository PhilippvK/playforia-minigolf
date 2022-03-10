package agolf.game;

import agolf.GameApplet;
import agolf.GameContainer;
import agolf.SynchronizedBool;
import org.moparforia.client.Launcher;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.util.StringTokenizer;

public class GamePanel extends Panel {

    private GameContainer gameContainer;
    private int width;
    private int height;
    private GamePlayerInfoPanel gamePlayerInfoPanel;
    private GameCanvas gameCanvas;
    private GameChatPanel gameChatPanel;
    private GameTrackInfoPanel gameTrackInfoPanel;
    private GameControlPanel gameControlPanel;
    protected int state;
    private int playerCount;
    private boolean isSinglePlayerGame;
    private boolean aBoolean363;
    private long aLong364;
    private Object anObject365;
    private boolean isWaitingForTurn;


    public GamePanel(GameContainer gameContainer, int width, int height, Image image) {
        this.gameContainer = gameContainer;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.create(image);
        this.aBoolean363 = true;
        this.anObject365 = new Object();
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
        var1.setColor(GameApplet.colourGameBackground);
        var1.fillRect(0, 0, this.width, this.height);
    }

    public void method333(String[] args) {
        if (args[1].equals("gameinfo")) {
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
            this.isSinglePlayerGame = args[14].equals("t");//todo unsure
            //int trackCategory = Launcher.isUsingCustomServer() ? Integer.parseInt(args[15]) : -1;
            byte mode = 0;
            if (this.gameContainer.synchronizedTrackTestMode.get()) {
                mode = 1;
            }

            if (this.playerCount > 1) {
                mode = 2;
            }

            this.addMultiPlayerPanels(mode);
            this.gamePlayerInfoPanel.method355(this.playerCount, trackCount, maxStrokes, strokeTimeout, trackScoring);
            this.gameTrackInfoPanel.setNumTracks(trackCount);
            this.gameControlPanel.setPlayerCount(this.playerCount);
            this.gameCanvas.method132(this.playerCount, waterEvent, collision);
            if (mode == 2) {
                String settings = "";
                if (passworded) {
                    settings = this.gameContainer.textManager.getGame("GameChat_GS_Password") + ", ";
                } else if (permission > 0) {
                    settings = this.gameContainer.textManager.getGame("GameChat_GS_" + (permission == 1 ? "Reg" : "Vip") + "Only") + ", ";
                }

                settings = settings + this.gameContainer.textManager.getGame("GameChat_GS_Players", this.playerCount);
                settings = settings + ", " + this.gameContainer.textManager.getGame("GameChat_GS_Tracks", trackCount);
                if (trackTypes > 0) {
                    settings = settings + " (" + this.gameContainer.textManager.getIfAvailable("LobbyReal_TrackTypes" + trackTypes, this.gameContainer.textManager.getGame("LobbyReal_TrackTypesTest")) + ")";
                }

                if (maxStrokes != 20) {
                    if (maxStrokes > 0) {
                        settings = settings + ", " + this.gameContainer.textManager.getGame("GameChat_GS_MaxStrokes", maxStrokes);
                    } else {
                        settings = settings + ", " + this.gameContainer.textManager.getGame("GameChat_GS_MaxStrokesUnlimited", maxStrokes);
                    }
                }

                if (strokeTimeout > 0) {
                    settings = settings + ", " + this.gameContainer.textManager.getGame("GameChat_GS_TimeLimit" + (strokeTimeout < 60 ? "Sec" : "Min"), strokeTimeout < 60 ? strokeTimeout : strokeTimeout / 60);
                }

                if (waterEvent == 1) {
                    settings = settings + ", " + this.gameContainer.textManager.getGame("GameChat_GS_WaterShore");
                }

                if (collision == 0) {
                    settings = settings + ", " + this.gameContainer.textManager.getGame("GameChat_GS_NoCollision");
                }

                if (trackScoring == 1) {
                    settings = settings + ", " + this.gameContainer.textManager.getGame("GameChat_GS_TrackScoring");
                }

                if (trackScoringEnd > 0) {
                    settings = settings + ", " + this.gameContainer.textManager.getGame("GameChat_GS_TrackScoringEnd" + trackScoringEnd);
                }

                /*if(trackCategory > -1) {
                    settings = settings + ", " + (trackCategory == 0 ? "official" : (trackCategory == 1 ? "custom" : "unknown")) + " maps";
                }*/

                this.gameChatPanel.addMessage(this.gameContainer.textManager.getGame("GameChat_GameName", gameName));
                this.gameChatPanel.addMessage(this.gameContainer.textManager.getGame("GameChat_GameSettings", settings));
            }

        }if (args[1].equals("scoringmulti")) {
            int len = args.length - 2;
            int[] var3 = new int[len];

            for (int trackCount = 0; trackCount < len; ++trackCount) {
                var3[trackCount] = Integer.parseInt(args[2 + trackCount]);
            }

            this.gamePlayerInfoPanel.method356(var3);
        } else if (args[1].equals("players")) {
            int len = (args.length - 2) / 3;
            int playerCountIndex = 2;

            for (int trackTypes = 0; trackTypes < len; ++trackTypes) {
                int playerCount = Integer.parseInt(args[playerCountIndex]);//todo lol why u inside the loop tho
                String clan = args[playerCountIndex + 2].equals("-") ? null : args[playerCountIndex + 2];
                this.gamePlayerInfoPanel.addPlayer(playerCount, args[playerCountIndex + 1], clan, false);
                this.gameChatPanel.setUserColour(args[playerCountIndex + 1], playerCount);
                playerCountIndex += 3;
            }

        }
        else if (args[1].equals("owninfo")) {
            int currentPlayerID = Integer.parseInt(args[2]);
            String currentPlayerClan = args[4].equals("-") ? null : args[4];
            this.gamePlayerInfoPanel.addPlayer(currentPlayerID, args[3], currentPlayerClan, true);
            this.gameChatPanel.setUserColour(args[3], currentPlayerID);
            this.aLong364 = System.currentTimeMillis();
        } else if (args[1].equals("join")) {
            int playerId = Integer.parseInt(args[2]);
            String playerClan = args[4].equals("-") ? null : args[4];
            this.gamePlayerInfoPanel.addPlayer(playerId, args[3], playerClan, false);
            this.gameChatPanel.setUserColour(args[3], playerId);
            if (this.playerCount != 2 || playerId != 1) {
                this.gameChatPanel.addMessage(playerClan != null ? this.gameContainer.textManager.getGame("GameChat_JoinClan", args[3], playerClan) : this.gameContainer.textManager.getGame("GameChat_Join", args[3]));
            }

        }
        else if (args[1].equals("part")) {
            int playerId = Integer.parseInt(args[2]);
            String playerName = this.gamePlayerInfoPanel.playerNames[playerId];
            boolean var23 = this.gamePlayerInfoPanel.method358(playerId, Integer.parseInt(args[3]));
            if (var23) {
                this.gameControlPanel.method329();
            }

            this.gameChatPanel.addMessage(this.gameContainer.textManager.getGame("GameChat_Part", playerName));
            this.gameChatPanel.removeUserColour(playerName);
            this.gameControlPanel.method330();
        } else if (args[1].equals("say")) {
            int playerId = Integer.parseInt(args[2]);
            this.gameChatPanel.addSay(playerId, this.gamePlayerInfoPanel.playerNames[playerId], args[3], false);
        } else if (args[1].equals("cr")) {
            StringTokenizer var19 = new StringTokenizer(args[2], ",");
            int playerTypes = var19.countTokens();
            int[][] var25 = new int[5][playerTypes];

            for (int var1 = 0; var1 < 5; ++var1) {
                for (int startIndex = 0; startIndex < playerTypes; ++startIndex) {
                    var25[var1][startIndex] = Integer.parseInt(var19.nextToken());
                }

                if (var1 < 4) {
                    var19 = new StringTokenizer(args[3 + var1], ",");
                }
            }

            this.gamePlayerInfoPanel.method375(var25);
        } else if (args[1].equals("start")) {
            if (this.playerCount > 1) {
                if (this.aBoolean363) {
                    if (System.currentTimeMillis() > this.aLong364 + 1000L) {
                        this.gameContainer.soundManager.playNotify();
                        //this.requestFocus();//todo this is annoying as fuck
                    }

                    this.gameContainer.gameApplet.showPlayerList(this.gamePlayerInfoPanel.method374());
                } else {
                    this.gameContainer.gameApplet.removePlayerListWinnders();
                }
            }

            this.aBoolean363 = false;
            this.gameCanvas.createMap(16777216);
            this.gamePlayerInfoPanel.method359();
            this.gameTrackInfoPanel.resetCurrentTrack();
            this.setState(1);
        }
        else if (args[1].equals("starttrack")) {
            /**game
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
                char var12 = args[commandIndex].charAt(0);
                if (var12 == 'A') {
                    author = args[commandIndex].substring(2);
                }

                if (var12 == 'N') {
                    name = args[commandIndex].substring(2);
                }

                if (var12 == 'T') {
                    data = args[commandIndex].substring(2);
                }

                if (var12 == 'T' && args[commandIndex].charAt(2) == '!') {// a track we already played?
                    args[commandIndex] = "T " + this.gameContainer.trackCollection.getTrack(author, name);
                    hasPlayed = true;
                }

                fullInstruction = fullInstruction + args[commandIndex];
                if (commandIndex < argsLen - 1) {
                    fullInstruction = fullInstruction + '\n';
                }
            }

            if(Launcher.debug())
                System.out.println("FULL: " + fullInstruction);

            if (!hasPlayed) {
                this.gameContainer.trackCollection.addTrack(author, name, data);
            }

            this.gameCanvas.parseMap(fullInstruction, args[trackTestMode ? 3 : 2], Integer.parseInt(args[trackTestMode ? 4 : 3]));

                      /* trackinformatino
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
            String[] trackInformation = this.gameCanvas.generateTrackInformation();
            int[][] trackStats = this.gameCanvas.generateTrackStatistics();

            this.gameTrackInfoPanel.parseTrackInfoStats(trackInformation[0], trackInformation[1], trackStats[0], trackStats[1], trackInformation[2], trackInformation[3], trackTestMode1, trackTestMode2, this.gameCanvas.method134());

            int numberOfPlayers = this.gamePlayerInfoPanel.method360();
            if (numberOfPlayers > 1) {
                this.gameChatPanel.addMessage(gameContainer.textManager.getGame("GameChat_ScoreMultiNotify", numberOfPlayers));
            }

            this.gameControlPanel.displaySkipButton(); // checks if you can skip on first shot
            if (this.gameContainer.synchronizedTrackTestMode.get()) {
                this.gameChatPanel.printSpecialSettingstoTextArea(this.gameCanvas.getTrackComment(), this.gameCanvas.getTrackSettings(), this.gameCanvas.method120());
            }

        } else if (args[1].equals("startturn")) {
            this.isWaitingForTurn = false;
            int playerId = Integer.parseInt(args[2]);

            boolean canPlay = this.gamePlayerInfoPanel.canShoot(playerId);
            //canPlay = true;
            this.gameCanvas.startTurn(playerId, canPlay, !this.gameChatPanel.haveFocus());

            if (!this.isSinglePlayerGame) {
                int trackCount = this.gamePlayerInfoPanel.method377();
                if (trackCount >= 10 || trackCount >= this.gameTrackInfoPanel.method385()) {
                    this.gameControlPanel.showSkipButton();
                }
            }
        } else if (args[1].equals("beginstroke")) {
            int playerId = Integer.parseInt(args[2]);
            this.gamePlayerInfoPanel.method363(playerId, false);
            this.gameCanvas.decodeCoords(playerId, false, args[3]);

        } else if (args[1].equals("changescore")) {
            int numScores = args.length - 3;
            int[] trackScores = new int[numScores];

            for (int trackCount = 0; trackCount < numScores; ++trackCount) {
                trackScores[trackCount] = Integer.parseInt(args[3 + trackCount]);
            }

            this.gamePlayerInfoPanel.setScores(Integer.parseInt(args[2]), trackScores);
        }
        else if (args[1].equals("voteskip")) {
            this.gamePlayerInfoPanel.voteSkip(Integer.parseInt(args[2]));
        } else if (args[1].equals("resetvoteskip")) {
            this.gamePlayerInfoPanel.voteSkipReset();
            if (!this.gameCanvas.getSynchronizedBool(this.gamePlayerInfoPanel.currentPlayerId)) {
                this.gameControlPanel.showSkipButton();
            }

        }
        else if (args[1].equals("rfng")) {
            this.gamePlayerInfoPanel.readyForNewGame(Integer.parseInt(args[2]));
        }
        else if(args[1].equals("end")) {
            this.gameCanvas.endGame();
            int len = args.length - 2;
            if (len > 0) {
                int[] gameOutcome = new int[len];
                boolean[] isWinner = new boolean[len];

                for (int var1 = 0; var1 < len; ++var1) {
                    gameOutcome[var1] = Integer.parseInt(args[2 + var1]);
                    isWinner[var1] = gameOutcome[var1] == 1;
                }

                this.gamePlayerInfoPanel.setGameOutcome(gameOutcome);
                this.gameContainer.gameApplet.showPlayerListWinners(isWinner);
            } else {
                this.gamePlayerInfoPanel.setGameOutcome((int[]) null);
            }

            this.setState(2);// game state?
            if (this.isSinglePlayerGame) {
                this.gameContainer.lobbyPanel.requestTrackSetList();
            }

            this.gameContainer.gameApplet.gameFinished(this.playerCount > 1);
        }
    }

    protected void sendChatMessage(String var1) {
        String var2 = "say\t" + var1;
//        this.gameContainer.connection.writeData("game\t" + var2);
        this.gameChatPanel.addSay(this.gamePlayerInfoPanel.currentPlayerId, this.gamePlayerInfoPanel.playerNames[this.gamePlayerInfoPanel.currentPlayerId], var1, true);
    }

    protected void setBeginStroke(int playerId, int x, int y, int keycount) {
        this.gameTrackInfoPanel.method384();
        this.gamePlayerInfoPanel.method363(playerId, false);
        String data = "beginstroke\t" + this.encodeCoords(x, y, keycount);
//        this.gameContainer.connection.writeData("game\t" + data);
    }

    protected void method336() {
        String var1 = this.gameCanvas.method142();
        if (var1 != null) {
            this.gamePlayerInfoPanel.method363(0, false);
            String var2 = "beginstroke\t" + var1;
//            this.gameContainer.connection.writeData("game\t" + var2);
            this.gameCanvas.decodeCoords(0, true, var1);
        }
    }

    protected boolean isValidPlayerID(int var1) {
        return this.gamePlayerInfoPanel.method361(var1);
    }

    protected void sendEndStroke(int playerid, SynchronizedBool[] settings, int var3) {
        String data = "endstroke\t" + playerid + "\t";

        for (int index = 0; index < settings.length; ++index) {
            if (var3 == index && !settings[index].get()) {
                this.gamePlayerInfoPanel.method363(index, true);
                data = data + "p";
            } else {
                data = data + (settings[index].get() ? "t" : "f");
            }
        }

//        this.gameContainer.connection.writeData("game\t" + data);
    }

    protected boolean method339(boolean var1) {
        if (this.state == 1) {
            if (!var1) {
                this.gamePlayerInfoPanel.method366();
                if (this.gamePlayerInfoPanel.method376() && this.gameCanvas.method137()) {
                    this.gameCanvas.restartGame();
                }

//                this.gameContainer.connection.writeData("game\tvoteskip");
                return true;
            }

            if (this.gameCanvas.method137()) {
                this.gameCanvas.restartGame();
                this.gamePlayerInfoPanel.method372();
//                this.gameContainer.connection.writeData("game\tskip");
                return true;
            }
        }

        return false;
    }

    protected void hideSkipButton() {
        this.gameControlPanel.hideSkipButton();
    }

    protected void requestNewGame() {
//        this.gameContainer.connection.writeData("game\tnewgame");
    }

    protected void method342() {
        this.gameCanvas.restartGame();
        this.gamePlayerInfoPanel.stop();
        this.gameContainer.gameApplet.setGameState(0);
//        this.gameContainer.connection.writeData("game\tback");
        this.gameContainer.gameApplet.removePlayerList();
    }

    protected void rateTrack(int track, int rating) {
        String var3 = "rate\t" + track + "\t" + rating;
//        this.gameContainer.connection.writeData("game\t" + var3);
    }

    protected void respondNewGame(int track, boolean accept) {// why track
        String var3 = "rejectaccept\t" + track + "\t" + (accept ? "t" : "f");
//        this.gameContainer.connection.writeData("game\t" + var3);
    }

    protected void method345(int var1) {
        String var2 = "backtoprivate\t" + var1;
//        this.gameContainer.connection.writeData("game\t" + var2);
    }

    protected boolean maxFps() {
        return this.gameControlPanel.maxFps();
    }

    protected String[] getPlayerInfo(int playerId) {
        return this.gamePlayerInfoPanel.getPlayerInfo(playerId);
    }

    protected void method348(int var1) {
        this.gameCanvas.method139(var1);
    }

    public void broadcastMessage(String message) {
        this.gameChatPanel.addBroadcastMessage(message);
    }

    protected boolean canStroke(boolean stopInfoPanel) {
        synchronized (anObject365) {
            if (this.isWaitingForTurn) {
                boolean var3 = false;
                return var3;
            }

            this.isWaitingForTurn = true;
        }

        if (stopInfoPanel) {// ???????????????????????????????????
            this.gamePlayerInfoPanel.stop();
        } else {
            this.gameCanvas.stop();
        }

        return true;
    }

    private void create(Image image) {
        if (this.gameContainer.gameApplet.syncIsValidSite.get()) {
            this.setLayout((LayoutManager) null);
            this.gamePlayerInfoPanel = new GamePlayerInfoPanel(this.gameContainer, 735, 60);
            this.gamePlayerInfoPanel.setLocation(0, 0);
            this.add(this.gamePlayerInfoPanel);
            this.gameCanvas = new GameCanvas(this.gameContainer, image);
            this.gameCanvas.setLocation(0, 65);
            this.add(this.gameCanvas);
            this.gameControlPanel = new GameControlPanel(this.gameContainer, this.gamePlayerInfoPanel, 95, 80);
            this.gameControlPanel.setLocation(this.width - 95, 445);
            this.add(this.gameControlPanel);
        }
    }

    private void addMultiPlayerPanels(int mode) {
        if (this.gameContainer.gameApplet.syncIsValidSite.get()) {
            this.setVisible(false);
            int var2 = mode > 0 ? 265 : 400;
            this.gameChatPanel = new GameChatPanel(this.gameContainer, this.width - 100 - 5 - var2 - 5, 80, mode);
            this.gameChatPanel.setLocation(0, 445);
            this.add(this.gameChatPanel);
            this.gameTrackInfoPanel = new GameTrackInfoPanel(this.gameContainer, var2, 80, mode == 0);
            this.gameTrackInfoPanel.setLocation(this.width - 100 - 5 - var2, 445);
            this.add(this.gameTrackInfoPanel);
            this.setVisible(true);
        }
    }

    private void setState(int var1) {
        if (var1 != this.state) {
            this.state = var1;
            this.gamePlayerInfoPanel.method371(var1);
            this.gameControlPanel.method327(var1);
        }
    }

    private String encodeCoords(int x, int y, int mod) {
        int var4 = x * 375 * 4 + y * 4 + mod;//mod.. or something, possible values 0..3

        String out;
        for (out = Integer.toString(var4, 36); out.length() < 4; out = "0" + out) {
            ;
        }

        return out;
    }

    public boolean isCheating() {
        return gameCanvas.allowCheating;
    }
}
