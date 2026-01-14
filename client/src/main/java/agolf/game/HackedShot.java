package agolf.game;

import agolf.Seed;
import agolf.SynchronizedBool;
import com.aapeli.tools.Tools;
import java.util.Arrays;
import java.util.List;

public class HackedShot implements Runnable {

    private static final double magicOffset = Math.sqrt(2.0D) / 2.0D;
    private static final int diagOffset = (int) (6.0D * magicOffset + 0.5D);
    private static int[] frameTimeHistory = new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};

    private int playerCount;
    private int onShoreSetting;
    private int collisionMode;
    private int currentPlayerID;
    private int anInt2816;
    private double startPositionX;
    private double startPositionY;
    private double bounciness;
    private double somethingSpeedThing;
    private double[] resetPositionX;
    private double[] resetPositionY;
    private List<double[]>[] teleportStarts;
    private List<double[]>[] teleportExits;
    private short[][][] magnetMap;
    private double[] playerX;
    private double[] playerY;
    private double[] speedX;
    private double[] speedY;
    private boolean[] simulatePlayer;
    private SynchronizedBool[] onHoleSync;
    private boolean isLocalPlayer;
    private boolean[] playerActive;
    private Seed seed;
    private int maxPhysicsIterations;
    private boolean strokeInterrupted;

    // from superclass
    protected byte[][] collisionMap;
    protected int[][] mapTiles;

    // this actually scares me, but its for science.
    public HackedShot(
            int playerCount,
            int onShoreSetting,
            int collisionMode,
            int currentPlayerID,
            int playerId,
            double startPositionX,
            double startPositionY,
            double bounciness,
            double somethingSpeedThing,
            double[] resetPositionX,
            double[] resetPositionY,
            List<double[]>[] teleportStarts,
            List<double[]>[] teleportExits,
            short[][][] magnetMap,
            double[] playerX,
            double[] playerY,
            double[] aDoubleArray2828,
            double[] aDoubleArray2829,
            boolean[] simulatePlayer,
            SynchronizedBool[] onHoleSync,
            boolean aBoolean2832,
            boolean[] playerActive,
            Seed seed,
            int maxPhysicsIterations,
            boolean aBoolean2843,
            byte[][] collisionMap,
            int[][] mapTiles) {

        this.playerCount = playerCount;
        this.onShoreSetting = onShoreSetting;
        this.collisionMode = collisionMode;
        this.currentPlayerID = currentPlayerID;
        this.anInt2816 = playerId;
        this.startPositionX = startPositionX;
        this.startPositionY = startPositionY;
        this.bounciness = bounciness;
        this.somethingSpeedThing = somethingSpeedThing;
        this.resetPositionX = Arrays.copyOf(resetPositionX, resetPositionX.length);
        this.resetPositionY = Arrays.copyOf(resetPositionY, resetPositionY.length);
        this.teleportStarts = Arrays.copyOf(teleportStarts, teleportStarts.length);
        this.teleportExits = Arrays.copyOf(teleportExits, teleportExits.length);
        if (magnetMap != null) {
            this.magnetMap = Arrays.copyOf(magnetMap, magnetMap.length);
        } else {
            this.magnetMap = null;
        }
        this.playerX = Arrays.copyOf(playerX, playerX.length);
        this.playerY = Arrays.copyOf(playerY, playerY.length);
        this.speedX = Arrays.copyOf(aDoubleArray2828, aDoubleArray2828.length);
        this.speedY = Arrays.copyOf(aDoubleArray2829, aDoubleArray2829.length);
        this.simulatePlayer = Arrays.copyOf(simulatePlayer, simulatePlayer.length);
        this.onHoleSync = Arrays.copyOf(onHoleSync, onHoleSync.length); // unsure bout thsione
        this.isLocalPlayer = aBoolean2832;
        this.playerActive = Arrays.copyOf(playerActive, playerActive.length);
        this.seed = seed.clone();
        this.maxPhysicsIterations = maxPhysicsIterations;
        this.strokeInterrupted = aBoolean2843;
        this.collisionMap = Arrays.copyOf(collisionMap, collisionMap.length);
        this.mapTiles = Arrays.copyOf(mapTiles, mapTiles.length);
    }

    public void run() {
        /* Image var1 = this.createImage(735, 375);
        Image var2 = mage;
        Graphics var3 = var1.getGraphics();
        Graphics var4 = this.getGraphics();
        var3.drawImage(var2, 0, 0, this);
        var4.drawImage(var1, 0, 0, this);
        */

        int var5 = 0;
        int[] var6 = new int[this.playerCount];
        int[] var7 = new int[this.playerCount];
        double[] var8 = new double[this.playerCount];
        double[] var9 = new double[this.playerCount];
        double[] var10 = new double[this.playerCount];
        double[] var11 = new double[this.playerCount];
        double[] var12 = new double[this.playerCount];
        double[] var13 = new double[this.playerCount];
        double[] var14 = new double[this.playerCount];
        boolean[] var15 = new boolean[this.playerCount];
        boolean[] var16 = new boolean[this.playerCount];
        boolean[] var17 = new boolean[this.playerCount];
        int[] var18 = new int[this.playerCount];

        for (int var19 = 0; var19 < this.playerCount; ++var19) {
            var6[var19] = var7[var19] = 0;
            var8[var19] = var11[var19] = this.playerX[var19];
            var9[var19] = var12[var19] = this.playerY[var19];
            var15[var19] = var16[var19] = false;
            var10[var19] = this.onHoleSync[var19].get() ? 2.1666666666666665D : 0.0D;
            var17[var19] = false;
            var18[var19] = 0;
        }

        boolean var20 = false;
        boolean var21 = false;
        boolean var22 = true;
        boolean var23 = true;

        int allPlayersStoppedCounter = -1;
        byte var25 = 0;
        byte var26 = 0;
        byte var27 = 0;
        byte var28 = 0;
        byte var29 = 0;
        byte var30 = 0;
        byte var31 = 0;
        byte var32 = 0;
        byte var33 = 0;
        int var34 = 0;
        int var35 = 0;
        double var36 = 0.0D;
        this.bounciness = this.somethingSpeedThing = 1.0D;
        int var38 = 0;

        do {
            long var39 = System.currentTimeMillis();

            for (int var41 = 0; var41 < this.playerCount; ++var41) {
                var13[var41] = this.playerX[var41];
                var14[var41] = this.playerY[var41];
            }

            int var43;
            int var44;
            for (int var42 = 0; var42 < this.maxPhysicsIterations; ++var42) {
                allPlayersStoppedCounter = 0;

                for (var43 = 0; var43 < this.playerCount; ++var43) {
                    if (this.simulatePlayer[var43] && !this.onHoleSync[var43].get()) {
                        for (var44 = 0; var44 < 10; ++var44) {
                            this.playerX[var43] += this.speedX[var43] * 0.1D;
                            this.playerY[var43] += this.speedY[var43] * 0.1D;
                            if (this.playerX[var43] < 6.6D) {
                                this.playerX[var43] = 6.6D;
                            }

                            if (this.playerX[var43] >= 727.9D) {
                                this.playerX[var43] = 727.9D;
                            }

                            if (this.playerY[var43] < 6.6D) {
                                this.playerY[var43] = 6.6D;
                            }

                            if (this.playerY[var43] >= 367.9D) {
                                this.playerY[var43] = 367.9D;
                            }

                            int var45;
                            if (this.collisionMode == 1 && !var15[var43] && !var16[var43]) {
                                for (var45 = 0; var45 < this.playerCount; ++var45) {
                                    if (var43 != var45
                                            && this.simulatePlayer[var45]
                                            && !this.onHoleSync[var45].get()
                                            && !var15[var45]
                                            && !var16[var45]
                                            && this.handlePlayerCollisions(var43, var45)) {
                                        this.speedX[var43] *= 0.75D;
                                        this.speedY[var43] *= 0.75D;
                                        this.speedX[var45] *= 0.75D;
                                        this.speedY[var45] *= 0.75D;
                                        allPlayersStoppedCounter = 0;
                                    }
                                }
                            }

                            var35 = (int) (this.playerX[var43] + 0.5D);
                            var34 = (int) (this.playerY[var43] + 0.5D);
                            var33 = collisionMap[var35][var34];
                            var32 = collisionMap[var35][var34 - 6];
                            var31 = collisionMap[var35 + diagOffset][var34 - diagOffset];
                            var30 = collisionMap[var35 + 6][var34];
                            var29 = collisionMap[var35 + diagOffset][var34 + diagOffset];
                            var28 = collisionMap[var35][var34 + 6];
                            var27 = collisionMap[var35 - diagOffset][var34 + diagOffset];
                            var26 = collisionMap[var35 - 6][var34];
                            var25 = collisionMap[var35 - diagOffset][var34 - diagOffset];
                            if (var33 != 12 && var33 != 13) {
                                var21 = var33 == 14 || var33 == 15;
                            } else {
                                this.speedX[var43] *= 0.97D;
                                this.speedY[var43] *= 0.97D;
                                var21 = true;
                            }

                            var45 = 0;

                            for (int var46 = 32; var46 <= 38; var46 += 2) {
                                if (var32 == var46
                                        || var31 == var46
                                        || var30 == var46
                                        || var29 == var46
                                        || var28 == var46
                                        || var27 == var46
                                        || var26 == var46
                                        || var25 == var46) {
                                    ++var45;
                                    if (!var17[var43]) {
                                        this.handleTeleport((var46 - 32) / 2, var43, var35, var34);
                                        var17[var43] = true;
                                    }
                                }
                            }

                            if (var45 == 0) {
                                var17[var43] = false;
                            }

                            if (var33 == 28 || var33 == 30) {
                                this.handleMines(var33 == 30, var43, var35, var34);
                            }

                            this.handleWallCollision(
                                    var43, var32, var31, var30, var29, var28, var27, var26, var25, var35, var34);
                        }

                        boolean var47 = this.handleDownhill(var43, var33);
                        boolean var48 = false;
                        if (this.magnetMap != null && !var21 && !var15[var43] && !var16[var43]) {
                            var48 = this.handleMagnetForce(var43, var35, var34);
                        }

                        var20 = false;
                        double var49;
                        if (var33 == 25
                                || collisionMap[var35][var34 - 1] == 25
                                || collisionMap[var35 + 1][var34] == 25
                                || collisionMap[var35][var34 + 1] == 25
                                || collisionMap[var35 - 1][var34] == 25) {
                            var49 = var33 == 25 ? 1.0D : 0.5D;
                            var20 = true;
                            int var51 = 0;
                            if (var32 == 25) {
                                ++var51;
                            } else {
                                this.speedY[var43] += var49 * 0.03D;
                            }

                            if (var31 == 25) {
                                ++var51;
                            } else {
                                this.speedY[var43] += var49 * 0.03D * magicOffset;
                                this.speedX[var43] -= var49 * 0.03D * magicOffset;
                            }

                            if (var30 == 25) {
                                ++var51;
                            } else {
                                this.speedX[var43] -= var49 * 0.03D;
                            }

                            if (var29 == 25) {
                                ++var51;
                            } else {
                                this.speedY[var43] -= var49 * 0.03D * magicOffset;
                                this.speedX[var43] -= var49 * 0.03D * magicOffset;
                            }

                            if (var28 == 25) {
                                ++var51;
                            } else {
                                this.speedY[var43] -= var49 * 0.03D;
                            }

                            if (var27 == 25) {
                                ++var51;
                            } else {
                                this.speedY[var43] -= var49 * 0.03D * magicOffset;
                                this.speedX[var43] += var49 * 0.03D * magicOffset;
                            }

                            if (var26 == 25) {
                                ++var51;
                            } else {
                                this.speedX[var43] += var49 * 0.03D;
                            }

                            if (var25 == 25) {
                                ++var51;
                            } else {
                                this.speedY[var43] += var49 * 0.03D * magicOffset;
                                this.speedX[var43] += var49 * 0.03D * magicOffset;
                            }

                            if (var51 >= 7) {
                                var20 = false;
                                var15[var43] = true;
                                this.speedX[var43] = this.speedY[var43] = 0.0D;
                            }
                        }

                        if (var20) {
                            ++var18[var43];
                            if (var18[var43] > 500) {
                                var20 = false;
                            }
                        } else {
                            var18[var43] = 0;
                        }

                        if (!var47 && !var48 && !var20 && !var15[var43] && !var16[var43] && !var21) {
                            var11[var43] = this.playerX[var43];
                            var12[var43] = this.playerY[var43];
                        }

                        var36 = Math.sqrt(
                                this.speedX[var43] * this.speedX[var43] + this.speedY[var43] * this.speedY[var43]);
                        if (var36 > 0.0D) {
                            double var52 = this.calculateFriction(var33, var36);
                            this.speedX[var43] *= var52;
                            this.speedY[var43] *= var52;
                            var36 *= var52;
                            if (var36 > 7.0D) {
                                var49 = 7.0D / var36;
                                this.speedX[var43] *= var49;
                                this.speedY[var43] *= var49;
                                var36 *= var49;
                            }
                        }

                        if (var5 > 4000) {
                            this.bounciness = 0.0D;
                            if (var5 > 7000) {
                                var48 = false;
                                var47 = false;
                                var36 = 0.0D;
                            }
                        }

                        if (var47 && var36 < 0.22499999999999998D) {
                            ++var7[var43];
                            if (var7[var43] >= 250) {
                                var47 = false;
                            }
                        } else {
                            var7[var43] = 0;
                        }

                        if (var48 && var36 < 0.22499999999999998D) {
                            ++var6[var43];
                            if (var6[var43] >= 150) {
                                var48 = false;
                            }
                        } else {
                            var6[var43] = 0;
                        }

                        if (var36 < 0.075D && !var47 && !var48 && !var20 && !var15[var43] && !var16[var43]) {
                            this.speedX[var43] = this.speedY[var43] = 0.0D;
                            if (var33 != 12 && var33 != 14 && var33 != 13 && var33 != 15) {
                                ++allPlayersStoppedCounter;
                            } else {
                                var16[var43] = true;
                            }
                        }

                        if (var15[var43] || var16[var43]) {
                            var10[var43] += 0.1D;
                            if (var15[var43] && var10[var43] > 2.1666666666666665D
                                    || var16[var43] && var10[var43] > 6.0D) {
                                if (var33 == 25) {
                                    this.onHoleSync[var43].set(true);
                                    if (this.isLocalPlayer && this.playerCount > 1) {
                                        // gameContainer.gamePanel.hideSkipButton();
                                    }
                                } else {
                                    if (var33 == 12 || var33 == 14) {
                                        this.playerX[var43] = this.onShoreSetting == 0 ? var8[var43] : var11[var43];
                                        this.playerY[var43] = this.onShoreSetting == 0 ? var9[var43] : var12[var43];
                                    }

                                    if (var33 == 13 || var33 == 15) {
                                        this.resetposition(var43, false);
                                    }

                                    var10[var43] = 0.0D;
                                }

                                var15[var43] = var16[var43] = false;
                                ++allPlayersStoppedCounter;
                            }
                        }
                    } else {
                        ++allPlayersStoppedCounter;
                    }
                }

                ++var5;
                if (allPlayersStoppedCounter >= this.playerCount) {
                    var42 = this.maxPhysicsIterations;
                }
            }

            for (var43 = 0; var43 < this.playerCount; ++var43) {
                if (this.simulatePlayer[var43]) {
                    int var54 = (int) (var13[var43] - 6.5D + 0.5D);
                    int var55 = (int) (var14[var43] - 6.5D + 0.5D);
                    int var56 = var54 + 13;
                    int var57 = var55 + 13;
                    // var3.drawImage(var2, var54, var55, var56, var57, var54, var55, var56, var57,
                    // this);

                    for (var44 = 0; var44 < this.playerCount; ++var44) {
                        if (this.simulatePlayer[var44] && var44 != this.currentPlayerID) {
                            // this.method161(var3, var44, var10[var44]);
                        }
                    }

                    // this.method161(var3, this.currentPlayerID, var10[this.currentPlayerID]);
                    if (this.playerX[var43] < var13[var43]) {
                        var54 = (int) (this.playerX[var43] - 6.5D + 0.5D);
                    }

                    if (this.playerX[var43] > var13[var43]) {
                        var56 = (int) (this.playerX[var43] - 6.5D + 0.5D) + 13;
                    }

                    if (this.playerY[var43] < var14[var43]) {
                        var55 = (int) (this.playerY[var43] - 6.5D + 0.5D);
                    }

                    if (this.playerY[var43] > var14[var43]) {
                        var57 = (int) (this.playerY[var43] - 6.5D + 0.5D) + 13;
                    }

                    // var4.drawImage(var1, var54, var55, var56, var57, var54, var55, var56, var57,
                    // this);
                }
            }

            var39 = System.currentTimeMillis() - var39;

            long var58 = 0L; // dont sleep

            Tools.sleep(var58);
            var38 = (int) ((long) var38 + var58);
        } while (allPlayersStoppedCounter < this.playerCount && !this.strokeInterrupted);

        if (this.strokeInterrupted) {
            // this.aThread2842 = null;
        } else {
            this.adjustPhysicsIterations(var38);
            this.onHoleSync[this.anInt2816].set(false); // FUCKING IMPORTANT OR IT BORKS

            // gameContainer.gamePanel.sendEndStroke(this.currentPlayerID,
            // this.aSynchronizedBoolArray2831, this.anInt2816);
            /*
            if (this.anInt2816 >= 0) {
                this.aSynchronizedBoolArray2831[this.anInt2816].set(true);
            }
            */

            // System.out.println("hacked="+playerX[currentPlayerID]+","+playerY[currentPlayerID]);
            // this.aThread2842 = null;
            // this.repaint();
        }
    }

    public double[] getHackedCoordintes() {
        double[] result = new double[] {playerX[currentPlayerID], playerY[currentPlayerID]};
        return result;
    }

    private void handleTeleport(int var1, int var2, int var3, int var4) {
        boolean var5 = true;
        int var6 = this.teleportExits[var1].size();
        int var7;
        int var8;
        double[] var11;
        int var13;
        if (var6 > 0) {
            var13 = var1;
            var7 = var6 - 1;
            var8 = this.seed.next() % (var7 + 1);
        } else {
            var7 = this.teleportStarts[var1].size();
            int var10;
            if (var7 >= 2) {
                int var14 = 0;

                do {
                    var10 = var7 - 1;
                    var8 = this.seed.next() % (var10 + 1);
                    var11 = this.teleportStarts[var1].get(var8);
                    if (Math.abs(var11[0] - (double) var3) >= 15.0D || Math.abs(var11[1] - (double) var4) >= 15.0D) {
                        this.playerX[var2] = var11[0];
                        this.playerY[var2] = var11[1];
                        return;
                    }

                    ++var14;
                } while (var14 < 100);

                return;
            }

            boolean var9 = false;

            for (var10 = 0; var10 < 4 && !var9; ++var10) {
                if (this.teleportExits[var10].size() > 0) {
                    var9 = true;
                }
            }

            if (!var9) {
                return;
            }

            do {
                var13 = this.seed.next() % 4;
                var6 = this.teleportExits[var13].size();
            } while (var6 == 0);

            int var12 = var6 - 1;
            var8 = this.seed.next() % (var12 + 1);
        }

        var11 = this.teleportExits[var13].get(var8);
        this.playerX[var2] = var11[0];
        this.playerY[var2] = var11[1];
    }

    private boolean handlePlayerCollisions(int var1, int var2) {
        double var3 = this.playerX[var2] - this.playerX[var1];
        double var5 = this.playerY[var2] - this.playerY[var1];
        double var7 = Math.sqrt(var3 * var3 + var5 * var5);
        if (var7 != 0.0D && var7 <= 13.0D) {
            double var9 = var3 / var7;
            double var11 = var5 / var7;
            double var13 = this.speedX[var1] * var9 + this.speedY[var1] * var11;
            double var15 = this.speedX[var2] * var9 + this.speedY[var2] * var11;
            if (var13 - var15 <= 0.0D) {
                return false;
            } else {
                double var17 = -this.speedX[var1] * var11 + this.speedY[var1] * var9;
                double var19 = -this.speedX[var2] * var11 + this.speedY[var2] * var9;
                this.speedX[var1] = var15 * var9 - var17 * var11;
                this.speedY[var1] = var15 * var11 + var17 * var9;
                this.speedX[var2] = var13 * var9 - var19 * var11;
                this.speedY[var2] = var13 * var11 + var19 * var9;
                return true;
            }
        } else {
            return false;
        }
    }

    private void handleMines(boolean var1, int var2, int var3, int var4) {
        int var7 = var3 / 15;
        int var8 = var4 / 15;
        int var9 = mapTiles[var7][var8] / 16777216;
        int var10 = mapTiles[var7][var8] / 65536 % 256 + 24;
        int var11 = mapTiles[var7][var8] / 256 % 256;
        int var12 = mapTiles[var7][var8] % 256;
        if (var9 == 2 && (var10 == 28 || var10 == 30)) {
            ++var10;
            mapTiles[var7][var8] = var9 * 256 * 256 * 256 + (var10 - 24) * 256 * 256 + var11 * 256 + var12;
            // this.drawTile(var7, var8, var5, var6);
            if (var1) {
                int[] var13 =
                        new int[] {17039367, 16779264, 17104905, 16778752, -1, 16779776, 17235973, 16778240, 17170443};
                int var14 = 0;

                for (int var15 = var8 - 1; var15 <= var8 + 1; ++var15) {
                    for (int var16 = var7 - 1; var16 <= var7 + 1; ++var16) {
                        if (var16 >= 0
                                && var16 < 49
                                && var15 >= 0
                                && var15 < 25
                                && (var15 != var8 || var16 != var7)
                                && mapTiles[var16][var15] == 16777216) {
                            mapTiles[var16][var15] = var13[var14];
                            // this.drawTile(var16, var15, var5, var6);
                        }

                        ++var14;
                    }
                }
            }

            double var17;
            do {
                do {
                    this.speedX[var2] = (double) (-65 + this.seed.next() % 131) / 10.0D;
                    this.speedY[var2] = (double) (-65 + this.seed.next() % 131) / 10.0D;
                    var17 = Math.sqrt(this.speedX[var2] * this.speedX[var2] + this.speedY[var2] * this.speedY[var2]);
                } while (var17 < 5.2D);
            } while (var17 > 6.5D);

            if (!var1) {
                this.speedX[var2] *= 0.8D;
                this.speedY[var2] *= 0.8D;
            }
        }
    }

    private void handleWallCollision(
            int var1,
            int var2,
            int var3,
            int var4,
            int var5,
            int var6,
            int var7,
            int var8,
            int var9,
            int var10,
            int var11) {
        boolean var14 = var2 >= 16 && var2 <= 23 && var2 != 19 || var2 == 27 || var2 >= 40 && var2 <= 43 || var2 == 46;
        boolean var15 = var3 >= 16 && var3 <= 23 && var3 != 19 || var3 == 27 || var3 >= 40 && var3 <= 43 || var3 == 46;
        boolean var16 = var4 >= 16 && var4 <= 23 && var4 != 19 || var4 == 27 || var4 >= 40 && var4 <= 43 || var4 == 46;
        boolean var17 = var5 >= 16 && var5 <= 23 && var5 != 19 || var5 == 27 || var5 >= 40 && var5 <= 43 || var5 == 46;
        boolean var18 = var6 >= 16 && var6 <= 23 && var6 != 19 || var6 == 27 || var6 >= 40 && var6 <= 43 || var6 == 46;
        boolean var19 = var7 >= 16 && var7 <= 23 && var7 != 19 || var7 == 27 || var7 >= 40 && var7 <= 43 || var7 == 46;
        boolean var20 = var8 >= 16 && var8 <= 23 && var8 != 19 || var8 == 27 || var8 >= 40 && var8 <= 43 || var8 == 46;
        boolean var21 = var9 >= 16 && var9 <= 23 && var9 != 19 || var9 == 27 || var9 >= 40 && var9 <= 43 || var9 == 46;
        if (var14 && var2 == 20) {
            var14 = false;
        }

        if (var21 && var9 == 20) {
            var21 = false;
        }

        if (var15 && var3 == 20) {
            var15 = false;
        }

        if (var20 && var8 == 20) {
            var20 = false;
        }

        if (var16 && var4 == 20) {
            var16 = false;
        }

        if (var16 && var4 == 21) {
            var16 = false;
        }

        if (var15 && var3 == 21) {
            var15 = false;
        }

        if (var17 && var5 == 21) {
            var17 = false;
        }

        if (var14 && var2 == 21) {
            var14 = false;
        }

        if (var18 && var6 == 21) {
            var18 = false;
        }

        if (var18 && var6 == 22) {
            var18 = false;
        }

        if (var17 && var5 == 22) {
            var17 = false;
        }

        if (var19 && var7 == 22) {
            var19 = false;
        }

        if (var16 && var4 == 22) {
            var16 = false;
        }

        if (var20 && var8 == 22) {
            var20 = false;
        }

        if (var20 && var8 == 23) {
            var20 = false;
        }

        if (var19 && var7 == 23) {
            var19 = false;
        }

        if (var21 && var9 == 23) {
            var21 = false;
        }

        if (var18 && var6 == 23) {
            var18 = false;
        }

        if (var14 && var2 == 23) {
            var14 = false;
        }

        if (var14
                && var15
                && var16
                && (var2 < 20 || var2 > 23)
                && (var3 < 20 || var3 > 23)
                && (var4 < 20 || var4 > 23)) {
            var16 = false;
            var14 = false;
        }

        if (var16
                && var17
                && var18
                && (var4 < 20 || var4 > 23)
                && (var5 < 20 || var5 > 23)
                && (var6 < 20 || var6 > 23)) {
            var18 = false;
            var16 = false;
        }

        if (var18
                && var19
                && var20
                && (var6 < 20 || var6 > 23)
                && (var7 < 20 || var7 > 23)
                && (var8 < 20 || var8 > 23)) {
            var20 = false;
            var18 = false;
        }

        if (var20
                && var21
                && var14
                && (var8 < 20 || var8 > 23)
                && (var9 < 20 || var9 > 23)
                && (var2 < 20 || var2 > 23)) {
            var14 = false;
            var20 = false;
        }

        double var22;
        if (!var14 && !var16 && !var18 && !var20) {
            double var24;
            if (var15
                    && (this.speedX[var1] > 0.0D && this.speedY[var1] < 0.0D
                            || this.speedX[var1] < 0.0D
                                    && this.speedY[var1] < 0.0D
                                    && -this.speedY[var1] > -this.speedX[var1]
                            || this.speedX[var1] > 0.0D
                                    && this.speedY[var1] > 0.0D
                                    && this.speedX[var1] > this.speedY[var1])) {
                var22 = this.getSpeedEffect(var3, var1, var10 + diagOffset, var11 - diagOffset, 1, -1);
                var24 = this.speedX[var1];
                this.speedX[var1] = this.speedY[var1] * var22;
                this.speedY[var1] = var24 * var22;
            }

            if (var17
                    && (this.speedX[var1] > 0.0D && this.speedY[var1] > 0.0D
                            || this.speedX[var1] > 0.0D
                                    && this.speedY[var1] < 0.0D
                                    && this.speedX[var1] > -this.speedY[var1]
                            || this.speedX[var1] < 0.0D
                                    && this.speedY[var1] > 0.0D
                                    && this.speedY[var1] > -this.speedX[var1])) {
                var22 = this.getSpeedEffect(var5, var1, var10 + diagOffset, var11 + diagOffset, 1, 1);
                var24 = this.speedX[var1];
                this.speedX[var1] = -this.speedY[var1] * var22;
                this.speedY[var1] = -var24 * var22;
            }

            if (var19
                    && (this.speedX[var1] < 0.0D && this.speedY[var1] > 0.0D
                            || this.speedX[var1] > 0.0D
                                    && this.speedY[var1] > 0.0D
                                    && this.speedY[var1] > this.speedX[var1]
                            || this.speedX[var1] < 0.0D
                                    && this.speedY[var1] < 0.0D
                                    && -this.speedX[var1] > -this.speedY[var1])) {
                var22 = this.getSpeedEffect(var7, var1, var10 - diagOffset, var11 + diagOffset, -1, 1);
                var24 = this.speedX[var1];
                this.speedX[var1] = this.speedY[var1] * var22;
                this.speedY[var1] = var24 * var22;
            }

            if (var21
                    && (this.speedX[var1] < 0.0D && this.speedY[var1] < 0.0D
                            || this.speedX[var1] < 0.0D
                                    && this.speedY[var1] > 0.0D
                                    && -this.speedX[var1] > this.speedY[var1]
                            || this.speedX[var1] > 0.0D
                                    && this.speedY[var1] < 0.0D
                                    && -this.speedY[var1] > this.speedX[var1])) {
                var22 = this.getSpeedEffect(var9, var1, var10 - diagOffset, var11 - diagOffset, -1, -1);
                var24 = this.speedX[var1];
                this.speedX[var1] = -this.speedY[var1] * var22;
                this.speedY[var1] = -var24 * var22;
            }
        } else {
            if (var14 && this.speedY[var1] < 0.0D) {
                var22 = this.getSpeedEffect(var2, var1, var10, var11 - 6, 0, -1);
                this.speedX[var1] *= var22;
                this.speedY[var1] *= -var22;
            } else if (var18 && this.speedY[var1] > 0.0D) {
                var22 = this.getSpeedEffect(var6, var1, var10, var11 + 6, 0, 1);
                this.speedX[var1] *= var22;
                this.speedY[var1] *= -var22;
            }

            if (var16 && this.speedX[var1] > 0.0D) {
                var22 = this.getSpeedEffect(var4, var1, var10 + 6, var11, 1, 0);
                this.speedX[var1] *= -var22;
                this.speedY[var1] *= var22;
                return;
            }

            if (var20 && this.speedX[var1] < 0.0D) {
                var22 = this.getSpeedEffect(var8, var1, var10 - 6, var11, -1, 0);
                this.speedX[var1] *= -var22;
                this.speedY[var1] *= var22;
                return;
            }
        }
    }

    private double getSpeedEffect(int var1, int var2, int var3, int var4, int var7, int var8) {
        if (var1 == 16) {
            return 0.81D;
        } else if (var1 == 17) {
            return 0.05D;
        } else if (var1 == 18) {
            if (this.bounciness <= 0.0D) {
                return 0.84D;
            } else {
                this.bounciness -= 0.01D;
                double var9 = Math.sqrt(this.speedX[var2] * this.speedX[var2] + this.speedY[var2] * this.speedY[var2]);
                return this.bounciness * 6.5D / var9;
            }
        } else if (var1 != 20 && var1 != 21 && var1 != 22 && var1 != 23) {
            if (var1 != 27 && var1 != 46) {
                if (var1 != 40 && var1 != 41 && var1 != 42 && var1 != 43) {
                    return 1.0D;
                } else {
                    this.handleBreakableBlock(var3, var4);
                    return 0.9D;
                }
            } else {
                return this.handleMovableBlock(var3, var4, var7, var8, var1 == 27) ? 0.325D : 0.8D;
            }
        } else {
            return 0.82D;
        }
    }

    private boolean handleMovableBlock(int var1, int var2, int var5, int var6, boolean var7) {
        int var8 = var1 / 15;
        int var9 = var2 / 15;
        int var10 = mapTiles[var8][var9] / 16777216;
        int var11 = mapTiles[var8][var9] / 65536 % 256 + 24;
        int var12 = mapTiles[var8][var9] / 256 % 256;
        if (var10 == 2 && (var11 == 27 || var11 == 46)) {
            int var13 = var8 + var5;
            int var14 = var9 + var6;
            int var15 = this.canMovableBlockMove(var13, var14);
            if (var15 == -1) {
                return false;
            } else {
                mapTiles[var8][var9] = 16777216 + var12 * 256;
                // this.drawTile(var8, var9, var3, var4);
                int[] var16 = this.calculateMovableBlockEndPosition(var8, var9, var13, var14, var12, var15, var7, 0);
                if (!var7 && (var16[2] == 12 || var16[2] == 13)) {
                    mapTiles[var16[0]][var16[1]] = 35061760 + var16[2] * 256;
                } else {
                    mapTiles[var16[0]][var16[1]] = 33554432 + ((var7 ? 27 : 46) - 24) * 256 * 256 + var16[2] * 256;
                }

                // this.drawTile(var16[0], var16[1], var3, var4);
                return true;
            }
        } else {
            return false;
        }
    }

    private int canMovableBlockMove(int var1, int var2) {
        if (var1 >= 0 && var1 < 49 && var2 >= 0 && var2 < 25) {
            int var3 = mapTiles[var1][var2] / 16777216;
            int var4 = mapTiles[var1][var2] / 65536 % 256;
            int var5 = mapTiles[var1][var2] / 256 % 256;
            if (var3 == 1 && var4 == 0 && var5 <= 15) {
                for (int var6 = 0; var6 < this.playerCount; ++var6) {
                    if (this.playerX[var6] > (double) (var1 * 15)
                            && this.playerX[var6] < (double) (var1 * 15 + 15 - 1)
                            && this.playerY[var6] > (double) (var2 * 15)
                            && this.playerY[var6] < (double) (var2 * 15 + 15 - 1)) {
                        return -1;
                    }
                }

                return var5;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private int[] calculateMovableBlockEndPosition(
            int var1, int var2, int var3, int var4, int var5, int var6, boolean var9, int var10) {
        int[] var11 = new int[] {var3, var4, var6};
        if (!var9 && var6 >= 4 && var6 <= 11 && var10 < 1078) {
            var1 = var3;
            var2 = var4;
            var5 = var6;
            if (var6 == 4 || var6 == 5 || var6 == 11) {
                --var4;
            }

            if (var6 == 8 || var6 == 7 || var6 == 9) {
                ++var4;
            }

            if (var6 == 5 || var6 == 6 || var6 == 7) {
                ++var3;
            }

            if (var6 == 9 || var6 == 10 || var6 == 11) {
                --var3;
            }

            var6 = this.canMovableBlockMove(var3, var4);
            if (var6 >= 0) {
                var11 = this.calculateMovableBlockEndPosition(var1, var2, var3, var4, var5, var6, var9, var10 + 1);
            }
        }

        return var11;
    }

    private void handleBreakableBlock(int var1, int var2) {
        int var5 = var1 / 15;
        int var6 = var2 / 15;
        int var7 = mapTiles[var5][var6] / 16777216;
        int var8 = mapTiles[var5][var6] / 65536 % 256 + 24;
        int var9 = mapTiles[var5][var6] / 256 % 256;
        int var10 = mapTiles[var5][var6] % 256;
        if (var7 == 2 && var8 >= 40 && var8 <= 43) {
            ++var8;
            if (var8 <= 43) {
                mapTiles[var5][var6] = var7 * 256 * 256 * 256 + (var8 - 24) * 256 * 256 + var9 * 256 + var10;
            } else {
                mapTiles[var5][var6] = 16777216 + var9 * 256 + var9;
            }

            // this.drawTile(var5, var6, var3, var4);
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

    private boolean handleMagnetForce(int var1, int var2, int var3) {
        int var4 = var2 / 5;
        int var5 = var3 / 5;
        short var6 = this.magnetMap[var4][var5][0];
        short var7 = this.magnetMap[var4][var5][1];
        if (var6 == 0 && var7 == 0) {
            return false;
        } else {
            if (this.somethingSpeedThing > 0.0D) {
                this.somethingSpeedThing -= 1.0E-4D;
            }

            this.speedX[var1] += this.somethingSpeedThing * (double) var6 * 5.0E-4D;
            this.speedY[var1] += this.somethingSpeedThing * (double) var7 * 5.0E-4D;
            return true;
        }
    }

    private double calculateFriction(int var1, double var2) {
        double var4 = this.getFriction(var1);
        double var6 = 0.75D * var2 / 6.5D;
        double var8 = 1.0D - var4;
        return var4 + var8 * var6;
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

    private void resetposition(int var1, boolean var2) {
        if (this.resetPositionX[var1] >= 0.0D && this.resetPositionX[var1] >= 0.0D) {
            this.playerX[var1] = this.resetPositionX[var1];
            this.playerY[var1] = this.resetPositionY[var1];
        } else if (this.startPositionX >= 0.0D && this.startPositionY >= 0.0D) {
            this.playerX[var1] = this.startPositionX;
            this.playerY[var1] = this.startPositionY;
            if (var2) {
                this.playerActive[var1] = false;
            }

        } else {
            this.playerX[var1] = 367.5D;
            this.playerY[var1] = 187.5D;
        }
    }

    private void adjustPhysicsIterations(int var1) {
        frameTimeHistory[0] = frameTimeHistory[1];
        frameTimeHistory[1] = frameTimeHistory[2];
        if (frameTimeHistory[1] < var1) {
            var1 = frameTimeHistory[1];
        }

        if (frameTimeHistory[0] < var1) {
            var1 = frameTimeHistory[0];
        }

        while (var1 > 700 && this.maxPhysicsIterations > 1) {
            var1 -= 700;
            --this.maxPhysicsIterations;
        }

        while (var1 < -2000 && this.maxPhysicsIterations < 6) {
            var1 += 2000;
            ++this.maxPhysicsIterations;
        }

        frameTimeHistory[2] = var1;
    }
}
