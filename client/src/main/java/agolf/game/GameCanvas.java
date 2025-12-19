package agolf.game;

import agolf.GameContainer;
import agolf.Seed;
import agolf.SynchronizedBool;
import com.aapeli.client.Parameters;
import com.aapeli.client.StringDraw;
import com.aapeli.tools.Tools;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class GameCanvas extends GameBackgroundCanvas
        implements Runnable, MouseMotionListener, MouseListener, KeyListener {

    private static final double magicOffset = Math.sqrt(2.0D) / 2.0D;
    private static final int diagOffset = (int) (6.0D * magicOffset + 0.5D);
    private static final Cursor cursorDefault = new Cursor(Cursor.DEFAULT_CURSOR);
    private Cursor cursorCrosshair = new Cursor(Cursor.CROSSHAIR_CURSOR);
    private static final Color colourAimLine = new Color(128, 0, 32);
    private static final Font gameFont = new Font("Dialog", Font.PLAIN, 10);
    private static final Color blackColour = Color.black;
    private static final Color whiteColour = Color.white;
    private static final Color backgroundColour = new Color(19, 167, 19);
    private int gameState;
    private Image[] ballSprites;
    private int playerCount;
    private int onShoreSetting;
    private int collisionMode;
    private int currentPlayerID;
    private int mouseX;
    private int mouseY;
    private int shootingMode;
    private int anInt2816;
    private double startPositionX;
    private double startPositionY;
    private double bounciness;
    private double somethingSpeedThing;
    private double[] resetPositionX;
    private double[] resetPositionY;
    private List<double[]>[] teleportStarts;
    private List<double[]>[] teleportExists;
    private short[][][] magnetMap;
    private double[] playerX;
    private double[] playerY;
    private double[] speedX;
    private double[] speedY;
    private boolean[] aBooleanArray2830;
    private SynchronizedBool[] onHoleSync; // not sure
    private boolean isLocalPlayer;
    private int playerNamesDisplayMode; // 0 == Hide names, 1 == Show initials, 2 == Show names, 3 ==
    // Name + clan
    private boolean[] aBooleanArray2834;
    private String aString2835;
    private Seed rngSeed;
    private static int[] anIntArray2837 = new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
    private static int anInt2838 = 2;
    private int anInt2839;
    private Image anImage2840;
    private Graphics graphics;
    private Thread aThread2842;
    private boolean aBoolean2843;
    private boolean norandom;

    // aimbot stuff
    private final boolean allowCheating = false;
    private double hackedX = 0;
    private double hackedY = 0;
    private boolean isCheating = false;

    protected GameCanvas(GameContainer gameContainer, Image image, Cursor c) {
        super(gameContainer, image);
        this.ballSprites = gameContainer.spriteManager.getBalls();
        this.playerCount = this.currentPlayerID = this.mouseX = this.mouseY = -1;
        this.playerNamesDisplayMode = 0;
        this.gameState = 0;
        this.anInt2839 = anInt2838;
        this.norandom = Parameters.getBooleanValue(gameContainer.params.getParameter("norandom"));
        // TODO: would be cool if user can set their own cursor
        this.cursorCrosshair = c;
    }

    @Override
    public void update(Graphics g) {
        if (this.anImage2840 == null) {
            this.anImage2840 = this.createImage(735, 375);
            this.graphics = this.anImage2840.getGraphics();
        }

        super.update(this.graphics);
        if (this.gameState == 1 && this.mouseX > -1 && this.mouseY > -1) {
            double[] power = this.getStrokePower(this.currentPlayerID, this.mouseX, this.mouseY);

            int x1 = (int) (this.playerX[this.currentPlayerID] + 0.5D);
            int y1 = (int) (this.playerY[this.currentPlayerID] + 0.5D);
            int x2 = (int) (this.playerX[this.currentPlayerID] + power[0] * 200.0D / 6.5D + 0.5D);
            int y2 = (int) (this.playerY[this.currentPlayerID] + power[1] * 200.0D / 6.5D + 0.5D);
            this.graphics.setColor(colourAimLine);
            if (this.shootingMode == 0) {
                this.graphics.drawLine(x1, y1, x2, y2);
            } else {
                int deltaX = x2 - x1;
                int deltaY = y2 - y1;
                this.drawDashedLine(this.graphics, x1, y1, deltaX, deltaY);
                if (this.shootingMode == 1) {
                    deltaX = -deltaX;
                    deltaY = -deltaY;
                }

                int temp;
                if (this.shootingMode == 2) {
                    temp = deltaX;
                    deltaX = deltaY;
                    deltaY = -temp;
                }

                if (this.shootingMode == 3) {
                    temp = deltaX;
                    deltaX = -deltaY;
                    deltaY = temp;
                }

                this.graphics.drawLine(x1, y1, x1 + deltaX, y1 + deltaY);
            }
        }

        if (this.currentPlayerID > -1) {
            this.graphics.setFont(gameFont);
            this.graphics.setColor(blackColour);

            for (int player = 0; player < this.playerCount; ++player) {
                if (this.aBooleanArray2830[player] && player != this.currentPlayerID) {
                    this.drawPlayer(this.graphics, player, this.onHoleSync[player].get() ? 2.1666666666666665D : 0.0D);
                }
            }

            this.graphics.setColor(whiteColour);
            this.drawPlayer(
                    this.graphics,
                    this.currentPlayerID,
                    this.onHoleSync[this.currentPlayerID].get() ? 2.1666666666666665D : 0.0D);
        }

        if (isCheating) {
            // TODO could add ImageFilter here to distinguish the hacked ball
            // from real ball.

            // Draw the player ball into
            int x = (int) (hackedX - 6.5D + 0.5D);
            int y = (int) (hackedY - 6.5D + 0.5D);
            int ballSpriteOffset = 0;
            if (super.gameContainer.graphicsQualityIndex == 3) {
                ballSpriteOffset = (x / 5 + y / 5) % 2 * 4;
            }
            graphics.drawImage(
                    this.ballSprites[this.currentPlayerID + ballSpriteOffset],
                    x,
                    y,
                    x + 13,
                    y + 13,
                    0,
                    0,
                    13,
                    13,
                    this);
        }

        g.drawImage(this.anImage2840, 0, 0, this);
    }

    @Override
    public void run() {
        Image ballImage = this.createImage(735, 375);
        Image var2 = super.image;
        Graphics ballGraphic = ballImage.getGraphics();
        Graphics canvas = this.getGraphics();
        ballGraphic.drawImage(var2, 0, 0, this);
        canvas.drawImage(ballImage, 0, 0, this);
        int loopStuckCounter = 0;
        int[] magnetStuckCounter = new int[this.playerCount];
        int[] downhillStuckCounter = new int[this.playerCount];
        double[] tempCoordX = new double[this.playerCount];
        double[] tempCoordY = new double[this.playerCount];
        double[] var10 = new double[this.playerCount];
        double[] tempCoord2X = new double[this.playerCount];
        double[] tempCoord2Y = new double[this.playerCount];
        double[] tempCoord3X = new double[this.playerCount];
        double[] tempCoord3Y = new double[this.playerCount];
        boolean[] onHole = new boolean[this.playerCount];
        boolean[] onLiquidOrSwamp = new boolean[this.playerCount];
        boolean[] teleported = new boolean[this.playerCount];
        int[] spinningStuckCounter = new int[this.playerCount];

        for (int player = 0; player < this.playerCount; ++player) {
            magnetStuckCounter[player] = downhillStuckCounter[player] = 0;
            tempCoordX[player] = tempCoord2X[player] = this.playerX[player];
            tempCoordY[player] = tempCoord2Y[player] = this.playerY[player];
            onHole[player] = onLiquidOrSwamp[player] = false;
            var10[player] = this.onHoleSync[player].get() ? 2.1666666666666665D : 0.0D;
            teleported[player] = false;
            spinningStuckCounter[player] = 0;
        }

        boolean shouldSpinAroundHole = false;
        boolean onLiquid = false;
        boolean var22 = false;
        boolean var23 = super.gameContainer.synchronizedTrackTestMode.get();
        if (var23) {
            var22 = super.gameContainer.gamePanel.maxFps();
        }

        int var24 = -1;
        byte topleft = 0;
        byte left = 0;
        byte bottomleft = 0;
        byte bottom = 0;
        byte bottomright = 0;
        byte right = 0;
        byte topright = 0;
        byte top = 0;
        byte center = 0;
        int y = 0;
        int x = 0;
        double speed = 0.0D;
        this.bounciness = this.somethingSpeedThing = 1.0D;
        int var38 = 0;

        do {
            long time = System.currentTimeMillis();

            for (int i = 0; i < this.playerCount; ++i) {
                tempCoord3X[i] = this.playerX[i];
                tempCoord3Y[i] = this.playerY[i];
            }

            for (int var42 = 0; var42 < this.anInt2839; ++var42) {
                var24 = 0;

                for (int i = 0; i < this.playerCount; ++i) {
                    if (this.aBooleanArray2830[i] && !this.onHoleSync[i].get()) {
                        for (int j = 0; j < 10; ++j) {

                            // this moves player
                            this.playerX[i] += this.speedX[i] * 0.1D;
                            this.playerY[i] += this.speedY[i] * 0.1D;

                            // check if player is going off the map
                            if (this.playerX[i] < 6.6D) {
                                this.playerX[i] = 6.6D;
                            }

                            if (this.playerX[i] >= 727.9D) {
                                this.playerX[i] = 727.9D;
                            }

                            if (this.playerY[i] < 6.6D) {
                                this.playerY[i] = 6.6D;
                            }

                            if (this.playerY[i] >= 367.9D) {
                                this.playerY[i] = 367.9D;
                            }

                            // checks player vs player collision
                            int anotherPlayer;
                            if (this.collisionMode == 1 && !onHole[i] && !onLiquidOrSwamp[i]) {
                                for (anotherPlayer = 0; anotherPlayer < this.playerCount; ++anotherPlayer) {
                                    if (i != anotherPlayer
                                            && this.aBooleanArray2830[anotherPlayer]
                                            && !this.onHoleSync[anotherPlayer].get()
                                            && !onHole[anotherPlayer]
                                            && !onLiquidOrSwamp[anotherPlayer]
                                            && this.handlePlayerCollisions(i, anotherPlayer)) {
                                        // collision is calculated in another function this just
                                        // makes it less effective
                                        this.speedX[i] *= 0.75D;
                                        this.speedY[i] *= 0.75D;
                                        this.speedX[anotherPlayer] *= 0.75D;
                                        this.speedY[anotherPlayer] *= 0.75D;
                                        var24 = 0; // players moved so we reset this to make sure
                                        // they move
                                    }
                                }
                            }

                            x = (int) (this.playerX[i] + 0.5D);
                            y = (int) (this.playerY[i] + 0.5D);
                            center = super.collisionMap[x][y];
                            top = super.collisionMap[x][y - 6];
                            topright = super.collisionMap[x + diagOffset][y - diagOffset];
                            right = super.collisionMap[x + 6][y];
                            bottomright = super.collisionMap[x + diagOffset][y + diagOffset];
                            bottom = super.collisionMap[x][y + 6];
                            bottomleft = super.collisionMap[x - diagOffset][y + diagOffset];
                            left = super.collisionMap[x - 6][y];
                            topleft = super.collisionMap[x - diagOffset][y - diagOffset];
                            if (center != 12 && center != 13) {
                                onLiquid = center == 14 || center == 15;
                            } else {
                                this.speedX[i] *= 0.97D;
                                this.speedY[i] *= 0.97D;
                                onLiquid = true;
                            }

                            int teleCounter = 0;
                            // 32 Blue Teleport Start
                            // 34 Red Teleport Start
                            // 36 Yellow Teleport Start
                            // 38 Green Teleport Start
                            for (int teleportId = 32; teleportId <= 38; teleportId += 2) {
                                if (top == teleportId
                                        || topright == teleportId
                                        || right == teleportId
                                        || bottomright == teleportId
                                        || bottom == teleportId
                                        || bottomleft == teleportId
                                        || left == teleportId
                                        || topleft == teleportId) {
                                    ++teleCounter;
                                    if (!teleported[i]) {
                                        this.handleTeleport((teleportId - 32) / 2, i, x, y);
                                        teleported[i] = true;
                                    }
                                }
                            }

                            if (teleCounter == 0) {
                                teleported[i] = false;
                            }
                            // 28 Mine
                            // 30 Big Mine
                            if (center == 28 || center == 30) {
                                this.handleMines(center == 30, i, x, y, ballGraphic, canvas);
                            }

                            this.handleWallCollision(
                                    i,
                                    top,
                                    topright,
                                    right,
                                    bottomright,
                                    bottom,
                                    bottomleft,
                                    left,
                                    topleft,
                                    x,
                                    y,
                                    ballGraphic,
                                    canvas);
                        }

                        boolean isDownhill = this.handleDownhill(i, center);
                        boolean isAffectedByMagnet = false;
                        if (this.magnetMap != null && !onLiquid && !onHole[i] && !onLiquidOrSwamp[i]) {
                            isAffectedByMagnet = this.handleMagnetForce(i, x, y);
                        }

                        shouldSpinAroundHole = false;
                        double holeSpeed;
                        // 25 hole
                        if (center == 25
                                || super.collisionMap[x][y - 1] == 25
                                || super.collisionMap[x + 1][y] == 25
                                || super.collisionMap[x][y + 1] == 25
                                || super.collisionMap[x - 1][y] == 25) {
                            holeSpeed = center == 25 ? 1.0D : 0.5D;
                            shouldSpinAroundHole = true;
                            int holeCounter = 0;
                            if (top == 25) {
                                ++holeCounter;
                            } else {
                                this.speedY[i] += holeSpeed * 0.03D;
                            }

                            if (topright == 25) {
                                ++holeCounter;
                            } else {
                                this.speedY[i] += holeSpeed * 0.03D * magicOffset;
                                this.speedX[i] -= holeSpeed * 0.03D * magicOffset;
                            }

                            if (right == 25) {
                                ++holeCounter;
                            } else {
                                this.speedX[i] -= holeSpeed * 0.03D;
                            }

                            if (bottomright == 25) {
                                ++holeCounter;
                            } else {
                                this.speedY[i] -= holeSpeed * 0.03D * magicOffset;
                                this.speedX[i] -= holeSpeed * 0.03D * magicOffset;
                            }

                            if (bottom == 25) {
                                ++holeCounter;
                            } else {
                                this.speedY[i] -= holeSpeed * 0.03D;
                            }

                            if (bottomleft == 25) {
                                ++holeCounter;
                            } else {
                                this.speedY[i] -= holeSpeed * 0.03D * magicOffset;
                                this.speedX[i] += holeSpeed * 0.03D * magicOffset;
                            }

                            if (left == 25) {
                                ++holeCounter;
                            } else {
                                this.speedX[i] += holeSpeed * 0.03D;
                            }

                            if (topleft == 25) {
                                ++holeCounter;
                            } else {
                                this.speedY[i] += holeSpeed * 0.03D * magicOffset;
                                this.speedX[i] += holeSpeed * 0.03D * magicOffset;
                            }

                            if (holeCounter >= 7) {
                                shouldSpinAroundHole = false;
                                onHole[i] = true;
                                this.speedX[i] = this.speedY[i] = 0.0D;
                            }
                        }

                        if (shouldSpinAroundHole) {
                            ++spinningStuckCounter[i];
                            if (spinningStuckCounter[i] > 500) {
                                shouldSpinAroundHole = false;
                            }
                        } else {
                            spinningStuckCounter[i] = 0;
                        }

                        if (!isDownhill
                                && !isAffectedByMagnet
                                && !shouldSpinAroundHole
                                && !onHole[i]
                                && !onLiquidOrSwamp[i]
                                && !onLiquid) {
                            tempCoord2X[i] = this.playerX[i];
                            tempCoord2Y[i] = this.playerY[i];
                        }

                        speed = Math.sqrt(this.speedX[i] * this.speedX[i] + this.speedY[i] * this.speedY[i]);
                        if (speed > 0.0D) {
                            double var52 = this.calculateFriction(center, speed);
                            this.speedX[i] *= var52;
                            this.speedY[i] *= var52;
                            speed *= var52;
                            if (speed > 7.0D) {
                                holeSpeed = 7.0D / speed;
                                this.speedX[i] *= holeSpeed;
                                this.speedY[i] *= holeSpeed;
                                speed *= holeSpeed;
                            }
                        }

                        if (loopStuckCounter > 4000) {
                            this.bounciness = 0.0D;
                            if (loopStuckCounter > 7000) {
                                isAffectedByMagnet = false;
                                isDownhill = false;
                                speed = 0.0D;
                            }
                        }

                        if (isDownhill && speed < 0.22499999999999998D) {
                            ++downhillStuckCounter[i];
                            if (downhillStuckCounter[i] >= 250) {
                                isDownhill = false;
                            }
                        } else {
                            downhillStuckCounter[i] = 0;
                        }

                        if (isAffectedByMagnet && speed < 0.22499999999999998D) {
                            ++magnetStuckCounter[i];
                            if (magnetStuckCounter[i] >= 150) {
                                isAffectedByMagnet = false;
                            }
                        } else {
                            magnetStuckCounter[i] = 0;
                        }

                        if (speed < 0.075D
                                && !isDownhill
                                && !isAffectedByMagnet
                                && !shouldSpinAroundHole
                                && !onHole[i]
                                && !onLiquidOrSwamp[i]) {
                            this.speedX[i] = this.speedY[i] = 0.0D;
                            if (center != 12 && center != 14 && center != 13 && center != 15) {
                                ++var24;
                            } else {
                                onLiquidOrSwamp[i] = true;
                            }
                        }

                        if (onHole[i] || onLiquidOrSwamp[i]) {
                            var10[i] += 0.1D;
                            if (onHole[i] && var10[i] > 2.1666666666666665D || onLiquidOrSwamp[i] && var10[i] > 6.0D) {
                                // 25 hole
                                if (center == 25) {
                                    this.onHoleSync[i].set(true);
                                    if (this.isLocalPlayer && this.playerCount > 1) {
                                        super.gameContainer.gamePanel.hideSkipButton();
                                    }
                                } else {
                                    // water 12
                                    // water swamp 14
                                    if (center == 12 || center == 14) {
                                        this.playerX[i] = this.onShoreSetting == 0 ? tempCoordX[i] : tempCoord2X[i];
                                        this.playerY[i] = this.onShoreSetting == 0 ? tempCoordY[i] : tempCoord2Y[i];
                                    }

                                    // 13 acid
                                    // 15 acid swamp
                                    if (center == 13 || center == 15) {
                                        this.resetPosition(i, false);
                                    }

                                    var10[i] = 0.0D;
                                }

                                onHole[i] = onLiquidOrSwamp[i] = false;
                                ++var24;
                            }
                        }
                    } else {
                        ++var24;
                    }
                }

                ++loopStuckCounter;
                if (var24 >= this.playerCount) {
                    var42 = this.anInt2839;
                }
            }

            for (int i = 0; i < this.playerCount; ++i) {
                if (this.aBooleanArray2830[i]) {
                    int x1 = (int) (tempCoord3X[i] - 6.5D + 0.5D);
                    int y1 = (int) (tempCoord3Y[i] - 6.5D + 0.5D);
                    int x2 = x1 + 13;
                    int y2 = y1 + 13;
                    ballGraphic.drawImage(var2, x1, y1, x2, y2, x1, y1, x2, y2, this);

                    for (int j = 0; j < this.playerCount; ++j) {
                        if (this.aBooleanArray2830[j] && j != this.currentPlayerID) {
                            this.drawPlayer(ballGraphic, j, var10[j]);
                        }
                    }

                    this.drawPlayer(ballGraphic, this.currentPlayerID, var10[this.currentPlayerID]);
                    if (this.playerX[i] < tempCoord3X[i]) {
                        x1 = (int) (this.playerX[i] - 6.5D + 0.5D);
                    }

                    if (this.playerX[i] > tempCoord3X[i]) {
                        x2 = (int) (this.playerX[i] - 6.5D + 0.5D) + 13;
                    }

                    if (this.playerY[i] < tempCoord3Y[i]) {
                        y1 = (int) (this.playerY[i] - 6.5D + 0.5D);
                    }

                    if (this.playerY[i] > tempCoord3Y[i]) {
                        y2 = (int) (this.playerY[i] - 6.5D + 0.5D) + 13;
                    }

                    canvas.drawImage(ballImage, x1, y1, x2, y2, x1, y1, x2, y2, this);
                }
            }

            time = System.currentTimeMillis() - time; // time to render
            long var58 = (long) (6 * this.anInt2839) - time; // fps cap ?
            if (var23) {
                if (var22) {
                    var58 = 0L;
                } else if (loopStuckCounter % 100 == 0) {
                    var22 = super.gameContainer.gamePanel.maxFps();
                }
            }

            Tools.sleep(var58);
            var38 = (int) ((long) var38 + var58);
        } while (var24 < this.playerCount && !this.aBoolean2843);

        if (this.aBoolean2843) {
            this.aThread2842 = null;
        } else {
            this.method164(var38);
            super.gameContainer.gamePanel.sendEndStroke(this.currentPlayerID, this.onHoleSync, this.anInt2816);
            if (this.anInt2816 >= 0) {
                this.onHoleSync[this.anInt2816].set(true);
            }

            this.aThread2842 = null;
            this.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent var1) {
        this.mouseX = var1.getX();
        this.mouseY = var1.getY();

        if (isCheating) {
            int x = this.mouseX;
            int y = this.mouseY;
            double subtractionX = this.playerX[this.currentPlayerID] - (double) x;
            double subtractionY = this.playerY[this.currentPlayerID] - (double) y;
            if (Math.sqrt(subtractionX * subtractionX + subtractionY * subtractionY) >= 6.5D) {
                this.doHackedStroke(this.currentPlayerID, true, x, y, this.shootingMode);
                this.repaint();
            }
        }

        this.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent event) {}

    @Override
    public void mouseEntered(MouseEvent event) {
        this.mouseMoved(event);
    }

    @Override
    public void mouseExited(MouseEvent event) {
        this.mouseX = this.mouseY = -1;
        this.repaint();
    }

    @Override
    public synchronized void mousePressed(MouseEvent event) {
        if (this.gameState == 1) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                int x = event.getX();
                int y = event.getY();
                this.mouseX = x;
                this.mouseY = y;
                double subtractionX = this.playerX[this.currentPlayerID] - (double) x;
                double subtractionY = this.playerY[this.currentPlayerID] - (double) y;
                // checks if mouse is on own ball
                if (Math.sqrt(subtractionX * subtractionX + subtractionY * subtractionY) >= 6.5D) {
                    this.removeMouseMotionListener(this);
                    this.removeMouseListener(this);
                    this.removeKeyListener(this);
                    this.setCursor(cursorDefault);
                    if (super.gameContainer.gamePanel.tryStroke(false)) {
                        super.gameContainer.gamePanel.setBeginStroke(this.currentPlayerID, x, y, this.shootingMode);
                        // this.doHackedStroke(this.currentPlayerID, true, x, y, this.keyCountMod4);
                        this.doStroke(this.currentPlayerID, true, x, y, this.shootingMode);
                    }
                }
            } else {
                this.shootingMode = (this.shootingMode + 1) % 4;
                this.repaint();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (this.gameState == 1) {
            event.consume();
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {}

    @Override
    public synchronized void keyPressed(KeyEvent event) {
        if (allowCheating) {
            // code for the aimbot.
            if (event.getKeyCode() == KeyEvent.VK_C) {
                isCheating = !isCheating;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {}

    @Override
    public void keyTyped(KeyEvent var1) {}

    protected void init(int playerCount, int waterMode, int collisionMode) {
        this.playerCount = playerCount;
        this.onShoreSetting = waterMode;
        this.collisionMode = collisionMode;
        this.playerX = new double[playerCount];
        this.playerY = new double[playerCount];
        this.speedX = new double[playerCount];
        this.speedY = new double[playerCount];
        this.onHoleSync = new SynchronizedBool[playerCount];

        for (int i = 0; i < playerCount; ++i) {
            this.onHoleSync[i] = new SynchronizedBool();
        }

        this.aBooleanArray2830 = new boolean[playerCount];
        this.aBooleanArray2834 = new boolean[playerCount];
        this.playerNamesDisplayMode = playerCount <= 2 ? 0 : 3;
    }

    @Override
    protected void createMap(int var1) {
        super.createMap(var1);
        this.currentPlayerID = this.mouseX = this.mouseY = -1;
        this.gameState = 0;
        this.repaint();
    }

    protected boolean init(String commandLines, String playerStatuses, int gameId) {
        boolean parseSuccessful = super.parseMapCommands(commandLines);

        this.aString2835 = null;
        StringTokenizer commandTokens = new StringTokenizer(commandLines, "\n");

        while (commandTokens.hasMoreTokens()) {
            String currentCommand = commandTokens.nextToken();
            char commandType = currentCommand.charAt(0);

            if (commandType == 'B' || commandType == 'L') {

                int recordHolderName = currentCommand.indexOf(',');
                int recordTimestamp = currentCommand.indexOf(',', recordHolderName + 1);
                int var10 = currentCommand.indexOf(',', recordTimestamp + 1);
                currentCommand = currentCommand.substring(var10 + 1);
                int var11 = currentCommand.indexOf('=');
                if (var11 > -1) {
                    gameId = Integer.parseInt(currentCommand.substring(0, var11));
                    this.aString2835 = currentCommand.substring(var11 + 1);
                }
            }
        }

        List<double[]> startPositions = new ArrayList<>();
        this.resetPositionX = new double[4];
        this.resetPositionY = new double[4];
        this.teleportExists = new ArrayList[4];
        this.teleportStarts = new ArrayList[4];
        List<int[]> magnets = new ArrayList<>();

        for (int i = 0; i < 4; ++i) {
            this.resetPositionX[i] = this.resetPositionY[i] = -1.0D;
            this.teleportExists[i] = new ArrayList<>();
            this.teleportStarts[i] = new ArrayList<>();
        }

        // Iterates over the 49*25 map
        for (int y = 0; y < 25; ++y) {
            for (int x = 0; x < 49; ++x) {
                if (super.trackTiles[x][y] / 16777216 == 2) {
                    int tile = super.trackTiles[x][y] / 65536 % 256 + 24;
                    double screenX = (double) (x * 15) + 7.5D;
                    double screenY = (double) (y * 15) + 7.5D;
                    // 24 Start Position Common
                    if (tile == 24) {
                        double[] startPosition = new double[] {screenX, screenY};
                        startPositions.add(startPosition);
                    }
                    // 48 Start Position Blue
                    // 49 Start Position Red
                    // 50 Start Positiono Yellow
                    // 51 Start Position Green
                    if (tile >= 48 && tile <= 51) {
                        this.resetPositionX[tile - 48] = screenX;
                        this.resetPositionY[tile - 48] = screenY;
                    }

                    int teleportIndex;
                    // 33 Teleport Exit Blue
                    // 35 Teleport Exit Red
                    // 37 Teleport Exit Yellow
                    // 39 Teleport Exit Green
                    if (tile == 33 || tile == 35 || tile == 37 || tile == 39) {
                        teleportIndex = (tile - 33) / 2;
                        double[] teleporter = new double[] {screenX, screenY};
                        this.teleportExists[teleportIndex].add(teleporter);
                    }

                    // 33 Teleport Start Blue
                    // 35 Teleport Start Red
                    // 37 Teleport Start Yellow
                    // 39 Teleport Start Green
                    if (tile == 32 || tile == 34 || tile == 36 || tile == 38) {
                        teleportIndex = (tile - 32) / 2;
                        double[] teleporter = new double[] {screenX, screenY};
                        this.teleportStarts[teleportIndex].add(teleporter);
                    }

                    // 44 magnet attract
                    // 45 magnet repel
                    if (tile == 44 || tile == 45) {
                        int[] magnet = new int[] {(int) (screenX + 0.5D), (int) (screenY + 0.5D), tile};
                        magnets.add(magnet);
                    }
                }
            }
        }

        int startPositionsCount = startPositions.size();
        if (startPositionsCount == 0) {
            this.startPositionX = this.startPositionY = -1.0D;
        } else {
            double[] startPosition = startPositions.get(gameId % startPositionsCount);
            this.startPositionX = startPosition[0];
            this.startPositionY = startPosition[1];
        }

        int magnetVecLen = magnets.size();
        if (magnetVecLen == 0) {
            this.magnetMap = null;
        } else {
            this.magnetMap = new short[147][75][2];

            // magnet map is 5times smaller than real one
            for (int magnetLoopY = 2; magnetLoopY < 375; magnetLoopY += 5) {
                for (int magnetLoopX = 2; magnetLoopX < 735; magnetLoopX += 5) {
                    double forceTempY = 0.0D;
                    double forceTempX = 0.0D;

                    for (int magnetIndex = 0; magnetIndex < magnetVecLen; ++magnetIndex) {
                        // [ x, y, blockid ]
                        int[] magnet = magnets.get(magnetIndex);
                        double forceTemp2X = magnet[0] - magnetLoopX;
                        double forcetemp2Y = magnet[1] - magnetLoopY;
                        double force = Math.sqrt(forceTemp2X * forceTemp2X + forcetemp2Y * forcetemp2Y);
                        if (force <= 127.0D) {
                            double var35 = Math.abs(forceTemp2X) / force;
                            force = 127.0D - force;
                            forceTemp2X = (forceTemp2X < 0.0D ? -1.0D : 1.0D) * force * var35;
                            forcetemp2Y = (forcetemp2Y < 0.0D ? -1.0D : 1.0D) * force * (1.0D - var35);
                            // 45 Magnet Repel
                            if (magnet[2] == 45) {
                                forceTemp2X = -forceTemp2X;
                                forcetemp2Y = -forcetemp2Y;
                            }

                            forceTempX += forceTemp2X;
                            forceTempY += forcetemp2Y;
                        }
                    }

                    int forceX = (int) forceTempX;
                    int forceY = (int) forceTempY;
                    // clamp value to what short can hold
                    if (forceX < -0x7ff) {
                        forceX = -0x7ff;
                    }

                    if (forceX > 0x7ff) {
                        forceX = 0x7ff;
                    }

                    if (forceY < -0x7ff) {
                        forceY = -0x7ff;
                    }

                    if (forceY > 0x7ff) {
                        forceY = 0x7ff;
                    }

                    this.magnetMap[magnetLoopX / 5][magnetLoopY / 5][0] = (short) forceX;
                    this.magnetMap[magnetLoopX / 5][magnetLoopY / 5][1] = (short) forceY;
                }
            }
        }

        for (int i = 0; i < this.playerCount; ++i) {
            this.aBooleanArray2834[i] = true;
            this.resetPosition(i, true);
            this.onHoleSync[i].set(false);
            this.aBooleanArray2830[i] = playerStatuses.charAt(i) == 't';
        }

        this.rngSeed = new Seed(gameId);
        this.repaint();
        return parseSuccessful;
    }

    protected boolean method134() {
        return this.aString2835 != null;
    }

    protected void startTurn(int playerId, boolean canLocalPlayerPlay, boolean requestFocus) {
        this.currentPlayerID = playerId;
        this.aBooleanArray2834[playerId] = true;
        this.mouseX = this.mouseY = -1;
        this.shootingMode = 0;
        if (canLocalPlayerPlay) {
            this.setStrokeListeners(requestFocus);
            this.gameState = 1;
        } else {
            this.gameState = 0;
        }

        this.repaint();
    }

    protected void decodeCoords(int playerId, boolean isLocalPlayer, String encoded) {
        int var4 = Integer.parseInt(encoded, 36);
        int x = var4 / 1500;
        int y = var4 % 1500 / 4;
        int shootingMode = var4 % 4;
        this.doStroke(playerId, isLocalPlayer, x, y, shootingMode);
    }

    protected boolean method137() {
        return this.gameState == 1;
    }

    protected void endGame() {
        this.removeMouseMotionListener(this);
        this.removeMouseListener(this);
        this.removeKeyListener(this);
        this.setCursor(cursorDefault);
        this.gameState = 0;
        this.repaint();
    }

    protected void setPlayerNamesDisplayMode(int mode) {
        this.playerNamesDisplayMode = mode;
        this.repaint();
    }

    protected boolean getSynchronizedBool(int index) {
        return this.onHoleSync[index].get();
    }

    protected void restartGame() {
        this.removeMouseMotionListener(this);
        this.removeMouseListener(this);
        this.removeKeyListener(this);
        this.setCursor(cursorDefault);
        if (this.aThread2842 != null) {
            this.aBoolean2843 = true;

            while (this.aThread2842 != null) {
                Tools.sleep(100L);
            }
        }

        this.gameState = 0;
        this.repaint();
    }

    protected String method142() {
        if (this.gameState != 1) {
            return null;
        } else {
            try {
                String var1 = this.aString2835.substring(0, 4);
                this.aString2835 = this.aString2835.substring(4);
                return var1;
            } catch (StringIndexOutOfBoundsException var2) {
                return null;
            }
        }
    }

    protected void doZeroLengthStroke() {
        this.removeMouseMotionListener(this);
        this.removeMouseListener(this);
        this.removeKeyListener(this);
        this.setCursor(cursorDefault);
        int x = (int) this.playerX[this.currentPlayerID];
        int y = (int) this.playerY[this.currentPlayerID];
        super.gameContainer.gamePanel.setBeginStroke(this.currentPlayerID, x, y, 0);
        this.doStroke(this.currentPlayerID, true, x, y, 0);
    }

    private void doStroke(int playerId, boolean isLocalPlayer, int mouseX, int mouseY, int shootingMode) {
        this.anInt2816 = super.gameContainer.gamePanel.isValidPlayerID(playerId) ? playerId : -1;
        double[] power = this.getStrokePower(playerId, mouseX, mouseY);
        this.speedX[playerId] = power[0];
        this.speedY[playerId] = power[1];
        if (shootingMode == 1) {
            this.speedX[playerId] = -this.speedX[playerId];
            this.speedY[playerId] = -this.speedY[playerId];
        }

        double temp;
        if (shootingMode == 2) {
            temp = this.speedX[playerId];
            this.speedX[playerId] = this.speedY[playerId];
            this.speedY[playerId] = -temp;
        }

        if (shootingMode == 3) {
            temp = this.speedX[playerId];
            this.speedX[playerId] = -this.speedY[playerId];
            this.speedY[playerId] = temp;
        }

        temp = Math.sqrt(this.speedX[playerId] * this.speedX[playerId] + this.speedY[playerId] * this.speedY[playerId]);
        double speed = temp / 6.5D;
        speed *= speed;
        if (!this.norandom) {
            this.speedX[playerId] += speed * ((double) (this.rngSeed.next() % 50001) / 100000.0D - 0.25D);
            this.speedY[playerId] += speed * ((double) (this.rngSeed.next() % 50001) / 100000.0D - 0.25D);
        }
        this.isLocalPlayer = isLocalPlayer;
        this.gameState = 2;
        this.aBoolean2843 = false;

        this.aThread2842 = new Thread(this);
        this.aThread2842.start();
    }

    private void doHackedStroke(int playerId, boolean isLocalPlayer, int mouseX, int mouseY, int mod) {
        double[] temp_aDoubleArray2828 = Arrays.copyOf(speedX, speedX.length);
        double[] temp_aDoubleArray2829 = Arrays.copyOf(speedY, speedY.length);
        boolean temp_aBoolean2832 = this.isLocalPlayer;
        boolean temp_aBoolean2843 = this.aBoolean2843;
        Seed temp_aSeed_2836 = rngSeed.clone();
        // int temp_anInt2816 = super.gameContainer.gamePanel.isValidPlayerID(playerId)
        // ? playerId :
        // -1;
        int temp_anInt2816 = playerId;

        double[] var6 = getStrokePower(playerId, mouseX, mouseY);
        temp_aDoubleArray2828[playerId] = var6[0];
        temp_aDoubleArray2829[playerId] = var6[1];
        if (mod == 1) {
            temp_aDoubleArray2828[playerId] = -temp_aDoubleArray2828[playerId];
            temp_aDoubleArray2829[playerId] = -temp_aDoubleArray2829[playerId];
        }

        double var7;
        if (mod == 2) {
            var7 = temp_aDoubleArray2828[playerId];
            temp_aDoubleArray2828[playerId] = temp_aDoubleArray2829[playerId];
            temp_aDoubleArray2829[playerId] = -var7;
        }

        if (mod == 3) {
            var7 = temp_aDoubleArray2828[playerId];
            temp_aDoubleArray2828[playerId] = -temp_aDoubleArray2829[playerId];
            temp_aDoubleArray2829[playerId] = var7;
        }

        var7 = Math.sqrt(temp_aDoubleArray2828[playerId] * temp_aDoubleArray2828[playerId]
                + temp_aDoubleArray2829[playerId] * temp_aDoubleArray2829[playerId]);
        double var9 = var7 / 6.5D;
        var9 *= var9;
        temp_aDoubleArray2828[playerId] += var9 * ((double) (temp_aSeed_2836.next() % 50001) / 100000.0D - 0.25D);
        temp_aDoubleArray2829[playerId] += var9 * ((double) (temp_aSeed_2836.next() % 50001) / 100000.0D - 0.25D);
        temp_aBoolean2832 = isLocalPlayer;
        // this.gameState = 2;
        temp_aBoolean2843 = false;

        HackedShot hs = new HackedShot(
                playerCount,
                onShoreSetting,
                collisionMode,
                currentPlayerID,
                temp_anInt2816,
                startPositionX,
                startPositionY,
                bounciness,
                somethingSpeedThing,
                resetPositionX,
                resetPositionY,
                teleportStarts,
                teleportExists,
                magnetMap,
                playerX,
                playerY,
                temp_aDoubleArray2828,
                temp_aDoubleArray2829,
                aBooleanArray2830,
                onHoleSync,
                temp_aBoolean2832,
                aBooleanArray2834,
                temp_aSeed_2836,
                anInt2839,
                temp_aBoolean2843,
                super.collisionMap,
                super.trackTiles);
        Thread hack = new Thread(hs);
        hack.start();
        try {
            hack.join();
        } catch (Exception e) {

        }
        double[] coords = hs.getHackedCoordintes();
        hackedX = coords[0];
        hackedY = coords[1];
    }

    private void resetPosition(int playerId, boolean gameStart) {
        if (this.resetPositionX[playerId] >= 0.0D && this.resetPositionX[playerId] >= 0.0D) {
            this.playerX[playerId] = this.resetPositionX[playerId];
            this.playerY[playerId] = this.resetPositionY[playerId];
        } else if (this.startPositionX >= 0.0D && this.startPositionY >= 0.0D) {
            this.playerX[playerId] = this.startPositionX;
            this.playerY[playerId] = this.startPositionY;
            if (gameStart) {
                this.aBooleanArray2834[playerId] = false;
            }

        } else {
            this.playerX[playerId] = 367.5D;
            this.playerY[playerId] = 187.5D;
        }
    }

    private double[] getStrokePower(int playerId, int mouseX, int mouseY) {
        double subX = this.playerX[playerId] - (double) mouseX;
        double subY = this.playerY[playerId] - (double) mouseY;
        double sqrtXY = Math.sqrt(subX * subX + subY * subY);
        double var10 = (sqrtXY - 5.0D) / 30.0D;
        if (var10 < 0.075D) {
            var10 = 0.075D;
        }

        if (var10 > 6.5D) {
            var10 = 6.5D;
        }

        double var12 = var10 / sqrtXY;
        double[] power = new double[] {
            ((double) mouseX - this.playerX[playerId]) * var12, ((double) mouseY - this.playerY[playerId]) * var12
        };
        return power;
    }

    private boolean handlePlayerCollisions(int player1, int player2) {
        double x = this.playerX[player2] - this.playerX[player1];
        double y = this.playerY[player2] - this.playerY[player1];
        double distance = Math.sqrt(x * x + y * y);
        if (distance != 0.0D && distance <= 13.0D) {
            double forceX = x / distance;
            double forceY = y / distance;
            double p1Speed = this.speedX[player1] * forceX + this.speedY[player1] * forceY;
            double p2Speed = this.speedX[player2] * forceX + this.speedY[player2] * forceY;
            if (p1Speed - p2Speed <= 0.0D) {
                return false;
            } else {
                double var17 = -this.speedX[player1] * forceY + this.speedY[player1] * forceX;
                double var19 = -this.speedX[player2] * forceY + this.speedY[player2] * forceX;
                this.speedX[player1] = p2Speed * forceX - var17 * forceY;
                this.speedY[player1] = p2Speed * forceY + var17 * forceX;
                this.speedX[player2] = p1Speed * forceX - var19 * forceY;
                this.speedY[player2] = p1Speed * forceY + var19 * forceX;
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean handleDownhill(int var1, int var2) {
        if (var2 >= 4 && var2 <= 11) {
            if (var2 == 4) {
                this.speedY[var1] -= 0.025D;
            }

            if (var2 == 5) {
                this.speedY[var1] -= 0.025D * magicOffset;
                this.speedX[var1] += 0.025D * magicOffset;
            }

            if (var2 == 6) {
                this.speedX[var1] += 0.025D;
            }

            if (var2 == 7) {
                this.speedY[var1] += 0.025D * magicOffset;
                this.speedX[var1] += 0.025D * magicOffset;
            }

            if (var2 == 8) {
                this.speedY[var1] += 0.025D;
            }

            if (var2 == 9) {
                this.speedY[var1] += 0.025D * magicOffset;
                this.speedX[var1] -= 0.025D * magicOffset;
            }

            if (var2 == 10) {
                this.speedX[var1] -= 0.025D;
            }

            if (var2 == 11) {
                this.speedY[var1] -= 0.025D * magicOffset;
                this.speedX[var1] -= 0.025D * magicOffset;
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean handleMagnetForce(int playerId, int mapX, int mapY) {
        int magnetX = mapX / 5;
        int magnetY = mapY / 5;
        short forceX = this.magnetMap[magnetX][magnetY][0];
        short forceY = this.magnetMap[magnetX][magnetY][1];
        if (forceX == 0 && forceY == 0) {
            return false;
        } else {
            if (this.somethingSpeedThing > 0.0D) {
                this.somethingSpeedThing -= 1.0E-4D;
            }

            this.speedX[playerId] += this.somethingSpeedThing * (double) forceX * 5.0E-4D;
            this.speedY[playerId] += this.somethingSpeedThing * (double) forceY * 5.0E-4D;
            return true;
        }
    }

    private double calculateFriction(int tile, double speed) {
        double friction = this.getFriction(tile);
        double var6 = 0.75D * speed / 6.5D;
        double var8 = 1.0D - friction;
        return friction + var8 * var6;
    }

    private double getFriction(int var1) {
        return var1 != 0 && (var1 < 4 || var1 > 11) && var1 != 19 && var1 != 47
                ? (var1 == 1
                        ? 0.92D
                        : (var1 == 2
                                ? 0.8D
                                : (var1 != 3 && var1 != 32 && var1 != 34 && var1 != 36 && var1 != 38
                                        ? (var1 != 12 && var1 != 13
                                                ? (var1 != 14 && var1 != 15
                                                        ? (var1 >= 20 && var1 <= 23
                                                                ? 0.995D
                                                                : (var1 == 25
                                                                        ? 0.96D
                                                                        : (var1 != 28 && var1 != 30
                                                                                ? (var1 != 29 && var1 != 31
                                                                                        ? (var1 == 44 ? 0.9D : 1.0D)
                                                                                        : 0.9D)
                                                                                : 1.0D)))
                                                        : 0.95D)
                                                : 0.0D)
                                        : 0.9975D)))
                : 0.9935D;
    }

    private void handleWallCollision(
            int playerId,
            int top,
            int topright,
            int right,
            int bottomright,
            int bottom,
            int bottomleft,
            int left,
            int topleft,
            int x,
            int y,
            Graphics ball,
            Graphics canvas) {
        boolean topCollide = top >= 16 && top <= 23 && top != 19 || top == 27 || top >= 40 && top <= 43 || top == 46;
        boolean toprightCollide = topright >= 16 && topright <= 23 && topright != 19
                || topright == 27
                || topright >= 40 && topright <= 43
                || topright == 46;
        boolean rightCollide =
                right >= 16 && right <= 23 && right != 19 || right == 27 || right >= 40 && right <= 43 || right == 46;
        boolean var17 = bottomright >= 16 && bottomright <= 23 && bottomright != 19
                || bottomright == 27
                || bottomright >= 40 && bottomright <= 43
                || bottomright == 46;
        boolean bottomCollide = bottom >= 16 && bottom <= 23 && bottom != 19
                || bottom == 27
                || bottom >= 40 && bottom <= 43
                || bottom == 46;
        boolean bottomleftCollide = bottomleft >= 16 && bottomleft <= 23 && bottomleft != 19
                || bottomleft == 27
                || bottomleft >= 40 && bottomleft <= 43
                || bottomleft == 46;
        boolean leftcollide =
                left >= 16 && left <= 23 && left != 19 || left == 27 || left >= 40 && left <= 43 || left == 46;
        boolean topleftCollide = topleft >= 16 && topleft <= 23 && topleft != 19
                || topleft == 27
                || topleft >= 40 && topleft <= 43
                || topleft == 46;
        if (topCollide && top == 20) {
            topCollide = false;
        }

        if (topleftCollide && topleft == 20) {
            topleftCollide = false;
        }

        if (toprightCollide && topright == 20) {
            toprightCollide = false;
        }

        if (leftcollide && left == 20) {
            leftcollide = false;
        }

        if (rightCollide && right == 20) {
            rightCollide = false;
        }

        if (rightCollide && right == 21) {
            rightCollide = false;
        }

        if (toprightCollide && topright == 21) {
            toprightCollide = false;
        }

        if (var17 && bottomright == 21) {
            var17 = false;
        }

        if (topCollide && top == 21) {
            topCollide = false;
        }

        if (bottomCollide && bottom == 21) {
            bottomCollide = false;
        }

        if (bottomCollide && bottom == 22) {
            bottomCollide = false;
        }

        if (var17 && bottomright == 22) {
            var17 = false;
        }

        if (bottomleftCollide && bottomleft == 22) {
            bottomleftCollide = false;
        }

        if (rightCollide && right == 22) {
            rightCollide = false;
        }

        if (leftcollide && left == 22) {
            leftcollide = false;
        }

        if (leftcollide && left == 23) {
            leftcollide = false;
        }

        if (bottomleftCollide && bottomleft == 23) {
            bottomleftCollide = false;
        }

        if (topleftCollide && topleft == 23) {
            topleftCollide = false;
        }

        if (bottomCollide && bottom == 23) {
            bottomCollide = false;
        }

        if (topCollide && top == 23) {
            topCollide = false;
        }

        if (topCollide
                && toprightCollide
                && rightCollide
                && (top < 20 || top > 23)
                && (topright < 20 || topright > 23)
                && (right < 20 || right > 23)) {
            rightCollide = false;
            topCollide = false;
        }

        if (rightCollide
                && var17
                && bottomCollide
                && (right < 20 || right > 23)
                && (bottomright < 20 || bottomright > 23)
                && (bottom < 20 || bottom > 23)) {
            bottomCollide = false;
            rightCollide = false;
        }

        if (bottomCollide
                && bottomleftCollide
                && leftcollide
                && (bottom < 20 || bottom > 23)
                && (bottomleft < 20 || bottomleft > 23)
                && (left < 20 || left > 23)) {
            leftcollide = false;
            bottomCollide = false;
        }

        if (leftcollide
                && topleftCollide
                && topCollide
                && (left < 20 || left > 23)
                && (topleft < 20 || topleft > 23)
                && (top < 20 || top > 23)) {
            topCollide = false;
            leftcollide = false;
        }

        double speedEffect;
        if (!topCollide && !rightCollide && !bottomCollide && !leftcollide) {
            double temp;
            if (toprightCollide
                    && (this.speedX[playerId] > 0.0D && this.speedY[playerId] < 0.0D
                            || this.speedX[playerId] < 0.0D
                                    && this.speedY[playerId] < 0.0D
                                    && -this.speedY[playerId] > -this.speedX[playerId]
                            || this.speedX[playerId] > 0.0D
                                    && this.speedY[playerId] > 0.0D
                                    && this.speedX[playerId] > this.speedY[playerId])) {
                speedEffect =
                        this.getSpeedEffect(topright, playerId, x + diagOffset, y - diagOffset, ball, canvas, 1, -1);
                temp = this.speedX[playerId];
                this.speedX[playerId] = this.speedY[playerId] * speedEffect;
                this.speedY[playerId] = temp * speedEffect;
            }

            if (var17
                    && (this.speedX[playerId] > 0.0D && this.speedY[playerId] > 0.0D
                            || this.speedX[playerId] > 0.0D
                                    && this.speedY[playerId] < 0.0D
                                    && this.speedX[playerId] > -this.speedY[playerId]
                            || this.speedX[playerId] < 0.0D
                                    && this.speedY[playerId] > 0.0D
                                    && this.speedY[playerId] > -this.speedX[playerId])) {
                speedEffect =
                        this.getSpeedEffect(bottomright, playerId, x + diagOffset, y + diagOffset, ball, canvas, 1, 1);
                temp = this.speedX[playerId];
                this.speedX[playerId] = -this.speedY[playerId] * speedEffect;
                this.speedY[playerId] = -temp * speedEffect;
            }

            if (bottomleftCollide
                    && (this.speedX[playerId] < 0.0D && this.speedY[playerId] > 0.0D
                            || this.speedX[playerId] > 0.0D
                                    && this.speedY[playerId] > 0.0D
                                    && this.speedY[playerId] > this.speedX[playerId]
                            || this.speedX[playerId] < 0.0D
                                    && this.speedY[playerId] < 0.0D
                                    && -this.speedX[playerId] > -this.speedY[playerId])) {
                speedEffect =
                        this.getSpeedEffect(bottomleft, playerId, x - diagOffset, y + diagOffset, ball, canvas, -1, 1);
                temp = this.speedX[playerId];
                this.speedX[playerId] = this.speedY[playerId] * speedEffect;
                this.speedY[playerId] = temp * speedEffect;
            }

            if (topleftCollide
                    && (this.speedX[playerId] < 0.0D && this.speedY[playerId] < 0.0D
                            || this.speedX[playerId] < 0.0D
                                    && this.speedY[playerId] > 0.0D
                                    && -this.speedX[playerId] > this.speedY[playerId]
                            || this.speedX[playerId] > 0.0D
                                    && this.speedY[playerId] < 0.0D
                                    && -this.speedY[playerId] > this.speedX[playerId])) {
                speedEffect =
                        this.getSpeedEffect(topleft, playerId, x - diagOffset, y - diagOffset, ball, canvas, -1, -1);
                temp = this.speedX[playerId];
                this.speedX[playerId] = -this.speedY[playerId] * speedEffect;
                this.speedY[playerId] = -temp * speedEffect;
            }
        } else {
            if (topCollide && this.speedY[playerId] < 0.0D) {
                speedEffect = this.getSpeedEffect(top, playerId, x, y - 6, ball, canvas, 0, -1);
                this.speedX[playerId] *= speedEffect;
                this.speedY[playerId] *= -speedEffect;
            } else if (bottomCollide && this.speedY[playerId] > 0.0D) {
                speedEffect = this.getSpeedEffect(bottom, playerId, x, y + 6, ball, canvas, 0, 1);
                this.speedX[playerId] *= speedEffect;
                this.speedY[playerId] *= -speedEffect;
            }

            if (rightCollide && this.speedX[playerId] > 0.0D) {
                speedEffect = this.getSpeedEffect(right, playerId, x + 6, y, ball, canvas, 1, 0);
                this.speedX[playerId] *= -speedEffect;
                this.speedY[playerId] *= speedEffect;
                return;
            }

            if (leftcollide && this.speedX[playerId] < 0.0D) {
                speedEffect = this.getSpeedEffect(left, playerId, x - 6, y, ball, canvas, -1, 0);
                this.speedX[playerId] *= -speedEffect;
                this.speedY[playerId] *= speedEffect;
                return;
            }
        }
    }

    private double getSpeedEffect(
            int tileId, int playerId, int x, int y, Graphics ball, Graphics canvas, int offsetX, int offsetY) {
        // 16 Block
        if (tileId == 16) {
            return 0.81D;
            // 17 Sticky Block
        } else if (tileId == 17) {
            return 0.05D;
            // 18 Bouncy Block
        } else if (tileId == 18) {
            if (this.bounciness <= 0.0D) {
                return 0.84D;
            } else {
                this.bounciness -= 0.01D;
                double speed = Math.sqrt(
                        this.speedX[playerId] * this.speedX[playerId] + this.speedY[playerId] * this.speedY[playerId]);
                return this.bounciness * 6.5D / speed;
            }
            // 20 Oneway North
            // 21 Oneway East
            // 22 Oneway South
            // 23 Oneway West
        } else if (tileId != 20 && tileId != 21 && tileId != 22 && tileId != 23) {
            // 27 Moveable Block
            // 46 Sunkable Moveable Block
            if (tileId != 27 && tileId != 46) {
                // 40 Full Breakable Block
                // 41 Three Quater Breakable Block
                // 42 Half Breakable Block
                // 43 Quater Breakable Block
                if (tileId != 40 && tileId != 41 && tileId != 42 && tileId != 43) {
                    return 1.0D;
                } else {
                    this.handleBreakableBlock(x, y, ball, canvas);
                    return 0.9D;
                }
            } else {
                return this.handleMovableBlock(x, y, ball, canvas, offsetX, offsetY, tileId == 27) ? 0.325D : 0.8D;
            }
        } else {
            return 0.82D;
        }
    }

    private void handleTeleport(int teleportId, int playerId, int x, int y) {
        int exitLen = this.teleportExists[teleportId].size();
        int startLen;
        int random;
        double[] teleportPos;
        int var13;
        if (exitLen > 0) {
            var13 = teleportId;
            startLen = exitLen - 1;
            random = this.rngSeed.next() % (startLen + 1);
        } else {
            startLen = this.teleportStarts[teleportId].size();
            int i;
            if (startLen >= 2) {
                int var14 = 0;

                // ?????
                do {
                    i = startLen - 1;
                    random = this.rngSeed.next() % (i + 1);
                    teleportPos = this.teleportStarts[teleportId].get(random);
                    if (Math.abs(teleportPos[0] - (double) x) >= 15.0D
                            || Math.abs(teleportPos[1] - (double) y) >= 15.0D) {
                        this.playerX[playerId] = teleportPos[0];
                        this.playerY[playerId] = teleportPos[1];
                        return;
                    }

                    ++var14;
                } while (var14 < 100);

                return;
            }

            boolean haveExit = false;

            for (i = 0; i < 4 && !haveExit; ++i) {
                if (this.teleportExists[i].size() > 0) {
                    haveExit = true;
                }
            }

            if (!haveExit) {
                return;
            }

            do {
                var13 = this.rngSeed.next() % 4;
                exitLen = this.teleportExists[var13].size();
            } while (exitLen == 0);

            int var12 = exitLen - 1;
            random = this.rngSeed.next() % (var12 + 1);
        }

        // finally move player to exit position
        teleportPos = this.teleportExists[var13].get(random);
        this.playerX[playerId] = teleportPos[0];
        this.playerY[playerId] = teleportPos[1];
    }

    private void handleMines(
            boolean isBigMine, int playerId, int screenX, int screenY, Graphics var5, Graphics canvas) {
        int mapX = screenX / 15;
        int mapY = screenY / 15;
        int special = super.trackTiles[mapX][mapY] / 16777216;
        int shape = super.trackTiles[mapX][mapY] / 65536 % 256 + 24;
        int foreground = super.trackTiles[mapX][mapY] / 256 % 256;
        int background = super.trackTiles[mapX][mapY] % 256;
        // 28 Mine
        // 30 Big Mine
        if (special == 2 && (shape == 28 || shape == 30)) {
            ++shape;
            super.trackTiles[mapX][mapY] =
                    special * 256 * 256 * 256 + (shape - 24) * 256 * 256 + foreground * 256 + background;
            this.drawTile(mapX, mapY, var5, canvas);

            // Big Mine will dig a hole around mine
            if (isBigMine) {
                int[] downhills =
                        new int[] {17039367, 16779264, 17104905, 16778752, -1, 16779776, 17235973, 16778240, 17170443};
                int tileIndex = 0;

                for (int y = mapY - 1; y <= mapY + 1; ++y) {
                    for (int x = mapX - 1; x <= mapX + 1; ++x) {
                        if (x >= 0
                                && x < 49
                                && y >= 0
                                && y < 25
                                && (y != mapY || x != mapX)
                                && super.trackTiles[x][y] == 16777216) {
                            super.trackTiles[x][y] = downhills[tileIndex];
                            this.drawTile(x, y, var5, canvas);
                        }

                        ++tileIndex;
                    }
                }
            }

            double speed;
            do {
                do {
                    this.speedX[playerId] = (double) (-65 + this.rngSeed.next() % 131) / 10.0D;
                    this.speedY[playerId] = (double) (-65 + this.rngSeed.next() % 131) / 10.0D;
                    speed = Math.sqrt(this.speedX[playerId] * this.speedX[playerId]
                            + this.speedY[playerId] * this.speedY[playerId]);
                } while (speed < 5.2D);
            } while (speed > 6.5D);

            if (!isBigMine) {
                this.speedX[playerId] *= 0.8D;
                this.speedY[playerId] *= 0.8D;
            }
        }
    }

    private boolean handleMovableBlock(
            int screenX, int screenY, Graphics var3, Graphics canvas, int offsetX, int offsetY, boolean nonSunkable) {
        int mapX = screenX / 15;
        int mapY = screenY / 15;
        int special = super.trackTiles[mapX][mapY] / 16777216;
        int shape = super.trackTiles[mapX][mapY] / 65536 % 256 + 24;
        int background = super.trackTiles[mapX][mapY] / 256 % 256;
        if (special == 2 && (shape == 27 || shape == 46)) {
            // where we want to move the block
            int x1 = mapX + offsetX;
            int y1 = mapY + offsetY;
            int canMove = this.canMovableBlockMove(x1, y1);
            if (canMove == -1) {
                return false;
            } else {
                // 16777216 == special:1 shape:0 fg:0 bg:0
                super.trackTiles[mapX][mapY] = 16777216 + background * 256;
                this.drawTile(mapX, mapY, var3, canvas);
                // [x,y,background id]
                int[] tileWithCoords = this.calculateMovableBlockEndPosition(
                        mapX, mapY, x1, y1, background, canMove, var3, canvas, nonSunkable, 0);
                // 12 Water
                // 13 Acid
                if (!nonSunkable && (tileWithCoords[2] == 12 || tileWithCoords[2] == 13)) {
                    // Sunked Movable Block with old background
                    super.trackTiles[tileWithCoords[0]][tileWithCoords[1]] = 35061760 + tileWithCoords[2] * 256;
                } else {
                    // Movable Block with old background
                    super.trackTiles[tileWithCoords[0]][tileWithCoords[1]] =
                            33554432 + ((nonSunkable ? 27 : 46) - 24) * 256 * 256 + tileWithCoords[2] * 256;
                }

                this.drawTile(tileWithCoords[0], tileWithCoords[1], var3, canvas);
                return true;
            }
        } else {
            return false;
        }
    }

    private int canMovableBlockMove(int x, int y) {
        if (x >= 0 && x < 49 && y >= 0 && y < 25) {
            int special = super.trackTiles[x][y] / 16777216;
            int shape = super.trackTiles[x][y] / 65536 % 256;
            int background = super.trackTiles[x][y] / 256 % 256;
            if (special == 1 && shape == 0 && background <= 15) {
                for (int i = 0; i < this.playerCount; ++i) {
                    if (this.playerX[i] > (double) (x * 15)
                            && this.playerX[i] < (double) (x * 15 + 15 - 1)
                            && this.playerY[i] > (double) (y * 15)
                            && this.playerY[i] < (double) (y * 15 + 15 - 1)) {
                        return -1;
                    }
                }

                return background;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private int[] calculateMovableBlockEndPosition(
            int x,
            int y,
            int x1,
            int y1,
            int background,
            int background1,
            Graphics var7,
            Graphics var8,
            boolean var9,
            int i) {
        int[] xytile = new int[] {x1, y1, background1};
        if (!var9 && background1 >= 4 && background1 <= 11 && i < 1078) {
            x = x1;
            y = y1;
            background = background1;
            // Downhill North
            if (background1 == 4 || background1 == 5 || background1 == 11) {
                --y1;
            }
            // Downhill South
            if (background1 == 8 || background1 == 7 || background1 == 9) {
                ++y1;
            }

            // Downhill East
            if (background1 == 5 || background1 == 6 || background1 == 7) {
                ++x1;
            }
            // Downhill West
            if (background1 == 9 || background1 == 10 || background1 == 11) {
                --x1;
            }

            background1 = this.canMovableBlockMove(x1, y1);
            if (background1 >= 0) {
                xytile = this.calculateMovableBlockEndPosition(
                        x, y, x1, y1, background, background1, var7, var8, var9, i + 1);
            }
        }

        return xytile;
    }

    private void handleBreakableBlock(int screenX, int screenY, Graphics var3, Graphics canvas) {
        int mapX = screenX / 15;
        int mapY = screenY / 15;
        int special = super.trackTiles[mapX][mapY] / 16777216;
        int shape = super.trackTiles[mapX][mapY] / 65536 % 256 + 24;
        int background = super.trackTiles[mapX][mapY] / 256 % 256;
        int foreground = super.trackTiles[mapX][mapY] % 256;
        if (special == 2 && shape >= 40 && shape <= 43) {
            ++shape;
            if (shape <= 43) {
                super.trackTiles[mapX][mapY] =
                        special * 256 * 256 * 256 + (shape - 24) * 256 * 256 + background * 256 + foreground;
            } else {
                super.trackTiles[mapX][mapY] = 16777216 + background * 256 + background;
            }

            this.drawTile(mapX, mapY, var3, canvas);
        }
    }

    private void drawTile(int tileX, int tileY, Graphics var3, Graphics var4) {
        Image tile = super.getTileAt(tileX, tileY);
        super.collisionMap(tileX, tileY);
        var3.drawImage(tile, tileX * 15, tileY * 15, this);
        var4.drawImage(tile, tileX * 15, tileY * 15, this);
    }

    private void drawPlayer(Graphics g, int playerid, double var3) {
        int x = (int) (this.playerX[playerid] - 6.5D + 0.5D);
        int y = (int) (this.playerY[playerid] - 6.5D + 0.5D);
        int var7 = 13;
        if (var3 > 0.0D) {
            x = (int) ((double) x + var3);
            y = (int) ((double) y + var3);
            var7 = (int) ((double) var7 - var3 * 2.0D);
        }

        int ballSpriteOffset = 0;
        if (super.gameContainer.graphicsQualityIndex == 3) {
            ballSpriteOffset = (x / 5 + y / 5) % 2 * 4;
        }

        if (var3 == 0.0D) {
            g.drawImage(this.ballSprites[playerid + ballSpriteOffset], x, y, this);
            if (this.playerNamesDisplayMode > 0
                    && this.aBooleanArray2834[playerid]
                    && this.gameState != 2
                    && this.playerCount > 1) {
                String[] playerName = super.gameContainer.gamePanel.getPlayerName(playerid);
                if (this.playerNamesDisplayMode == 1) {
                    StringDraw.drawString(g, playerName[0].substring(0, 1), x + 6, y + 13 - 3, 0);
                    return;
                }

                int nameWidth = StringDraw.getStringWidth(g, playerName[0]);
                int textX = x + 13 + 2;
                if (this.playerNamesDisplayMode != 2 && playerName[1] != null) {
                    String clanName = "[" + playerName[1] + "]";
                    int clanWidth = StringDraw.getStringWidth(g, clanName);
                    byte var14 = -1;
                    if (textX + nameWidth >= 733 || textX + clanWidth >= 733) {
                        textX = x - 2;
                        var14 = 1;
                    }

                    StringDraw.drawOutlinedString(g, backgroundColour, playerName[0], textX, y + 13 - 3 - 6, var14);
                    StringDraw.drawOutlinedString(g, backgroundColour, clanName, textX, y + 13 - 3 + 7, var14);
                    return;
                }

                if (textX + nameWidth >= 733) {
                    textX = x - 2 - nameWidth;
                }

                StringDraw.drawOutlinedString(g, backgroundColour, playerName[0], textX, y + 13 - 3, -1);
            }
        } else {
            g.drawImage(this.ballSprites[playerid + ballSpriteOffset], x, y, x + var7, y + var7, 0, 0, 13, 13, this);
        }
    }

    private void setStrokeListeners(boolean requestFocus) {
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.setCursor(cursorCrosshair);
        this.addKeyListener(this);
        if (requestFocus) {
            // this.requestFocus();//todo this is annoying as fuck
        }
    }

    private void drawDashedLine(Graphics var1, int var2, int var3, int var4, int var5) {
        int var6 = var4 >= 0 ? var4 : -var4;
        int var7 = var5 >= 0 ? var5 : -var5;
        int var8 = Math.max(var6, var7) / 10;
        double var9 = var2;
        double var11 = var3;
        double var13 = (double) var4 / ((double) var8 * 2.0D);
        double var15 = (double) var5 / ((double) var8 * 2.0D);
        var9 += var13;
        var11 += var15;

        for (int var17 = 0; var17 < var8; ++var17) {
            var1.drawLine((int) var9, (int) var11, (int) (var9 + var13), (int) (var11 + var15));
            var9 += var13 * 2.0D;
            var11 += var15 * 2.0D;
        }
    }

    private void method164(int var1) {
        anIntArray2837[0] = anIntArray2837[1];
        anIntArray2837[1] = anIntArray2837[2];
        if (anIntArray2837[1] < var1) {
            var1 = anIntArray2837[1];
        }

        if (anIntArray2837[0] < var1) {
            var1 = anIntArray2837[0];
        }

        while (var1 > 700 && this.anInt2839 > 1) {
            var1 -= 700;
            --this.anInt2839;
        }

        while (var1 < -2000 && this.anInt2839 < 6) {
            var1 += 2000;
            ++this.anInt2839;
        }

        anIntArray2837[2] = var1;
        anInt2838 = this.anInt2839;
    }
}
