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
import org.moparforia.shared.tracks.parsers.VersionedTrackFileParser;

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
    private Image[] ballSprites;
    private int mouseX;
    private int mouseY;
    private int shootingMode;
    private int playerNamesDisplayMode; // 0 == Hide names, 1 == Show initials, 2 == Show names, 3 ==
    // Name + clan
    private String encodedCoordinates;
    private static int[] frameTimeHistory = new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
    private Image gameArea;
    private Graphics graphics;
    private Thread shotThread;
    private boolean norandom;

    // aimbot stuff
    private final boolean allowCheating = false;
    private double hackedX = 0;
    private double hackedY = 0;
    private boolean isCheating = false;

    private GameState state;

    protected GameCanvas(GameContainer gameContainer, Image image, Cursor c) {
        super(gameContainer, image);
        this.state = new GameState();
        this.ballSprites = gameContainer.spriteManager.getBalls();
        this.state.playerCount = this.state.currentPlayerId = this.mouseX = this.mouseY = -1;
        this.playerNamesDisplayMode = 0;
        this.state.gameState = 0;
        this.state.maxPhysicsIterations = 2;
        this.norandom = Parameters.getBooleanValue(gameContainer.params.getParameter("norandom"));
        // TODO: would be cool if user can set their own cursor
        this.cursorCrosshair = c;
    }

    @Override
    public void update(Graphics g) {
        if (this.gameArea == null) {
            this.gameArea = this.createImage(735, 375);
            this.graphics = this.gameArea.getGraphics();
        }

        super.update(this.graphics);
        if (this.state.gameState == 1 && this.mouseX > -1 && this.mouseY > -1) {
            double[] power = this.getStrokePower(this.state.currentPlayerId, this.mouseX, this.mouseY);

            int x1 = (int) (this.state.playerX[this.state.currentPlayerId] + 0.5D);
            int y1 = (int) (this.state.playerY[this.state.currentPlayerId] + 0.5D);
            int x2 = (int) (this.state.playerX[this.state.currentPlayerId] + power[0] * 200.0D / 6.5D + 0.5D);
            int y2 = (int) (this.state.playerY[this.state.currentPlayerId] + power[1] * 200.0D / 6.5D + 0.5D);
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

        if (this.state.currentPlayerId > -1) {
            this.graphics.setFont(gameFont);
            this.graphics.setColor(blackColour);

            for (int player = 0; player < this.state.playerCount; ++player) {
                if (this.state.simulatePlayer[player] && player != this.state.currentPlayerId) {
                    this.drawPlayer(
                            this.graphics, player, this.state.onHoleSync[player].get() ? 2.1666666666666665D : 0.0D);
                }
            }

            this.graphics.setColor(whiteColour);
            this.drawPlayer(
                    this.graphics,
                    this.state.currentPlayerId,
                    this.state.onHoleSync[this.state.currentPlayerId].get() ? 2.1666666666666665D : 0.0D);
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
                    this.ballSprites[this.state.currentPlayerId + ballSpriteOffset],
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

        g.drawImage(this.gameArea, 0, 0, this);
    }

    @Override
    public void run() {
        Image ballImage = this.createImage(735, 375);
        Image gameImage = super.image;
        Graphics ballGraphic = ballImage.getGraphics();
        Graphics canvas = this.getGraphics();
        ballGraphic.drawImage(gameImage, 0, 0, this);
        canvas.drawImage(ballImage, 0, 0, this);
        int loopStuckCounter = 0;
        int[] magnetStuckCounter = new int[this.state.playerCount];
        int[] downhillStuckCounter = new int[this.state.playerCount];
        double[] tempCoordX = new double[this.state.playerCount];
        double[] tempCoordY = new double[this.state.playerCount];
        double[] onHoleTimer = new double[this.state.playerCount];
        double[] tempCoord2X = new double[this.state.playerCount];
        double[] tempCoord2Y = new double[this.state.playerCount];
        double[] tempCoord3X = new double[this.state.playerCount];
        double[] tempCoord3Y = new double[this.state.playerCount];
        boolean[] onHole = new boolean[this.state.playerCount];
        boolean[] onLiquidOrSwamp = new boolean[this.state.playerCount];
        boolean[] teleported = new boolean[this.state.playerCount];
        int[] spinningStuckCounter = new int[this.state.playerCount];

        for (int player = 0; player < this.state.playerCount; ++player) {
            magnetStuckCounter[player] = downhillStuckCounter[player] = 0;
            tempCoordX[player] = tempCoord2X[player] = this.state.playerX[player];
            tempCoordY[player] = tempCoord2Y[player] = this.state.playerY[player];
            onHole[player] = onLiquidOrSwamp[player] = false;
            onHoleTimer[player] = this.state.onHoleSync[player].get() ? 2.1666666666666665D : 0.0D;
            teleported[player] = false;
            spinningStuckCounter[player] = 0;
        }

        boolean shouldSpinAroundHole = false;
        boolean onLiquid = false;

        int allPlayersStoppedCounter = -1;
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
        this.state.bounciness = this.state.somethingSpeedThing = 1.0D;
        int accumulatedSleepTime = 0;

        do {
            long time = System.currentTimeMillis();

            for (int i = 0; i < this.state.playerCount; ++i) {
                tempCoord3X[i] = this.state.playerX[i];
                tempCoord3Y[i] = this.state.playerY[i];
            }

            for (int physicsIteration = 0; physicsIteration < this.state.maxPhysicsIterations; ++physicsIteration) {
                allPlayersStoppedCounter = 0;

                for (int i = 0; i < this.state.playerCount; ++i) {
                    if (this.state.simulatePlayer[i] && !this.state.onHoleSync[i].get()) {
                        for (int j = 0; j < 10; ++j) {

                            // this moves player
                            this.state.playerX[i] += this.state.speedX[i] * 0.1D;
                            this.state.playerY[i] += this.state.speedY[i] * 0.1D;

                            // check if player is going off the map
                            if (this.state.playerX[i] < 6.6D) {
                                this.state.playerX[i] = 6.6D;
                            }

                            if (this.state.playerX[i] >= 727.9D) {
                                this.state.playerX[i] = 727.9D;
                            }

                            if (this.state.playerY[i] < 6.6D) {
                                this.state.playerY[i] = 6.6D;
                            }

                            if (this.state.playerY[i] >= 367.9D) {
                                this.state.playerY[i] = 367.9D;
                            }

                            // checks player vs player collision
                            int anotherPlayer;
                            if (this.state.collisionMode == 1 && !onHole[i] && !onLiquidOrSwamp[i]) {
                                for (anotherPlayer = 0; anotherPlayer < this.state.playerCount; ++anotherPlayer) {
                                    if (i != anotherPlayer
                                            && this.state.simulatePlayer[anotherPlayer]
                                            && !this.state.onHoleSync[anotherPlayer].get()
                                            && !onHole[anotherPlayer]
                                            && !onLiquidOrSwamp[anotherPlayer]
                                            && this.handlePlayerCollisions(i, anotherPlayer)) {
                                        // collision is calculated in another function this just
                                        // makes it less effective
                                        this.state.speedX[i] *= 0.75D;
                                        this.state.speedY[i] *= 0.75D;
                                        this.state.speedX[anotherPlayer] *= 0.75D;
                                        this.state.speedY[anotherPlayer] *= 0.75D;
                                        allPlayersStoppedCounter = 0; // players moved so we reset this to make sure
                                        // they move
                                    }
                                }
                            }

                            x = (int) (this.state.playerX[i] + 0.5D);
                            y = (int) (this.state.playerY[i] + 0.5D);
                            center = this.map.getColMap(x, y);
                            top = this.map.getColMap(x, y - 6);
                            topright = this.map.getColMap(x + diagOffset, y - diagOffset);
                            right = this.map.getColMap(x + 6, y);
                            bottomright = this.map.getColMap(x + diagOffset, y + diagOffset);
                            bottom = this.map.getColMap(x, y + 6);
                            bottomleft = this.map.getColMap(x - diagOffset, y + diagOffset);
                            left = this.map.getColMap(x - 6, y);
                            topleft = this.map.getColMap(x - diagOffset, y - diagOffset);
                            if (center != 12 && center != 13) {
                                onLiquid = center == 14 || center == 15;
                            } else {
                                this.state.speedX[i] *= 0.97D;
                                this.state.speedY[i] *= 0.97D;
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
                        if (this.state.magnetMap != null && !onLiquid && !onHole[i] && !onLiquidOrSwamp[i]) {
                            isAffectedByMagnet = this.handleMagnetForce(i, x, y);
                        }

                        shouldSpinAroundHole = false;
                        double holeSpeed;
                        // 25 hole
                        if (center == 25
                                || this.map.getColMap(x, y - 1) == 25
                                || this.map.getColMap(x + 1, y) == 25
                                || this.map.getColMap(x, y + 1) == 25
                                || this.map.getColMap(x - 1, y) == 25) {
                            holeSpeed = center == 25 ? 1.0D : 0.5D;
                            shouldSpinAroundHole = true;
                            int holeCounter = 0;
                            if (top == 25) {
                                ++holeCounter;
                            } else {
                                this.state.speedY[i] += holeSpeed * 0.03D;
                            }

                            if (topright == 25) {
                                ++holeCounter;
                            } else {
                                this.state.speedY[i] += holeSpeed * 0.03D * magicOffset;
                                this.state.speedX[i] -= holeSpeed * 0.03D * magicOffset;
                            }

                            if (right == 25) {
                                ++holeCounter;
                            } else {
                                this.state.speedX[i] -= holeSpeed * 0.03D;
                            }

                            if (bottomright == 25) {
                                ++holeCounter;
                            } else {
                                this.state.speedY[i] -= holeSpeed * 0.03D * magicOffset;
                                this.state.speedX[i] -= holeSpeed * 0.03D * magicOffset;
                            }

                            if (bottom == 25) {
                                ++holeCounter;
                            } else {
                                this.state.speedY[i] -= holeSpeed * 0.03D;
                            }

                            if (bottomleft == 25) {
                                ++holeCounter;
                            } else {
                                this.state.speedY[i] -= holeSpeed * 0.03D * magicOffset;
                                this.state.speedX[i] += holeSpeed * 0.03D * magicOffset;
                            }

                            if (left == 25) {
                                ++holeCounter;
                            } else {
                                this.state.speedX[i] += holeSpeed * 0.03D;
                            }

                            if (topleft == 25) {
                                ++holeCounter;
                            } else {
                                this.state.speedY[i] += holeSpeed * 0.03D * magicOffset;
                                this.state.speedX[i] += holeSpeed * 0.03D * magicOffset;
                            }

                            if (holeCounter >= 7) {
                                shouldSpinAroundHole = false;
                                onHole[i] = true;
                                this.state.speedX[i] = this.state.speedY[i] = 0.0D;
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
                            tempCoord2X[i] = this.state.playerX[i];
                            tempCoord2Y[i] = this.state.playerY[i];
                        }

                        speed = Math.sqrt(this.state.speedX[i] * this.state.speedX[i]
                                + this.state.speedY[i] * this.state.speedY[i]);
                        if (speed > 0.0D) {
                            double frictionFactor = Tile.calculateFriction(center, speed);
                            this.state.speedX[i] *= frictionFactor;
                            this.state.speedY[i] *= frictionFactor;
                            speed *= frictionFactor;
                            if (speed > 7.0D) {
                                holeSpeed = 7.0D / speed;
                                this.state.speedX[i] *= holeSpeed;
                                this.state.speedY[i] *= holeSpeed;
                                speed *= holeSpeed;
                            }
                        }

                        if (loopStuckCounter > 4000) {
                            this.state.bounciness = 0.0D;
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
                            this.state.speedX[i] = this.state.speedY[i] = 0.0D;
                            if (center != 12 && center != 14 && center != 13 && center != 15) {
                                ++allPlayersStoppedCounter;
                            } else {
                                onLiquidOrSwamp[i] = true;
                            }
                        }

                        if (onHole[i] || onLiquidOrSwamp[i]) {
                            onHoleTimer[i] += 0.1D;
                            if (onHole[i] && onHoleTimer[i] > 2.1666666666666665D
                                    || onLiquidOrSwamp[i] && onHoleTimer[i] > 6.0D) {
                                // 25 hole
                                if (center == 25) {
                                    this.state.onHoleSync[i].set(true);
                                    if (this.state.isLocalPlayer && this.state.playerCount > 1) {
                                        super.gameContainer.gamePanel.hideSkipButton();
                                    }
                                } else {
                                    // water 12
                                    // water swamp 14
                                    if (center == 12 || center == 14) {
                                        this.state.playerX[i] =
                                                this.state.onShoreSetting == 0 ? tempCoordX[i] : tempCoord2X[i];
                                        this.state.playerY[i] =
                                                this.state.onShoreSetting == 0 ? tempCoordY[i] : tempCoord2Y[i];
                                    }

                                    // 13 acid
                                    // 15 acid swamp
                                    if (center == 13 || center == 15) {
                                        this.resetPosition(i, false);
                                    }

                                    onHoleTimer[i] = 0.0D;
                                }

                                onHole[i] = onLiquidOrSwamp[i] = false;
                                ++allPlayersStoppedCounter;
                            }
                        }
                    } else {
                        ++allPlayersStoppedCounter;
                    }
                }

                ++loopStuckCounter;
                if (allPlayersStoppedCounter >= this.state.playerCount) {
                    physicsIteration = this.state.maxPhysicsIterations;
                }
            }

            for (int i = 0; i < this.state.playerCount; ++i) {
                if (this.state.simulatePlayer[i]) {
                    int x1 = (int) (tempCoord3X[i] - 6.5D + 0.5D);
                    int y1 = (int) (tempCoord3Y[i] - 6.5D + 0.5D);
                    int x2 = x1 + 13;
                    int y2 = y1 + 13;
                    ballGraphic.drawImage(gameImage, x1, y1, x2, y2, x1, y1, x2, y2, this);

                    for (int j = 0; j < this.state.playerCount; ++j) {
                        if (this.state.simulatePlayer[j] && j != this.state.currentPlayerId) {
                            this.drawPlayer(ballGraphic, j, onHoleTimer[j]);
                        }
                    }

                    this.drawPlayer(ballGraphic, this.state.currentPlayerId, onHoleTimer[this.state.currentPlayerId]);
                    if (this.state.playerX[i] < tempCoord3X[i]) {
                        x1 = (int) (this.state.playerX[i] - 6.5D + 0.5D);
                    }

                    if (this.state.playerX[i] > tempCoord3X[i]) {
                        x2 = (int) (this.state.playerX[i] - 6.5D + 0.5D) + 13;
                    }

                    if (this.state.playerY[i] < tempCoord3Y[i]) {
                        y1 = (int) (this.state.playerY[i] - 6.5D + 0.5D);
                    }

                    if (this.state.playerY[i] > tempCoord3Y[i]) {
                        y2 = (int) (this.state.playerY[i] - 6.5D + 0.5D) + 13;
                    }

                    canvas.drawImage(ballImage, x1, y1, x2, y2, x1, y1, x2, y2, this);
                }
            }

            time = System.currentTimeMillis() - time; // time to run above for loop
            long sleepTime = (long) (6 * this.state.maxPhysicsIterations) - time;
            // If we want to force fps, set sleepTime to zero so that maxPhysicsIterations
            // are not adjusted. gamePanel.maxFps gets status of checkbox, so
            // it can change in game.
            if (super.gameContainer.synchronizedTrackTestMode.get() && super.gameContainer.gamePanel.maxFps()) {
                sleepTime = 0L;
            }

            Tools.sleep(sleepTime);
            accumulatedSleepTime += sleepTime;
        } while (allPlayersStoppedCounter < this.state.playerCount && !this.state.strokeInterrupted);

        if (!this.state.strokeInterrupted) {
            this.adjustPhysicsIterations(accumulatedSleepTime);
            super.gameContainer.gamePanel.sendEndStroke(
                    this.state.currentPlayerId, this.state.onHoleSync, this.state.isValidPlayerId);
            if (this.state.isValidPlayerId >= 0) {
                this.state.onHoleSync[this.state.isValidPlayerId].set(true);
            }

            this.repaint();
        }
        this.shotThread = null;
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        this.mouseX = event.getX();
        this.mouseY = event.getY();

        if (isCheating) {
            int x = this.mouseX;
            int y = this.mouseY;
            double subtractionX = this.state.playerX[this.state.currentPlayerId] - (double) x;
            double subtractionY = this.state.playerY[this.state.currentPlayerId] - (double) y;
            if (Math.sqrt(subtractionX * subtractionX + subtractionY * subtractionY) >= 6.5D) {
                // this.doHackedStroke(this.state.currentPlayerId, true, x, y, this.shootingMode);
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
        if (this.state.gameState == 1) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                int x = event.getX();
                int y = event.getY();
                this.mouseX = x;
                this.mouseY = y;
                double subtractionX = this.state.playerX[this.state.currentPlayerId] - (double) x;
                double subtractionY = this.state.playerY[this.state.currentPlayerId] - (double) y;
                // checks if mouse is on own ball
                if (Math.sqrt(subtractionX * subtractionX + subtractionY * subtractionY) >= 6.5D) {
                    this.removeMouseMotionListener(this);
                    this.removeMouseListener(this);
                    this.removeKeyListener(this);
                    this.setCursor(cursorDefault);
                    if (super.gameContainer.gamePanel.tryStroke(false)) {
                        super.gameContainer.gamePanel.setBeginStroke(
                                this.state.currentPlayerId, x, y, this.shootingMode);
                        // this.doHackedStroke(this.state.currentPlayerId, true, x, y, this.keyCountMod4);
                        this.doStroke(this.state.currentPlayerId, true, x, y, this.shootingMode);
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
        if (this.state.gameState == 1) {
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
    public void keyTyped(KeyEvent event) {}

    protected void init(int playerCount, int waterMode, int collisionMode) {
        this.state.playerCount = playerCount;
        this.state.onShoreSetting = waterMode;
        this.state.collisionMode = collisionMode;
        this.state.playerX = new double[playerCount];
        this.state.playerY = new double[playerCount];
        this.state.speedX = new double[playerCount];
        this.state.speedY = new double[playerCount];
        this.state.onHoleSync = new SynchronizedBool[playerCount];

        for (int i = 0; i < playerCount; ++i) {
            this.state.onHoleSync[i] = new SynchronizedBool();
        }

        this.state.simulatePlayer = new boolean[playerCount];
        this.state.playerActive = new boolean[playerCount];
        this.playerNamesDisplayMode = playerCount <= 2 ? 0 : 3;
    }

    @Override
    protected void createMap(int tile) {
        super.createMap(tile);
        this.state.currentPlayerId = this.mouseX = this.mouseY = -1;
        this.state.gameState = 0;
        this.repaint();
    }

    protected boolean init(String commandLines, String playerStatuses, int gameId) {
        boolean parseSuccessful = false;
        VersionedTrackFileParser parser = new VersionedTrackFileParser(1);
        try {
            this.track = parser.parseTrackFromString(commandLines);
            this.map.parse(track.getMap());
            this.trackStats = parser.parseStatsFromString(commandLines);
        } catch (Exception e) {
            System.out.println("Error while parsing track & map: " + e.getMessage());
            return false;
        }
        this.map.checkSolids(this.gameContainer.spriteManager);
        super.drawMap();

        this.encodedCoordinates = null;

        List<double[]> startPositions = new ArrayList<>();
        this.state.resetPositionX = new double[4];
        this.state.resetPositionY = new double[4];
        this.state.teleportExits = new ArrayList[4];
        this.state.teleportStarts = new ArrayList[4];
        List<int[]> magnets = new ArrayList<>();

        for (int i = 0; i < 4; ++i) {
            this.state.resetPositionX[i] = this.state.resetPositionY[i] = -1.0D;
            this.state.teleportExits[i] = new ArrayList<>();
            this.state.teleportStarts[i] = new ArrayList<>();
        }

        // Iterates over the 49*25 map
        for (int y = 0; y < 25; ++y) {
            for (int x = 0; x < 49; ++x) {
                if (this.map.getTile(x, y).getSpecial() == 2) {
                    int shape = this.map.getTile(x, y).getShape();
                    double screenX = (double) (x * 15) + 7.5D;
                    double screenY = (double) (y * 15) + 7.5D;
                    // 24 Start Position Common
                    if (shape == 24) {
                        double[] startPosition = new double[] {screenX, screenY};
                        startPositions.add(startPosition);
                    }
                    // 48 Start Position Blue
                    // 49 Start Position Red
                    // 50 Start Positiono Yellow
                    // 51 Start Position Green
                    if (shape >= 48 && shape <= 51) {
                        this.state.resetPositionX[shape - 48] = screenX;
                        this.state.resetPositionY[shape - 48] = screenY;
                    }

                    int teleportIndex;
                    // 33 Teleport Exit Blue
                    // 35 Teleport Exit Red
                    // 37 Teleport Exit Yellow
                    // 39 Teleport Exit Green
                    if (shape == 33 || shape == 35 || shape == 37 || shape == 39) {
                        teleportIndex = (shape - 33) / 2;
                        double[] teleporter = new double[] {screenX, screenY};
                        this.state.teleportExits[teleportIndex].add(teleporter);
                    }

                    // 33 Teleport Start Blue
                    // 35 Teleport Start Red
                    // 37 Teleport Start Yellow
                    // 39 Teleport Start Green
                    if (shape == 32 || shape == 34 || shape == 36 || shape == 38) {
                        teleportIndex = (shape - 32) / 2;
                        double[] teleporter = new double[] {screenX, screenY};
                        this.state.teleportStarts[teleportIndex].add(teleporter);
                    }

                    // 44 magnet attract
                    // 45 magnet repel
                    if (shape == 44 || shape == 45) {
                        int[] magnet = new int[] {(int) (screenX + 0.5D), (int) (screenY + 0.5D), shape};
                        magnets.add(magnet);
                    }
                }
            }
        }

        int startPositionsCount = startPositions.size();
        if (startPositionsCount == 0) {
            this.state.startPositionX = this.state.startPositionY = -1.0D;
        } else {
            double[] startPosition = startPositions.get(gameId % startPositionsCount);
            this.state.startPositionX = startPosition[0];
            this.state.startPositionY = startPosition[1];
        }

        int magnetVecLen = magnets.size();
        if (magnetVecLen == 0) {
            this.state.magnetMap = null;
        } else {
            this.state.magnetMap = new short[147][75][2];

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
                            double modifier = Math.abs(forceTemp2X) / force;
                            force = 127.0D - force;
                            forceTemp2X = (forceTemp2X < 0.0D ? -1.0D : 1.0D) * force * modifier;
                            forcetemp2Y = (forcetemp2Y < 0.0D ? -1.0D : 1.0D) * force * (1.0D - modifier);
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

                    this.state.magnetMap[magnetLoopX / 5][magnetLoopY / 5][0] = (short) forceX;
                    this.state.magnetMap[magnetLoopX / 5][magnetLoopY / 5][1] = (short) forceY;
                }
            }
        }

        for (int i = 0; i < this.state.playerCount; ++i) {
            this.state.playerActive[i] = true;
            this.resetPosition(i, true);
            this.state.onHoleSync[i].set(false);
            this.state.simulatePlayer[i] = playerStatuses.charAt(i) == 't';
        }

        this.state.seed = new Seed(gameId);
        this.repaint();
        return parseSuccessful;
    }

    protected boolean hasCoordinates() {
        return this.encodedCoordinates != null;
    }

    protected void startTurn(int playerId, boolean canLocalPlayerPlay, boolean requestFocus) {
        this.state.currentPlayerId = playerId;
        this.state.playerActive[playerId] = true;
        this.mouseX = this.mouseY = -1;
        this.shootingMode = 0;
        if (canLocalPlayerPlay) {
            this.setStrokeListeners(requestFocus);
            this.state.gameState = 1;
        } else {
            this.state.gameState = 0;
        }

        this.repaint();
    }

    protected void decodeCoords(int playerId, boolean isLocalPlayer, String encoded) {
        int shotData = Integer.parseInt(encoded, 36);
        int x = shotData / 1500;
        int y = shotData % 1500 / 4;
        int shootingMode = shotData % 4;
        this.doStroke(playerId, isLocalPlayer, x, y, shootingMode);
    }

    protected boolean method137() {
        return this.state.gameState == 1;
    }

    protected void endGame() {
        this.removeMouseMotionListener(this);
        this.removeMouseListener(this);
        this.removeKeyListener(this);
        this.setCursor(cursorDefault);
        this.state.gameState = 0;
        this.repaint();
    }

    protected void setPlayerNamesDisplayMode(int mode) {
        this.playerNamesDisplayMode = mode;
        this.repaint();
    }

    protected boolean getSynchronizedBool(int index) {
        return this.state.onHoleSync[index].get();
    }

    protected void restartGame() {
        this.removeMouseMotionListener(this);
        this.removeMouseListener(this);
        this.removeKeyListener(this);
        this.setCursor(cursorDefault);
        if (this.shotThread != null) {
            this.state.strokeInterrupted = true;

            while (this.shotThread != null) {
                Tools.sleep(100L);
            }
        }

        this.state.gameState = 0;
        this.repaint();
    }

    protected String getEncodedCoordinates() {
        if (this.state.gameState != 1) {
            return null;
        } else {
            try {
                String coords = this.encodedCoordinates.substring(0, 4);
                this.encodedCoordinates = this.encodedCoordinates.substring(4);
                return coords;
            } catch (StringIndexOutOfBoundsException e) {
                return null;
            }
        }
    }

    protected void doZeroLengthStroke() {
        this.removeMouseMotionListener(this);
        this.removeMouseListener(this);
        this.removeKeyListener(this);
        this.setCursor(cursorDefault);
        int x = (int) this.state.playerX[this.state.currentPlayerId];
        int y = (int) this.state.playerY[this.state.currentPlayerId];
        super.gameContainer.gamePanel.setBeginStroke(this.state.currentPlayerId, x, y, 0);
        this.doStroke(this.state.currentPlayerId, true, x, y, 0);
    }

    private void doStroke(int playerId, boolean isLocalPlayer, int mouseX, int mouseY, int shootingMode) {
        this.state.isValidPlayerId = super.gameContainer.gamePanel.isValidPlayerID(playerId) ? playerId : -1;
        double[] power = this.getStrokePower(playerId, mouseX, mouseY);
        this.state.speedX[playerId] = power[0];
        this.state.speedY[playerId] = power[1];
        if (shootingMode == 1) {
            this.state.speedX[playerId] = -this.state.speedX[playerId];
            this.state.speedY[playerId] = -this.state.speedY[playerId];
        }

        double temp;
        if (shootingMode == 2) {
            temp = this.state.speedX[playerId];
            this.state.speedX[playerId] = this.state.speedY[playerId];
            this.state.speedY[playerId] = -temp;
        }

        if (shootingMode == 3) {
            temp = this.state.speedX[playerId];
            this.state.speedX[playerId] = -this.state.speedY[playerId];
            this.state.speedY[playerId] = temp;
        }

        temp = Math.sqrt(this.state.speedX[playerId] * this.state.speedX[playerId]
                + this.state.speedY[playerId] * this.state.speedY[playerId]);
        double speed = temp / 6.5D;
        speed *= speed;
        if (!this.norandom) {
            this.state.speedX[playerId] += speed * ((double) (this.state.seed.next() % 50001) / 100000.0D - 0.25D);
            this.state.speedY[playerId] += speed * ((double) (this.state.seed.next() % 50001) / 100000.0D - 0.25D);
        }
        this.state.isLocalPlayer = isLocalPlayer;
        this.state.gameState = 2;
        this.state.strokeInterrupted = false;

        this.shotThread = new Thread(this);
        this.shotThread.start();
    }

    private void doHackedStroke(int playerId, boolean isLocalPlayer, int mouseX, int mouseY, int mod) {
        double[] temp_aDoubleArray2828 = Arrays.copyOf(this.state.speedX, this.state.speedX.length);
        double[] temp_aDoubleArray2829 = Arrays.copyOf(this.state.speedY, this.state.speedY.length);
        boolean temp_aBoolean2832 = this.state.isLocalPlayer;
        boolean temp_aBoolean2843 = this.state.strokeInterrupted;
        Seed temp_aSeed_2836 = this.state.seed.clone();
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
        // this.state.gameState = 2;
        temp_aBoolean2843 = false;

        HackedShot hs = new HackedShot(
                this.state.playerCount,
                this.state.onShoreSetting,
                this.state.collisionMode,
                state.currentPlayerId,
                temp_anInt2816,
                this.state.startPositionX,
                this.state.startPositionY,
                this.state.bounciness,
                this.state.somethingSpeedThing,
                this.state.resetPositionX,
                this.state.resetPositionY,
                this.state.teleportStarts,
                this.state.teleportExits,
                this.state.magnetMap,
                this.state.playerX,
                this.state.playerY,
                temp_aDoubleArray2828,
                temp_aDoubleArray2829,
                this.state.simulatePlayer,
                this.state.onHoleSync,
                temp_aBoolean2832,
                this.state.playerActive,
                temp_aSeed_2836,
                this.state.maxPhysicsIterations,
                temp_aBoolean2843,
                this.map.getColMap(),
                this.map.getTileCodeArray());
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
        if (this.state.resetPositionX[playerId] >= 0.0D && this.state.resetPositionX[playerId] >= 0.0D) {
            this.state.playerX[playerId] = this.state.resetPositionX[playerId];
            this.state.playerY[playerId] = this.state.resetPositionY[playerId];
        } else if (this.state.startPositionX >= 0.0D && this.state.startPositionY >= 0.0D) {
            this.state.playerX[playerId] = this.state.startPositionX;
            this.state.playerY[playerId] = this.state.startPositionY;
            if (gameStart) {
                this.state.playerActive[playerId] = false;
            }

        } else {
            this.state.playerX[playerId] = 367.5D;
            this.state.playerY[playerId] = 187.5D;
        }
    }

    private double[] getStrokePower(int playerId, int mouseX, int mouseY) {
        double deltaX = this.state.playerX[playerId] - (double) mouseX;
        double deltaY = this.state.playerY[playerId] - (double) mouseY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double magnitude = (distance - 5.0D) / 30.0D;
        if (magnitude < 0.075D) {
            magnitude = 0.075D;
        }

        if (magnitude > 6.5D) {
            magnitude = 6.5D;
        }

        double scaleFactor = magnitude / distance;
        double[] power = new double[] {
            ((double) mouseX - this.state.playerX[playerId]) * scaleFactor,
            ((double) mouseY - this.state.playerY[playerId]) * scaleFactor
        };
        return power;
    }

    private boolean handlePlayerCollisions(int player1, int player2) {
        double x = this.state.playerX[player2] - this.state.playerX[player1];
        double y = this.state.playerY[player2] - this.state.playerY[player1];
        double distance = Math.sqrt(x * x + y * y);
        if (distance != 0.0D && distance <= 13.0D) {
            double forceX = x / distance;
            double forceY = y / distance;
            double p1Speed = this.state.speedX[player1] * forceX + this.state.speedY[player1] * forceY;
            double p2Speed = this.state.speedX[player2] * forceX + this.state.speedY[player2] * forceY;
            if (p1Speed - p2Speed <= 0.0D) {
                return false;
            } else {
                double p1PerpSpeed = -this.state.speedX[player1] * forceY + this.state.speedY[player1] * forceX;
                double p2PerpSpeed = -this.state.speedX[player2] * forceY + this.state.speedY[player2] * forceX;
                this.state.speedX[player1] = p2Speed * forceX - p1PerpSpeed * forceY;
                this.state.speedY[player1] = p2Speed * forceY + p1PerpSpeed * forceX;
                this.state.speedX[player2] = p1Speed * forceX - p2PerpSpeed * forceY;
                this.state.speedY[player2] = p1Speed * forceY + p2PerpSpeed * forceX;
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean handleDownhill(int playerId, int elementId) {
        if (elementId >= 4 && elementId <= 11) {
            if (elementId == 4) {
                this.state.speedY[playerId] -= 0.025D;
            }

            if (elementId == 5) {
                this.state.speedY[playerId] -= 0.025D * magicOffset;
                this.state.speedX[playerId] += 0.025D * magicOffset;
            }

            if (elementId == 6) {
                this.state.speedX[playerId] += 0.025D;
            }

            if (elementId == 7) {
                this.state.speedY[playerId] += 0.025D * magicOffset;
                this.state.speedX[playerId] += 0.025D * magicOffset;
            }

            if (elementId == 8) {
                this.state.speedY[playerId] += 0.025D;
            }

            if (elementId == 9) {
                this.state.speedY[playerId] += 0.025D * magicOffset;
                this.state.speedX[playerId] -= 0.025D * magicOffset;
            }

            if (elementId == 10) {
                this.state.speedX[playerId] -= 0.025D;
            }

            if (elementId == 11) {
                this.state.speedY[playerId] -= 0.025D * magicOffset;
                this.state.speedX[playerId] -= 0.025D * magicOffset;
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean handleMagnetForce(int playerId, int mapX, int mapY) {
        int magnetX = mapX / 5;
        int magnetY = mapY / 5;
        short forceX = this.state.magnetMap[magnetX][magnetY][0];
        short forceY = this.state.magnetMap[magnetX][magnetY][1];
        if (forceX == 0 && forceY == 0) {
            return false;
        } else {
            if (this.state.somethingSpeedThing > 0.0D) {
                this.state.somethingSpeedThing -= 1.0E-4D;
            }

            this.state.speedX[playerId] += this.state.somethingSpeedThing * (double) forceX * 5.0E-4D;
            this.state.speedY[playerId] += this.state.somethingSpeedThing * (double) forceY * 5.0E-4D;
            return true;
        }
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
        boolean bottomrightCollide = bottomright >= 16 && bottomright <= 23 && bottomright != 19
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

        if (bottomrightCollide && bottomright == 21) {
            bottomrightCollide = false;
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

        if (bottomrightCollide && bottomright == 22) {
            bottomrightCollide = false;
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
                && bottomrightCollide
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
                    && (this.state.speedX[playerId] > 0.0D && this.state.speedY[playerId] < 0.0D
                            || this.state.speedX[playerId] < 0.0D
                                    && this.state.speedY[playerId] < 0.0D
                                    && -this.state.speedY[playerId] > -this.state.speedX[playerId]
                            || this.state.speedX[playerId] > 0.0D
                                    && this.state.speedY[playerId] > 0.0D
                                    && this.state.speedX[playerId] > this.state.speedY[playerId])) {
                speedEffect =
                        this.getSpeedEffect(topright, playerId, x + diagOffset, y - diagOffset, ball, canvas, 1, -1);
                temp = this.state.speedX[playerId];
                this.state.speedX[playerId] = this.state.speedY[playerId] * speedEffect;
                this.state.speedY[playerId] = temp * speedEffect;
            }

            if (bottomrightCollide
                    && (this.state.speedX[playerId] > 0.0D && this.state.speedY[playerId] > 0.0D
                            || this.state.speedX[playerId] > 0.0D
                                    && this.state.speedY[playerId] < 0.0D
                                    && this.state.speedX[playerId] > -this.state.speedY[playerId]
                            || this.state.speedX[playerId] < 0.0D
                                    && this.state.speedY[playerId] > 0.0D
                                    && this.state.speedY[playerId] > -this.state.speedX[playerId])) {
                speedEffect =
                        this.getSpeedEffect(bottomright, playerId, x + diagOffset, y + diagOffset, ball, canvas, 1, 1);
                temp = this.state.speedX[playerId];
                this.state.speedX[playerId] = -this.state.speedY[playerId] * speedEffect;
                this.state.speedY[playerId] = -temp * speedEffect;
            }

            if (bottomleftCollide
                    && (this.state.speedX[playerId] < 0.0D && this.state.speedY[playerId] > 0.0D
                            || this.state.speedX[playerId] > 0.0D
                                    && this.state.speedY[playerId] > 0.0D
                                    && this.state.speedY[playerId] > this.state.speedX[playerId]
                            || this.state.speedX[playerId] < 0.0D
                                    && this.state.speedY[playerId] < 0.0D
                                    && -this.state.speedX[playerId] > -this.state.speedY[playerId])) {
                speedEffect =
                        this.getSpeedEffect(bottomleft, playerId, x - diagOffset, y + diagOffset, ball, canvas, -1, 1);
                temp = this.state.speedX[playerId];
                this.state.speedX[playerId] = this.state.speedY[playerId] * speedEffect;
                this.state.speedY[playerId] = temp * speedEffect;
            }

            if (topleftCollide
                    && (this.state.speedX[playerId] < 0.0D && this.state.speedY[playerId] < 0.0D
                            || this.state.speedX[playerId] < 0.0D
                                    && this.state.speedY[playerId] > 0.0D
                                    && -this.state.speedX[playerId] > this.state.speedY[playerId]
                            || this.state.speedX[playerId] > 0.0D
                                    && this.state.speedY[playerId] < 0.0D
                                    && -this.state.speedY[playerId] > this.state.speedX[playerId])) {
                speedEffect =
                        this.getSpeedEffect(topleft, playerId, x - diagOffset, y - diagOffset, ball, canvas, -1, -1);
                temp = this.state.speedX[playerId];
                this.state.speedX[playerId] = -this.state.speedY[playerId] * speedEffect;
                this.state.speedY[playerId] = -temp * speedEffect;
            }
        } else {
            if (topCollide && this.state.speedY[playerId] < 0.0D) {
                speedEffect = this.getSpeedEffect(top, playerId, x, y - 6, ball, canvas, 0, -1);
                this.state.speedX[playerId] *= speedEffect;
                this.state.speedY[playerId] *= -speedEffect;
            } else if (bottomCollide && this.state.speedY[playerId] > 0.0D) {
                speedEffect = this.getSpeedEffect(bottom, playerId, x, y + 6, ball, canvas, 0, 1);
                this.state.speedX[playerId] *= speedEffect;
                this.state.speedY[playerId] *= -speedEffect;
            }

            if (rightCollide && this.state.speedX[playerId] > 0.0D) {
                speedEffect = this.getSpeedEffect(right, playerId, x + 6, y, ball, canvas, 1, 0);
                this.state.speedX[playerId] *= -speedEffect;
                this.state.speedY[playerId] *= speedEffect;
                return;
            }

            if (leftcollide && this.state.speedX[playerId] < 0.0D) {
                speedEffect = this.getSpeedEffect(left, playerId, x - 6, y, ball, canvas, -1, 0);
                this.state.speedX[playerId] *= -speedEffect;
                this.state.speedY[playerId] *= speedEffect;
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
            if (this.state.bounciness <= 0.0D) {
                return 0.84D;
            } else {
                this.state.bounciness -= 0.01D;
                double speed = Math.sqrt(this.state.speedX[playerId] * this.state.speedX[playerId]
                        + this.state.speedY[playerId] * this.state.speedY[playerId]);
                return this.state.bounciness * 6.5D / speed;
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
        int exitLen = this.state.teleportExits[teleportId].size();
        int startLen;
        int random;
        double[] teleportPos;
        int selectedTeleportId;
        if (exitLen > 0) {
            selectedTeleportId = teleportId;
            startLen = exitLen - 1;
            random = this.state.seed.next() % (startLen + 1);
        } else {
            startLen = this.state.teleportStarts[teleportId].size();
            int i;
            if (startLen >= 2) {
                int attemptCount = 0;

                // ?????
                do {
                    i = startLen - 1;
                    random = this.state.seed.next() % (i + 1);
                    teleportPos = this.state.teleportStarts[teleportId].get(random);
                    if (Math.abs(teleportPos[0] - (double) x) >= 15.0D
                            || Math.abs(teleportPos[1] - (double) y) >= 15.0D) {
                        this.state.playerX[playerId] = teleportPos[0];
                        this.state.playerY[playerId] = teleportPos[1];
                        return;
                    }

                    ++attemptCount;
                } while (attemptCount < 100);

                return;
            }

            boolean haveExit = false;

            for (i = 0; i < 4 && !haveExit; ++i) {
                if (this.state.teleportExits[i].size() > 0) {
                    haveExit = true;
                }
            }

            if (!haveExit) {
                return;
            }

            do {
                selectedTeleportId = this.state.seed.next() % 4;
                exitLen = this.state.teleportExits[selectedTeleportId].size();
            } while (exitLen == 0);

            random = this.state.seed.next() % (exitLen);
        }

        // finally move player to exit position
        teleportPos = this.state.teleportExits[selectedTeleportId].get(random);
        this.state.playerX[playerId] = teleportPos[0];
        this.state.playerY[playerId] = teleportPos[1];
    }

    private void handleMines(
            boolean isBigMine, int playerId, int screenX, int screenY, Graphics ballCanvas, Graphics canvas) {
        int mapX = screenX / 15;
        int mapY = screenY / 15;
        Tile tile = this.map.getTile(mapX, mapY);
        int special = tile.getSpecial();
        int shape = tile.getShape();
        int foreground = tile.getForeground();
        int background = tile.getBackground();
        // 28 Mine
        // 30 Big Mine
        if (special == 2 && (shape == 28 || shape == 30)) {
            ++shape;
            this.map.updateTile(
                    mapX, mapY, special * 256 * 256 * 256 + (shape - 24) * 256 * 256 + foreground * 256 + background);
            this.drawTile(mapX, mapY, ballCanvas, canvas);

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
                                && this.map.getTile(x, y).getCode() == 16777216) {
                            // super.map.setTile(x, y, downhills[tileIndex]);
                            this.map.updateTile(x, y, downhills[tileIndex]);
                            this.drawTile(x, y, ballCanvas, canvas);
                        }

                        ++tileIndex;
                    }
                }
            }

            double speed;
            do {
                do {
                    this.state.speedX[playerId] = (double) (-65 + this.state.seed.next() % 131) / 10.0D;
                    this.state.speedY[playerId] = (double) (-65 + this.state.seed.next() % 131) / 10.0D;
                    speed = Math.sqrt(this.state.speedX[playerId] * this.state.speedX[playerId]
                            + this.state.speedY[playerId] * this.state.speedY[playerId]);
                } while (speed < 5.2D);
            } while (speed > 6.5D);

            if (!isBigMine) {
                this.state.speedX[playerId] *= 0.8D;
                this.state.speedY[playerId] *= 0.8D;
            }
        }
    }

    private boolean handleMovableBlock(
            int screenX,
            int screenY,
            Graphics ballGraphics,
            Graphics canvas,
            int offsetX,
            int offsetY,
            boolean nonSunkable) {
        int mapX = screenX / 15;
        int mapY = screenY / 15;
        Tile tile = this.map.getTile(mapX, mapY);
        int special = tile.getSpecial();
        int shape = tile.getShape();
        int background = tile.getBackground();
        if (special == 2 && (shape == 27 || shape == 46)) {
            // where we want to move the block
            int x1 = mapX + offsetX;
            int y1 = mapY + offsetY;
            int canMove = this.map.canMovableBlockMove(
                    x1, y1, this.state.playerX, this.state.playerY, this.state.playerCount);
            if (canMove == -1) {
                return false;
            } else {
                // 16777216 == special:1 shape:0 fg:0 bg:0
                this.map.updateTile(mapX, mapY, 16777216 + background * 256);
                this.drawTile(mapX, mapY, ballGraphics, canvas);
                // [x,y,background id]
                int[] tileWithCoords =
                        this.calculateMovableBlockEndPosition(mapX, mapY, x1, y1, background, canMove, nonSunkable, 0);
                // 12 Water
                // 13 Acid
                if (!nonSunkable && (tileWithCoords[2] == 12 || tileWithCoords[2] == 13)) {
                    // Sunked Movable Block with old background
                    this.map.updateTile(tileWithCoords[0], tileWithCoords[1], 35061760 + tileWithCoords[2] * 256);
                } else {
                    // Movable Block with old background
                    this.map.updateTile(
                            tileWithCoords[0],
                            tileWithCoords[1],
                            33554432 + ((nonSunkable ? 27 : 46) - 24) * 256 * 256 + tileWithCoords[2] * 256);
                }

                this.drawTile(tileWithCoords[0], tileWithCoords[1], ballGraphics, canvas);
                return true;
            }
        } else {
            return false;
        }
    }

    // Block in coordinates (x, y) with background 'background'
    // Destination (x1, y1) with background 'background1'
    // calculates the end position (if the block moves more than once)
    //
    // Returns an array of three: (x, y) of the block end position and the
    // background of that tile.
    private int[] calculateMovableBlockEndPosition(
            int x, int y, int x1, int y1, int background, int background1, boolean nonSunkable, int i) {
        int[] xytile = new int[] {x1, y1, background1};
        if (!nonSunkable && background1 >= 4 && background1 <= 11 && i < 1078) {
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

            background1 = this.map.canMovableBlockMove(
                    x1, y1, this.state.playerX, this.state.playerY, this.state.playerCount);
            if (background1 >= 0) {
                xytile = this.calculateMovableBlockEndPosition(
                        x, y, x1, y1, background, background1, nonSunkable, i + 1);
            }
        }

        return xytile;
    }

    private void handleBreakableBlock(int screenX, int screenY, Graphics ballGraphics, Graphics canvas) {
        int mapX = screenX / 15;
        int mapY = screenY / 15;
        Tile tile = this.map.getTile(mapX, mapY);
        int special = tile.getSpecial();
        int shape = tile.getShape();
        int background = tile.getBackground();
        int foreground = tile.getForeground();
        if (special == 2 && shape >= 40 && shape <= 43) {
            ++shape;
            if (shape <= 43) {
                this.map.updateTile(
                        mapX,
                        mapY,
                        special * 256 * 256 * 256 + (shape - 24) * 256 * 256 + background * 256 + foreground);
            } else {
                this.map.updateTile(mapX, mapY, 16777216 + background * 256 + background);
            }

            this.drawTile(mapX, mapY, ballGraphics, canvas);
        }
    }

    private void drawTile(int tileX, int tileY, Graphics ballGraphics, Graphics canvas) {
        Image tile = super.getTileImageAt(tileX, tileY);
        super.map.collisionMap(tileX, tileY, super.gameContainer.spriteManager);
        ballGraphics.drawImage(tile, tileX * 15, tileY * 15, this);
        canvas.drawImage(tile, tileX * 15, tileY * 15, this);
    }

    private void drawPlayer(Graphics g, int playerid, double shrinkAmount) {
        int x = (int) (this.state.playerX[playerid] - 6.5D + 0.5D);
        int y = (int) (this.state.playerY[playerid] - 6.5D + 0.5D);
        int ballSize = 13;
        if (shrinkAmount > 0.0D) {
            x = (int) ((double) x + shrinkAmount);
            y = (int) ((double) y + shrinkAmount);
            ballSize = (int) ((double) ballSize - shrinkAmount * 2.0D);
        }

        int ballSpriteOffset = 0;
        if (super.gameContainer.graphicsQualityIndex == 3) {
            ballSpriteOffset = (x / 5 + y / 5) % 2 * 4;
        }

        if (shrinkAmount == 0.0D) {
            g.drawImage(this.ballSprites[playerid + ballSpriteOffset], x, y, this);
            if (this.playerNamesDisplayMode > 0
                    && this.state.playerActive[playerid]
                    && this.state.gameState != 2
                    && this.state.playerCount > 1) {
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
                    byte textAlignment = -1;
                    if (textX + nameWidth >= 733 || textX + clanWidth >= 733) {
                        textX = x - 2;
                        textAlignment = 1;
                    }

                    StringDraw.drawOutlinedString(
                            g, backgroundColour, playerName[0], textX, y + 13 - 3 - 6, textAlignment);
                    StringDraw.drawOutlinedString(g, backgroundColour, clanName, textX, y + 13 - 3 + 7, textAlignment);
                    return;
                }

                if (textX + nameWidth >= 733) {
                    textX = x - 2 - nameWidth;
                }

                StringDraw.drawOutlinedString(g, backgroundColour, playerName[0], textX, y + 13 - 3, -1);
            }
        } else {
            g.drawImage(
                    this.ballSprites[playerid + ballSpriteOffset],
                    x,
                    y,
                    x + ballSize,
                    y + ballSize,
                    0,
                    0,
                    13,
                    13,
                    this);
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

    private void drawDashedLine(Graphics g, int x, int y, int deltaX, int deltaY) {
        int absDeltaX = Math.abs(deltaX);
        int absDeltaY = Math.abs(deltaY);
        int dashCount = Math.max(absDeltaX, absDeltaY) / 10;

        double currentX = x;
        double currentY = y;

        double stepX = (double) deltaX / (dashCount * 2.0D);
        double stepY = (double) deltaY / (dashCount * 2.0D);

        currentX += stepX;
        currentY += stepY;

        for (int i = 0; i < dashCount; ++i) {
            g.drawLine((int) currentX, (int) currentY, (int) (currentX + stepX), (int) (currentY + stepY));

            currentX += stepX * 2.0D;
            currentY += stepY * 2.0D;
        }
    }

    // Possibly changes the value of maxPhysicsiterations.
    // Running the physics loop takes few milliseconds, and it runs inside
    // a while loop, so the accumulatedSleepTime can get value of several
    // thousands (at least on my pc). I assume that on a slow machine running
    // the loop can then take over 12ms and then the accumulatedSleepTime will
    // get negative value.
    private void adjustPhysicsIterations(int frameTime) {
        frameTimeHistory[0] = frameTimeHistory[1];
        frameTimeHistory[1] = frameTimeHistory[2];
        if (frameTimeHistory[1] < frameTime) {
            frameTime = frameTimeHistory[1];
        }

        if (frameTimeHistory[0] < frameTime) {
            frameTime = frameTimeHistory[0];
        }

        while (frameTime > 700 && this.state.maxPhysicsIterations > 1) {
            frameTime -= 700;
            --this.state.maxPhysicsIterations;
        }

        while (frameTime < -2000 && this.state.maxPhysicsIterations < 6) {
            frameTime += 2000;
            ++this.state.maxPhysicsIterations;
        }

        frameTimeHistory[2] = frameTime;
    }
}
