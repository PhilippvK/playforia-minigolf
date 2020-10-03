package agolf.game;

import agolf.GameApplet;
import agolf.GameContainer;
import agolf.SynchronizedInteger;
import com.aapeli.colorgui.Choicer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class GamePlayerInfoPanel extends Panel implements ItemListener, MouseListener {

    private static final Font fontDialog12 = new Font("Dialog", 0, 12);
    private static final Font fontDialog12b = new Font("Dialog", 1, 12);
    private static final Font fontDialog10 = new Font("Dialog", 0, 10);
    private static final Color aColor371 = new Color(224, 255, 224);
    private static final Color aColor372 = new Color(128, 208, 128);
    private static final Color aColor373 = new Color(224, 0, 0);
    private static final Color aColor374 = new Color(0, 128, 0);
    /*private static final Color[][] aColorArrayArray375 = new Color[][]{
            {new Color(0, 0, 255), new Color(128, 128, 255)}, {new Color(255, 0, 0), new Color(255, 128, 128)},
            {new Color(128, 128, 0), new Color(128, 128, 64)}, {new Color(0, 160, 0), new Color(64, 160, 64)}
    };*/
    private static final Color[][] aColorArrayArray375 = new Color[][]{
            {new Color(0, 0, 255), new Color(128, 128, 255)},
            {new Color(255, 0, 0), new Color(255, 128, 128)},
            {new Color(255, 255, 0), new Color(255, 255, 180)},
            {new Color(0, 180, 0), new Color(128, 180, 128)},
            {new Color(255, 0, 255), new Color(255, 128, 255)},
            {new Color(0, 255, 255), new Color(180, 0, 255)},
            {new Color(180, 0, 255), new Color(180, 128, 255)},
            {new Color(255, 180, 0), new Color(255, 180, 128)},
            {new Color(0, 255, 0), new Color(128, 255, 128)},
            {new Color(128, 128, 128), new Color(180, 180, 180)}
    };


    private static int anInt376;
    private GameContainer gameContainer;
    private int width;
    private int height;
    private boolean aBoolean380;
    private int playerCount;
    private int anInt382;
    private int anInt383;
    private int strokeTimeout;
    private int anInt385;
    private int anInt386;
    private int playerID;
    private int[] gameOutcome;
    protected int currentPlayerId;
    protected String[] playerNames;
    protected String[] playerClans;
    private SynchronizedInteger[][] trackStrokes;
    private SynchronizedInteger[] playersId;
    private int[] anIntArray394;
    private boolean[] playerVotedToSkip;
    private boolean[] aBooleanArray396;
    private int[] anIntArray397;
    private int[][] anIntArrayArray398;
    private Choicer aChoicer399;
    private Image image;
    private Graphics graphics;
    private GamePlayerInfoPanelThread panelThread;
    private int currentTimeForShot;


    protected GamePlayerInfoPanel(GameContainer gameContainer, int width, int height) {
        this.gameContainer = gameContainer;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.setLayout((LayoutManager) null);
        this.currentTimeForShot = -1;
        this.aBoolean380 = false;
        this.anIntArray397 = null;
        this.anIntArrayArray398 = null;
    }

    public void addNotify() {
        super.addNotify();
        this.repaint();
    }

    public void paint(Graphics g) {
        this.update(g);
    }

    public void update(Graphics g) {
        if (this.image == null) {
            this.image = this.createImage(this.width, this.height);
            this.graphics = this.image.getGraphics();
        }

        this.graphics.setColor(GameApplet.colourGameBackground);
        this.graphics.fillRect(0, 0, this.width, this.height);
        //System.out.println("yyeeep " + aBoolean380);
        if (this.aBoolean380) {
            int[] var2 = null;
            int var4;
            if (this.anIntArrayArray398 != null && anInt376 > 0) {
                var2 = this.anIntArrayArray398[anInt376 - 1];
                this.graphics.setFont(fontDialog12);
                this.graphics.setColor(aColor372);
                this.graphics.drawString(this.gameContainer.textManager.getGame("GamePlayerInfo_CompareResultNick"), 20, 20);
                int var3 = 0;

                for (var4 = 0; var4 <= this.anInt386 && var4 < this.anInt382; ++var4) {
                    this.graphics.drawString(var2[var4] > 0 ? String.valueOf(var2[var4]) : "?", 130 + var4 * 20, 20);
                    var3 += var2[var4];
                }

                if (var3 > 0) {
                    this.graphics.drawString("= " + var3, 130 + this.anInt382 * 20 + 15, 20);
                }
            }

            int[] var12 = this.method379();
            var4 = (10+1 - this.playerCount) * 13;
            if (this.anInt386 >= 0 && this.anInt386 < this.anInt382) {
                this.graphics.setColor(aColor371);
                this.graphics.fillRect(130 + this.anInt386 * 20 - 5 + 1, var4 - 13, 19, this.playerCount * 15 + 2);
                this.graphics.fillRect(130 + this.anInt386 * 20 - 5, var4 - 13 + 1, 21, this.playerCount * 15 + 2 - 2);
            }

            for (int var5 = 0; var5 < this.playerCount; ++var5) {
                Font var6 = this.currentPlayerId == var5 ? fontDialog12b : fontDialog12;
                Color var7 = aColorArrayArray375[var5][this.anIntArray394[var5] == 0 ? 0 : 1];
                this.graphics.setFont(var6);
                this.graphics.setColor(var7);
                if (this.playerCount > 1) {
                    this.graphics.drawString(var5 + 1 + ".", 2, var4);
                }

                if (this.playerNames[var5] != null) {
                    this.graphics.drawString(this.playerNames[var5], 20, var4);
                }

                for (int var8 = 0; var8 < this.anInt382; ++var8) {
                    if (var8 <= this.anInt386) {
                        int var9 = this.trackStrokes[var5][var8].get();
                        if (var2 != null) {
                            if (var8 < this.anInt386 && var9 < var2[var8]) {
                                this.graphics.setColor(aColor374);
                            }

                            if (var9 > var2[var8] && var2[var8] > 0) {
                                this.graphics.setColor(aColor373);
                            }
                        }

                        this.graphics.drawString(var9 >= 0 ? String.valueOf(var9) : this.gameContainer.textManager.getGame("GamePlayerInfo_Skipped"), 130 + var8 * 20, var4);
                        this.graphics.setColor(var7);
                    } else if (this.anIntArray397[var8] == 1) {
                        this.graphics.drawString("-", 130 + var8 * 20 + 5, var4);
                    } else {
                        this.graphics.setFont(fontDialog10);
                        this.graphics.setColor(aColorArrayArray375[var5][1]);
                        this.graphics.drawString("(*" + this.anIntArray397[var8] + ")", 130 + var8 * 20, var4 - 1);
                        this.graphics.setFont(var6);
                        this.graphics.setColor(var7);
                    }
                }

                this.graphics.drawString("= " + this.playersId[var5].get(), 130 + this.anInt382 * 20 + 15, var4);
                String var10;
                if (var12 != null && this.anIntArray394[var5] == 0) {
                    var10 = null;
                    if (var12[var5] == 0) {
                        if (this.gameOutcome == null) {
                            var10 = this.gameContainer.textManager.getGame("GamePlayerInfo_Leader");
                        }
                    } else {
                        var10 = this.gameContainer.textManager.getGame("GamePlayerInfo_AfterLeader", (var12[var5] > 0 ? "+" : "") + var12[var5]);
                    }

                    if (var10 != null) {
                        this.graphics.drawString(var10, 130 + this.anInt382 * 20 + 15 + 40, var4);
                    }
                }

                var10 = null;
                String var11 = null;
                if (this.playerNames[var5] == null) {
                    var10 = "GamePlayerInfo_WaitingPlayer";
                }

                if (this.playerCount > 1 && this.playerID == var5) {
                    if (this.playerID == this.currentPlayerId) {
                        var10 = "GamePlayerInfo_OwnTurn";
                        if (this.panelThread != null && this.currentTimeForShot > 0 && (this.strokeTimeout > 0 || this.strokeTimeout == 0 && this.currentTimeForShot <= 30)) {
                            var11 = " (" + this.gameContainer.textManager.getTime((long) this.currentTimeForShot) + ")";
                        }
                    } else {
                        var10 = "GamePlayerInfo_PlayerTurn";
                    }
                }

                if (this.gameOutcome != null) {
                    if (this.gameOutcome[var5] == 1) {
                        var10 = "GamePlayerInfo_Winner";
                    } else if (this.gameOutcome[var5] == 0) {
                        var10 = "GamePlayerInfo_Draw";
                    }
                }

                if (var10 != null) {
                    this.graphics.drawString(this.gameContainer.textManager.getGame(var10) + (var11 != null ? var11 : ""), 130 + this.anInt382 * 20 + 15 + 40 + 40, var4);
                }

                var10 = null;
                if (this.playerVotedToSkip[var5]) {
                    var10 = "GamePlayerInfo_VoteSkipTrack";
                }

                if (this.aBooleanArray396[var5]) {
                    var10 = "GamePlayerInfo_ReadyForNewGame";
                }

                if (this.anIntArray394[var5] == 5) {
                    var10 = "GamePlayerInfo_Quit_ConnectionProblem";
                }

                if (this.anIntArray394[var5] == 4) {
                    var10 = "GamePlayerInfo_Quit_Part";
                }

                if (var10 != null) {
                    this.graphics.drawString(this.gameContainer.textManager.getGame(var10), 130 + this.anInt382 * 20 + 15 + 40 + 40 + 100, var4);
                }

                var4 += 15;
            }
        }

        g.drawImage(this.image, 0, 0, this);
    }

    public void itemStateChanged(ItemEvent var1) {
        anInt376 = this.aChoicer399.getSelectedIndex();
        this.repaint();
    }

    public void mouseEntered(MouseEvent var1) {
    }

    public void mouseExited(MouseEvent var1) {
    }

    public void mousePressed(MouseEvent var1) {
        if (var1.getClickCount() == 2) {
            int var2 = var1.getY();
            int var3 = (5 - this.playerCount) * 13;

            for (int var4 = 0; var4 < this.playerCount; ++var4) {
                if (this.playerNames[var4] != null && var2 >= var3 - 12 && var2 < var3 + 3) {
                    this.gameContainer.gameApplet.showPlayerCard(this.playerNames[var4]);
                }

                var3 += 15;
            }

        }
    }

    public void mouseReleased(MouseEvent var1) {
    }

    public void mouseClicked(MouseEvent var1) {
    }

    protected void method355(int playerCount, int trackCount, int startidx, int strokeTimeout, int scoring) {
        this.playerCount = playerCount;
        this.anInt382 = trackCount;
        this.anInt383 = startidx;
        this.strokeTimeout = strokeTimeout;
        this.anInt385 = scoring;
        this.playerNames = new String[playerCount];
        this.playerClans = new String[playerCount];
        this.trackStrokes = new SynchronizedInteger[playerCount][trackCount];
        this.playersId = new SynchronizedInteger[playerCount];

        int var7;
        for (int var6 = 0; var6 < playerCount; ++var6) {
            for (var7 = 0; var7 < trackCount; ++var7) {
                this.trackStrokes[var6][var7] = new SynchronizedInteger();
            }

            this.playersId[var6] = new SynchronizedInteger();
        }

        this.anIntArray394 = new int[playerCount];
        this.playerVotedToSkip = new boolean[playerCount];
        this.aBooleanArray396 = new boolean[playerCount];

        for (var7 = 0; var7 < playerCount; ++var7) {
            this.playerNames[var7] = this.playerClans[var7] = null;
            this.anIntArray394[var7] = 0;
        }

        this.anIntArray397 = new int[trackCount];

        for (int var8 = 0; var8 < trackCount; ++var8) {
            this.anIntArray397[var8] = 1;
        }

        this.currentPlayerId = -1;
        this.method359();
        this.aBoolean380 = true;
        this.repaint();
    }

    protected void method356(int[] var1) {
        this.anIntArray397 = var1;
        this.repaint();
    }

    protected void addPlayer(int playerID, String name, String clan, boolean isLocalPlayer) {
        this.playerNames[playerID] = name;
        this.playerClans[playerID] = clan;
        if (isLocalPlayer) {
            this.currentPlayerId = playerID;
        }

        this.repaint();
        this.removeMouseListener(this);
        this.addMouseListener(this);
    }

    protected boolean method358(int playerId, int var2) {
        if (var2 == 6) {
            this.playerNames[playerId] = null;
        } else {
            this.anIntArray394[playerId] = var2;
            if (this.gameContainer.gamePanel.state == 2) {
                this.gameContainer.gamePanel.state = 3;
                this.repaint();
                return true;
            }
        }

        this.repaint();
        return false;
    }

    protected void method359() {
        for (int var1 = 0; var1 < this.playerCount; ++var1) {
            for (int var2 = 0; var2 < this.anInt382; ++var2) {
                this.trackStrokes[var1][var2].set(0);
            }

            this.playersId[var1].set(0);
            this.playerVotedToSkip[var1] = this.aBooleanArray396[var1] = false;
        }

        this.anInt386 = this.playerID = -1;
        this.gameOutcome = null;
        this.repaint();
    }

    protected int method360() {
        for (int var1 = 0; var1 < this.playerCount; ++var1) {
            this.playerVotedToSkip[var1] = false;
        }

        ++this.anInt386;
        this.repaint();
        return this.anIntArray397[this.anInt386];
    }

    protected boolean method361(int var1) {
        if (this.anInt383 == 0) {
            return false;
        } else {
            int var2 = this.trackStrokes[var1][this.anInt386].get();
            if (this.anInt385 == 0) {
                var2 /= this.anIntArray397[this.anInt386];
            }

            return var2 >= this.anInt383;
        }
    }

    protected boolean canShoot(int playerNumber) {
        this.playerID = playerNumber;
        int timeout = this.strokeTimeout > 0 ? this.strokeTimeout : 180;

        if (this.playerCount > 1 && playerNumber == this.currentPlayerId && timeout > 0) {
            this.stop();
            this.currentTimeForShot = timeout;
            this.panelThread = new GamePlayerInfoPanelThread(this);
        }

        this.repaint();
        return playerNumber == this.currentPlayerId;
    }

    protected void method363(int var1, boolean var2) {
        if (this.anInt385 == 0) {
            int var3 = !var2 ? this.anIntArray397[this.anInt386] : 1;
            this.trackStrokes[var1][this.anInt386].get_upd(var3);
            this.playersId[var1].get_upd(var3);
        } else {
            this.trackStrokes[var1][this.anInt386].get_upd();
        }

        this.repaint();
    }

    protected void setScores(int trackId, int[] scores) {
        for (int player = 0; player < this.playerCount; ++player) {
            this.trackStrokes[player][trackId].set(scores[player]);
            this.playersId[player].set(0);

            for (int track = 0; track <= trackId; ++track) {
                int strokes = this.trackStrokes[player][track].get();
                if (strokes >= 0) {
                    this.playersId[player].get_upd(strokes);
                }
            }
        }

        this.repaint();
    }

    protected void setGameOutcome(int[] var1) {
        this.gameOutcome = var1;
        this.anInt386 = this.anInt382;
        this.playerID = -1;
        this.repaint();
    }

    protected void method366() {
        this.voteSkip(this.currentPlayerId);
    }

    protected void voteSkip(int playerId) {
        this.playerVotedToSkip[playerId] = true;
        this.repaint();
    }

    protected void voteSkipReset() {
        for (int var1 = 0; var1 < this.playerCount; ++var1) {
            this.playerVotedToSkip[var1] = false;
        }

        this.repaint();
    }

    protected void readyForNewGameLocal() {
        this.readyForNewGame(this.currentPlayerId);
    }

    protected void readyForNewGame(int var1) {
        this.aBooleanArray396[var1] = true;
        this.repaint();
    }

    protected void method371(int var1) {
        if (var1 == 2) {
            for (int var2 = 0; var2 < this.playerCount && var1 == 2; ++var2) {
                if (this.anIntArray394[var2] != 0) {
                    var1 = 3;
                    this.gameContainer.gamePanel.state = 3;
                }
            }
        }

    }

    protected void method372() {
        if (this.anInt385 == 0) {
            this.playersId[this.playerID].get_upd(-this.trackStrokes[this.playerID][this.anInt386].get());
        }

        this.trackStrokes[this.playerID][this.anInt386].set(-1);
        this.repaint();
    }

    protected String[] getPlayerInfo(int playerId) {
        return new String[]{this.playerNames[playerId], this.playerClans[playerId]};
    }

    protected String[] method374() {
        return this.playerNames;
    }

    protected void method375(int[][] var1) {
        this.anIntArrayArray398 = var1;
        this.aChoicer399 = new Choicer();
        this.aChoicer399.addItem(this.gameContainer.textManager.getGame("GamePlayerInfo_CompareResultNone"));
        this.aChoicer399.addItem(this.gameContainer.textManager.getGame("GamePlayerInfo_CompareResultOwn", this.playerNames[0]));
        this.aChoicer399.addItem(this.gameContainer.textManager.getGame("GamePlayerInfo_CompareResultTop100Average"));
        this.aChoicer399.addItem(this.gameContainer.textManager.getGame("GamePlayerInfo_CompareResultBestOfDay"));
        this.aChoicer399.addItem(this.gameContainer.textManager.getGame("GamePlayerInfo_CompareResultBestOfAllTime"));
        this.aChoicer399.addItem(this.gameContainer.textManager.getGame("GamePlayerInfo_CompareResultIdeal"));
        this.aChoicer399.select(anInt376);
        this.aChoicer399.setBounds(555, 5, 170, 20);
        this.aChoicer399.addItemListener(this);
        this.setVisible(false);
        this.add(this.aChoicer399);
        this.setVisible(true);
        this.repaint();
    }

    protected boolean method376() {
        for (int var1 = 0; var1 < this.playerCount; ++var1) {
            if (this.anIntArray394[var1] == 0 && !this.playerVotedToSkip[var1]) {
                return false;
            }
        }

        return true;
    }

    protected int method377() {
        return this.playerCount > 1 ? -1 : this.trackStrokes[0][this.anInt386].get();
    }

    protected void stop() {
        if (this.panelThread != null) {
            this.panelThread.stopRunning();
            this.panelThread = null;
        }

    }

    private int[] method379() {
        int var1 = this.anInt385 == 0 ? Integer.MAX_VALUE : Integer.MIN_VALUE;

        int var3;
        for (int var2 = 0; var2 < this.playerCount; ++var2) {
            if (this.anIntArray394[var2] == 0) {
                var3 = this.playersId[var2].get();
                if (this.anInt385 == 0 && var3 < var1) {
                    var1 = var3;
                }

                if (this.anInt385 == 1 && var3 > var1) {
                    var1 = var3;
                }
            }
        }

        int[] var4 = new int[this.playerCount];
        int var5 = 0;

        for (int var6 = 0; var6 < this.playerCount; ++var6) {
            var3 = this.playersId[var6].get();
            if (var3 == var1) {
                var4[var6] = 0;
                ++var5;
            } else {
                var4[var6] = var3 - var1;
            }
        }

        if (var5 == this.playerCount) {
            return null;
        } else {
            return var4;
        }
    }

    protected boolean run() {
        --this.currentTimeForShot;
        this.repaint();
        if (this.currentTimeForShot <= 0) {
            this.gameContainer.gamePanel.canStroke(false);
            this.panelThread = null;
            return false;
        } else {
            return true;
        }
    }
}
