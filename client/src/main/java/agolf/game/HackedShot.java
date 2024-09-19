package agolf.game;

import agolf.Seed;
import agolf.SynchronizedBool;
import com.aapeli.tools.Tools;

import java.util.Arrays;
import java.util.List;

public class HackedShot implements Runnable {

    private static final double aDouble2798 = Math.sqrt(2.0D) / 2.0D;
    private static final int anInt2799 = (int) (6.0D * aDouble2798 + 0.5D);
    private static int[] anIntArray2837 = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
    private static int anInt2838 = 2;

    private int anInt2809;
    private int anInt2810;
    private int anInt2811;
    private int currentPlayerID;
    private int anInt2816;
    private double aDouble2817;
    private double aDouble2818;
    private double aDouble2819;
    private double aDouble2820;
    private double[] aDoubleArray2821;
    private double[] aDoubleArray2822;
    private List<double[]>[] teleportStarts;
    private List<double[]>[] teleportExits;
    private short[][][] aShortArrayArrayArray2825;
    private double[] playerX;
    private double[] playerY;
    private double[] aDoubleArray2828;
    private double[] aDoubleArray2829;
    private boolean[] aBooleanArray2830;
    private SynchronizedBool[] aSynchronizedBoolArray2831;
    private boolean aBoolean2832;
    private boolean[] aBooleanArray2834;
    private Seed aSeed_2836;
    private int anInt2839;
    private boolean aBoolean2843;

    // from superclass
    protected byte[][] isSolidArrayIThink;
    protected int[][] mapTiles;


    // this actually scares me, but its for science.
    public HackedShot(int anInt2809,
                      int anInt2810,
                      int anInt2811,
                      int currentPlayerID,
                      int anInt2816,
                      double aDouble2817,
                      double aDouble2818,
                      double aDouble2819,
                      double aDouble2820,
                      double[] aDoubleArray2821,
                      double[] aDoubleArray2822,
                      List<double[]>[] teleportStarts,
                      List<double[]>[] teleportExits,
                      short[][][] aShortArrayArrayArray2825,
                      double[] playerX,
                      double[] playerY,
                      double[] aDoubleArray2828,
                      double[] aDoubleArray2829,
                      boolean[] aBooleanArray2830,
                      SynchronizedBool[] aSynchronizedBoolArray2831,
                      boolean aBoolean2832,
                      boolean[] aBooleanArray2834,
                      Seed aSeed_2836,
                      int anInt2839,
                      boolean aBoolean2843,
                      byte[][] isSolidArrayIThink,
                      int[][] mapTiles) {

        this.anInt2809 = anInt2809;
        this.anInt2810 = anInt2810;
        this.anInt2811 = anInt2811;
        this.currentPlayerID = currentPlayerID;
        this.anInt2816 = anInt2816;
        this.aDouble2817 = aDouble2817;
        this.aDouble2818 = aDouble2818;
        this.aDouble2819 = aDouble2819;
        this.aDouble2820 = aDouble2820;
        this.aDoubleArray2821 = Arrays.copyOf(aDoubleArray2821, aDoubleArray2821.length);
        this.aDoubleArray2822 = Arrays.copyOf(aDoubleArray2822, aDoubleArray2822.length);
        this.teleportStarts = Arrays.copyOf(teleportStarts, teleportStarts.length);
        this.teleportExits = Arrays.copyOf(teleportExits, teleportExits.length);
        if (aShortArrayArrayArray2825 != null) {
            this.aShortArrayArrayArray2825 = Arrays.copyOf(aShortArrayArrayArray2825, aShortArrayArrayArray2825.length);
        } else {
            this.aShortArrayArrayArray2825 = null;
        }
        this.playerX = Arrays.copyOf(playerX, playerX.length);
        this.playerY = Arrays.copyOf(playerY, playerY.length);
        this.aDoubleArray2828 = Arrays.copyOf(aDoubleArray2828, aDoubleArray2828.length);
        this.aDoubleArray2829 = Arrays.copyOf(aDoubleArray2829, aDoubleArray2829.length);
        this.aBooleanArray2830 = Arrays.copyOf(aBooleanArray2830, aBooleanArray2830.length);
        this.aSynchronizedBoolArray2831 = Arrays.copyOf(aSynchronizedBoolArray2831, aSynchronizedBoolArray2831.length); //unsure bout thsione
        this.aBoolean2832 = aBoolean2832;
        this.aBooleanArray2834 = Arrays.copyOf(aBooleanArray2834, aBooleanArray2834.length);
        this.aSeed_2836 = aSeed_2836.clone();
        this.anInt2839 = anInt2839;
        this.aBoolean2843 = aBoolean2843;
        this.isSolidArrayIThink = Arrays.copyOf(isSolidArrayIThink, isSolidArrayIThink.length);
        this.mapTiles =  Arrays.copyOf(mapTiles , mapTiles .length);


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
        int[] var6 = new int[this.anInt2809];
        int[] var7 = new int[this.anInt2809];
        double[] var8 = new double[this.anInt2809];
        double[] var9 = new double[this.anInt2809];
        double[] var10 = new double[this.anInt2809];
        double[] var11 = new double[this.anInt2809];
        double[] var12 = new double[this.anInt2809];
        double[] var13 = new double[this.anInt2809];
        double[] var14 = new double[this.anInt2809];
        boolean[] var15 = new boolean[this.anInt2809];
        boolean[] var16 = new boolean[this.anInt2809];
        boolean[] var17 = new boolean[this.anInt2809];
        int[] var18 = new int[this.anInt2809];

        for (int var19 = 0; var19 < this.anInt2809; ++var19) {
            var6[var19] = var7[var19] = 0;
            var8[var19] = var11[var19] = this.playerX[var19];
            var9[var19] = var12[var19] = this.playerY[var19];
            var15[var19] = var16[var19] = false;
            var10[var19] = this.aSynchronizedBoolArray2831[var19].get() ? 2.1666666666666665D : 0.0D;
            var17[var19] = false;
            var18[var19] = 0;
        }

        boolean var20 = false;
        boolean var21 = false;
        boolean var22 = true;
        boolean var23 = true;

        int var24 = -1;
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
        this.aDouble2819 = this.aDouble2820 = 1.0D;
        int var38 = 0;

        do {
            long var39 = System.currentTimeMillis();

            for (int var41 = 0; var41 < this.anInt2809; ++var41) {
                var13[var41] = this.playerX[var41];
                var14[var41] = this.playerY[var41];
            }

            int var43;
            int var44;
            for (int var42 = 0; var42 < this.anInt2839; ++var42) {
                var24 = 0;

                for (var43 = 0; var43 < this.anInt2809; ++var43) {
                    if (this.aBooleanArray2830[var43] && !this.aSynchronizedBoolArray2831[var43].get()) {
                        for (var44 = 0; var44 < 10; ++var44) {
                            this.playerX[var43] += this.aDoubleArray2828[var43] * 0.1D;
                            this.playerY[var43] += this.aDoubleArray2829[var43] * 0.1D;
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
                            if (this.anInt2811 == 1 && !var15[var43] && !var16[var43]) {
                                for (var45 = 0; var45 < this.anInt2809; ++var45) {
                                    if (var43 != var45 && this.aBooleanArray2830[var45] && !this.aSynchronizedBoolArray2831[var45].get() && !var15[var45] && !var16[var45] && this.method147(var43, var45)) {
                                        this.aDoubleArray2828[var43] *= 0.75D;
                                        this.aDoubleArray2829[var43] *= 0.75D;
                                        this.aDoubleArray2828[var45] *= 0.75D;
                                        this.aDoubleArray2829[var45] *= 0.75D;
                                        var24 = 0;
                                    }
                                }
                            }

                            var35 = (int) (this.playerX[var43] + 0.5D);
                            var34 = (int) (this.playerY[var43] + 0.5D);
                            var33 = isSolidArrayIThink[var35][var34];
                            var32 = isSolidArrayIThink[var35][var34 - 6];
                            var31 = isSolidArrayIThink[var35 + anInt2799][var34 - anInt2799];
                            var30 = isSolidArrayIThink[var35 + 6][var34];
                            var29 = isSolidArrayIThink[var35 + anInt2799][var34 + anInt2799];
                            var28 = isSolidArrayIThink[var35][var34 + 6];
                            var27 = isSolidArrayIThink[var35 - anInt2799][var34 + anInt2799];
                            var26 = isSolidArrayIThink[var35 - 6][var34];
                            var25 = isSolidArrayIThink[var35 - anInt2799][var34 - anInt2799];
                            if (var33 != 12 && var33 != 13) {
                                var21 = var33 == 14 || var33 == 15;
                            } else {
                                this.aDoubleArray2828[var43] *= 0.97D;
                                this.aDoubleArray2829[var43] *= 0.97D;
                                var21 = true;
                            }

                            var45 = 0;

                            for (int var46 = 32; var46 <= 38; var46 += 2) {
                                if (var32 == var46 || var31 == var46 || var30 == var46 || var29 == var46 || var28 == var46 || var27 == var46 || var26 == var46 || var25 == var46) {
                                    ++var45;
                                    if (!var17[var43]) {
                                        this.method154((var46 - 32) / 2, var43, var35, var34);
                                        var17[var43] = true;
                                    }
                                }
                            }

                            if (var45 == 0) {
                                var17[var43] = false;
                            }

                            if (var33 == 28 || var33 == 30) {
                                this.method155(var33 == 30, var43, var35, var34);
                            }

                            this.method152(var43, var32, var31, var30, var29, var28, var27, var26, var25, var35, var34);
                        }

                        boolean var47 = this.method148(var43, var33);
                        boolean var48 = false;
                        if (this.aShortArrayArrayArray2825 != null && !var21 && !var15[var43] && !var16[var43]) {
                            var48 = this.method149(var43, var35, var34);
                        }

                        var20 = false;
                        double var49;
                        if (var33 == 25 || isSolidArrayIThink[var35][var34 - 1] == 25 || isSolidArrayIThink[var35 + 1][var34] == 25 || isSolidArrayIThink[var35][var34 + 1] == 25 || isSolidArrayIThink[var35 - 1][var34] == 25) {
                            var49 = var33 == 25 ? 1.0D : 0.5D;
                            var20 = true;
                            int var51 = 0;
                            if (var32 == 25) {
                                ++var51;
                            } else {
                                this.aDoubleArray2829[var43] += var49 * 0.03D;
                            }

                            if (var31 == 25) {
                                ++var51;
                            } else {
                                this.aDoubleArray2829[var43] += var49 * 0.03D * aDouble2798;
                                this.aDoubleArray2828[var43] -= var49 * 0.03D * aDouble2798;
                            }

                            if (var30 == 25) {
                                ++var51;
                            } else {
                                this.aDoubleArray2828[var43] -= var49 * 0.03D;
                            }

                            if (var29 == 25) {
                                ++var51;
                            } else {
                                this.aDoubleArray2829[var43] -= var49 * 0.03D * aDouble2798;
                                this.aDoubleArray2828[var43] -= var49 * 0.03D * aDouble2798;
                            }

                            if (var28 == 25) {
                                ++var51;
                            } else {
                                this.aDoubleArray2829[var43] -= var49 * 0.03D;
                            }

                            if (var27 == 25) {
                                ++var51;
                            } else {
                                this.aDoubleArray2829[var43] -= var49 * 0.03D * aDouble2798;
                                this.aDoubleArray2828[var43] += var49 * 0.03D * aDouble2798;
                            }

                            if (var26 == 25) {
                                ++var51;
                            } else {
                                this.aDoubleArray2828[var43] += var49 * 0.03D;
                            }

                            if (var25 == 25) {
                                ++var51;
                            } else {
                                this.aDoubleArray2829[var43] += var49 * 0.03D * aDouble2798;
                                this.aDoubleArray2828[var43] += var49 * 0.03D * aDouble2798;
                            }

                            if (var51 >= 7) {
                                var20 = false;
                                var15[var43] = true;
                                this.aDoubleArray2828[var43] = this.aDoubleArray2829[var43] = 0.0D;
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

                        var36 = Math.sqrt(this.aDoubleArray2828[var43] * this.aDoubleArray2828[var43] + this.aDoubleArray2829[var43] * this.aDoubleArray2829[var43]);
                        if (var36 > 0.0D) {
                            double var52 = this.method150(var33, var36);
                            this.aDoubleArray2828[var43] *= var52;
                            this.aDoubleArray2829[var43] *= var52;
                            var36 *= var52;
                            if (var36 > 7.0D) {
                                var49 = 7.0D / var36;
                                this.aDoubleArray2828[var43] *= var49;
                                this.aDoubleArray2829[var43] *= var49;
                                var36 *= var49;
                            }
                        }

                        if (var5 > 4000) {
                            this.aDouble2819 = 0.0D;
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
                            this.aDoubleArray2828[var43] = this.aDoubleArray2829[var43] = 0.0D;
                            if (var33 != 12 && var33 != 14 && var33 != 13 && var33 != 15) {
                                ++var24;
                            } else {
                                var16[var43] = true;
                            }
                        }

                        if (var15[var43] || var16[var43]) {
                            var10[var43] += 0.1D;
                            if (var15[var43] && var10[var43] > 2.1666666666666665D || var16[var43] && var10[var43] > 6.0D) {
                                if (var33 == 25) {
                                    this.aSynchronizedBoolArray2831[var43].set(true);
                                    if (this.aBoolean2832 && this.anInt2809 > 1) {
                                        //gameContainer.gamePanel.hideSkipButton();
                                    }
                                } else {
                                    if (var33 == 12 || var33 == 14) {
                                        this.playerX[var43] = this.anInt2810 == 0 ? var8[var43] : var11[var43];
                                        this.playerY[var43] = this.anInt2810 == 0 ? var9[var43] : var12[var43];
                                    }

                                    if (var33 == 13 || var33 == 15) {
                                        this.method145(var43, false);
                                    }

                                    var10[var43] = 0.0D;
                                }

                                var15[var43] = var16[var43] = false;
                                ++var24;
                            }
                        }
                    } else {
                        ++var24;
                    }
                }

                ++var5;
                if (var24 >= this.anInt2809) {
                    var42 = this.anInt2839;
                }
            }

            for (var43 = 0; var43 < this.anInt2809; ++var43) {
                if (this.aBooleanArray2830[var43]) {
                    int var54 = (int) (var13[var43] - 6.5D + 0.5D);
                    int var55 = (int) (var14[var43] - 6.5D + 0.5D);
                    int var56 = var54 + 13;
                    int var57 = var55 + 13;
                    //var3.drawImage(var2, var54, var55, var56, var57, var54, var55, var56, var57, this);

                    for (var44 = 0; var44 < this.anInt2809; ++var44) {
                        if (this.aBooleanArray2830[var44] && var44 != this.currentPlayerID) {
                            //this.method161(var3, var44, var10[var44]);
                        }
                    }

                    //this.method161(var3, this.currentPlayerID, var10[this.currentPlayerID]);
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

                    // var4.drawImage(var1, var54, var55, var56, var57, var54, var55, var56, var57, this);
                }
            }

            var39 = System.currentTimeMillis() - var39;

            long var58 = 0L; // dont sleep


            Tools.sleep(var58);
            var38 = (int) ((long) var38 + var58);
        } while (var24 < this.anInt2809 && !this.aBoolean2843);

        if (this.aBoolean2843) {
            // this.aThread2842 = null;
        } else {
            this.method164(var38);
            this.aSynchronizedBoolArray2831[this.anInt2816].set(false); // FUCKING IMPORTANT OR IT BORKS

            //gameContainer.gamePanel.sendEndStroke(this.currentPlayerID, this.aSynchronizedBoolArray2831, this.anInt2816);
            /*
            if (this.anInt2816 >= 0) {
                this.aSynchronizedBoolArray2831[this.anInt2816].set(true);
            }
            */

            //System.out.println("hacked="+playerX[currentPlayerID]+","+playerY[currentPlayerID]);
            //this.aThread2842 = null;
            // this.repaint();
        }
    }

    public double[] getHackedCoordintes() {
        double[] result =  new double[] {playerX[currentPlayerID],playerY[currentPlayerID]};
        return result;
    }

    private void method154(int var1, int var2, int var3, int var4) {
        boolean var5 = true;
        int var6 = this.teleportExits[var1].size();
        int var7;
        int var8;
        double[] var11;
        int var13;
        if (var6 > 0) {
            var13 = var1;
            var7 = var6 - 1;
            var8 = this.aSeed_2836.next() % (var7 + 1);
        } else {
            var7 = this.teleportStarts[var1].size();
            int var10;
            if (var7 >= 2) {
                int var14 = 0;

                do {
                    var10 = var7 - 1;
                    var8 = this.aSeed_2836.next() % (var10 + 1);
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
                var13 = this.aSeed_2836.next() % 4;
                var6 = this.teleportExits[var13].size();
            } while (var6 == 0);

            int var12 = var6 - 1;
            var8 = this.aSeed_2836.next() % (var12 + 1);
        }

        var11 = this.teleportExits[var13].get(var8);
        this.playerX[var2] = var11[0];
        this.playerY[var2] = var11[1];
    }

    private boolean method147(int var1, int var2) {
        double var3 = this.playerX[var2] - this.playerX[var1];
        double var5 = this.playerY[var2] - this.playerY[var1];
        double var7 = Math.sqrt(var3 * var3 + var5 * var5);
        if (var7 != 0.0D && var7 <= 13.0D) {
            double var9 = var3 / var7;
            double var11 = var5 / var7;
            double var13 = this.aDoubleArray2828[var1] * var9 + this.aDoubleArray2829[var1] * var11;
            double var15 = this.aDoubleArray2828[var2] * var9 + this.aDoubleArray2829[var2] * var11;
            if (var13 - var15 <= 0.0D) {
                return false;
            } else {
                double var17 = -this.aDoubleArray2828[var1] * var11 + this.aDoubleArray2829[var1] * var9;
                double var19 = -this.aDoubleArray2828[var2] * var11 + this.aDoubleArray2829[var2] * var9;
                this.aDoubleArray2828[var1] = var15 * var9 - var17 * var11;
                this.aDoubleArray2829[var1] = var15 * var11 + var17 * var9;
                this.aDoubleArray2828[var2] = var13 * var9 - var19 * var11;
                this.aDoubleArray2829[var2] = var13 * var11 + var19 * var9;
                return true;
            }
        } else {
            return false;
        }
    }

    private void method155(boolean var1, int var2, int var3, int var4) {
        int var7 = var3 / 15;
        int var8 = var4 / 15;
        int var9 = mapTiles[var7][var8] / 16777216;
        int var10 = mapTiles[var7][var8] / 65536 % 256 + 24;
        int var11 = mapTiles[var7][var8] / 256 % 256;
        int var12 = mapTiles[var7][var8] % 256;
        if (var9 == 2 && (var10 == 28 || var10 == 30)) {
            ++var10;
            mapTiles[var7][var8] = var9 * 256 * 256 * 256 + (var10 - 24) * 256 * 256 + var11 * 256 + var12;
            //this.drawTile(var7, var8, var5, var6);
            if (var1) {
                int[] var13 = new int[]{17039367, 16779264, 17104905, 16778752, -1, 16779776, 17235973, 16778240, 17170443};
                int var14 = 0;

                for (int var15 = var8 - 1; var15 <= var8 + 1; ++var15) {
                    for (int var16 = var7 - 1; var16 <= var7 + 1; ++var16) {
                        if (var16 >= 0 && var16 < 49 && var15 >= 0 && var15 < 25 && (var15 != var8 || var16 != var7) && mapTiles[var16][var15] == 16777216) {
                            mapTiles[var16][var15] = var13[var14];
                            //this.drawTile(var16, var15, var5, var6);
                        }

                        ++var14;
                    }
                }
            }

            double var17;
            do {
                do {
                    this.aDoubleArray2828[var2] = (double) (-65 + this.aSeed_2836.next() % 131) / 10.0D;
                    this.aDoubleArray2829[var2] = (double) (-65 + this.aSeed_2836.next() % 131) / 10.0D;
                    var17 = Math.sqrt(this.aDoubleArray2828[var2] * this.aDoubleArray2828[var2] + this.aDoubleArray2829[var2] * this.aDoubleArray2829[var2]);
                } while (var17 < 5.2D);
            } while (var17 > 6.5D);

            if (!var1) {
                this.aDoubleArray2828[var2] *= 0.8D;
                this.aDoubleArray2829[var2] *= 0.8D;
            }

        }
    }

    private void method152(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11) {
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

        if (var14 && var15 && var16 && (var2 < 20 || var2 > 23) && (var3 < 20 || var3 > 23) && (var4 < 20 || var4 > 23)) {
            var16 = false;
            var14 = false;
        }

        if (var16 && var17 && var18 && (var4 < 20 || var4 > 23) && (var5 < 20 || var5 > 23) && (var6 < 20 || var6 > 23)) {
            var18 = false;
            var16 = false;
        }

        if (var18 && var19 && var20 && (var6 < 20 || var6 > 23) && (var7 < 20 || var7 > 23) && (var8 < 20 || var8 > 23)) {
            var20 = false;
            var18 = false;
        }

        if (var20 && var21 && var14 && (var8 < 20 || var8 > 23) && (var9 < 20 || var9 > 23) && (var2 < 20 || var2 > 23)) {
            var14 = false;
            var20 = false;
        }

        double var22;
        if (!var14 && !var16 && !var18 && !var20) {
            double var24;
            if (var15 && (this.aDoubleArray2828[var1] > 0.0D && this.aDoubleArray2829[var1] < 0.0D || this.aDoubleArray2828[var1] < 0.0D && this.aDoubleArray2829[var1] < 0.0D && -this.aDoubleArray2829[var1] > -this.aDoubleArray2828[var1] || this.aDoubleArray2828[var1] > 0.0D && this.aDoubleArray2829[var1] > 0.0D && this.aDoubleArray2828[var1] > this.aDoubleArray2829[var1])) {
                var22 = this.method153(var3, var1, var10 + anInt2799, var11 - anInt2799, 1, -1);
                var24 = this.aDoubleArray2828[var1];
                this.aDoubleArray2828[var1] = this.aDoubleArray2829[var1] * var22;
                this.aDoubleArray2829[var1] = var24 * var22;
            }

            if (var17 && (this.aDoubleArray2828[var1] > 0.0D && this.aDoubleArray2829[var1] > 0.0D || this.aDoubleArray2828[var1] > 0.0D && this.aDoubleArray2829[var1] < 0.0D && this.aDoubleArray2828[var1] > -this.aDoubleArray2829[var1] || this.aDoubleArray2828[var1] < 0.0D && this.aDoubleArray2829[var1] > 0.0D && this.aDoubleArray2829[var1] > -this.aDoubleArray2828[var1])) {
                var22 = this.method153(var5, var1, var10 + anInt2799, var11 + anInt2799, 1, 1);
                var24 = this.aDoubleArray2828[var1];
                this.aDoubleArray2828[var1] = -this.aDoubleArray2829[var1] * var22;
                this.aDoubleArray2829[var1] = -var24 * var22;
            }

            if (var19 && (this.aDoubleArray2828[var1] < 0.0D && this.aDoubleArray2829[var1] > 0.0D || this.aDoubleArray2828[var1] > 0.0D && this.aDoubleArray2829[var1] > 0.0D && this.aDoubleArray2829[var1] > this.aDoubleArray2828[var1] || this.aDoubleArray2828[var1] < 0.0D && this.aDoubleArray2829[var1] < 0.0D && -this.aDoubleArray2828[var1] > -this.aDoubleArray2829[var1])) {
                var22 = this.method153(var7, var1, var10 - anInt2799, var11 + anInt2799, -1, 1);
                var24 = this.aDoubleArray2828[var1];
                this.aDoubleArray2828[var1] = this.aDoubleArray2829[var1] * var22;
                this.aDoubleArray2829[var1] = var24 * var22;
            }

            if (var21 && (this.aDoubleArray2828[var1] < 0.0D && this.aDoubleArray2829[var1] < 0.0D || this.aDoubleArray2828[var1] < 0.0D && this.aDoubleArray2829[var1] > 0.0D && -this.aDoubleArray2828[var1] > this.aDoubleArray2829[var1] || this.aDoubleArray2828[var1] > 0.0D && this.aDoubleArray2829[var1] < 0.0D && -this.aDoubleArray2829[var1] > this.aDoubleArray2828[var1])) {
                var22 = this.method153(var9, var1, var10 - anInt2799, var11 - anInt2799, -1, -1);
                var24 = this.aDoubleArray2828[var1];
                this.aDoubleArray2828[var1] = -this.aDoubleArray2829[var1] * var22;
                this.aDoubleArray2829[var1] = -var24 * var22;
            }
        } else {
            if (var14 && this.aDoubleArray2829[var1] < 0.0D) {
                var22 = this.method153(var2, var1, var10, var11 - 6, 0, -1);
                this.aDoubleArray2828[var1] *= var22;
                this.aDoubleArray2829[var1] *= -var22;
            } else if (var18 && this.aDoubleArray2829[var1] > 0.0D) {
                var22 = this.method153(var6, var1, var10, var11 + 6, 0, 1);
                this.aDoubleArray2828[var1] *= var22;
                this.aDoubleArray2829[var1] *= -var22;
            }

            if (var16 && this.aDoubleArray2828[var1] > 0.0D) {
                var22 = this.method153(var4, var1, var10 + 6, var11, 1, 0);
                this.aDoubleArray2828[var1] *= -var22;
                this.aDoubleArray2829[var1] *= var22;
                return;
            }

            if (var20 && this.aDoubleArray2828[var1] < 0.0D) {
                var22 = this.method153(var8, var1, var10 - 6, var11, -1, 0);
                this.aDoubleArray2828[var1] *= -var22;
                this.aDoubleArray2829[var1] *= var22;
                return;
            }
        }

    }

    private double method153(int var1, int var2, int var3, int var4, int var7, int var8) {
        if (var1 == 16) {
            return 0.81D;
        } else if (var1 == 17) {
            return 0.05D;
        } else if (var1 == 18) {
            if (this.aDouble2819 <= 0.0D) {
                return 0.84D;
            } else {
                this.aDouble2819 -= 0.01D;
                double var9 = Math.sqrt(this.aDoubleArray2828[var2] * this.aDoubleArray2828[var2] + this.aDoubleArray2829[var2] * this.aDoubleArray2829[var2]);
                return this.aDouble2819 * 6.5D / var9;
            }
        } else if (var1 != 20 && var1 != 21 && var1 != 22 && var1 != 23) {
            if (var1 != 27 && var1 != 46) {
                if (var1 != 40 && var1 != 41 && var1 != 42 && var1 != 43) {
                    return 1.0D;
                } else {
                    this.method159(var3, var4);
                    return 0.9D;
                }
            } else {
                return this.method156(var3, var4, var7, var8, var1 == 27) ? 0.325D : 0.8D;
            }
        } else {
            return 0.82D;
        }
    }

    private boolean method156(int var1, int var2, int var5, int var6, boolean var7) {
        int var8 = var1 / 15;
        int var9 = var2 / 15;
        int var10 = mapTiles[var8][var9] / 16777216;
        int var11 = mapTiles[var8][var9] / 65536 % 256 + 24;
        int var12 = mapTiles[var8][var9] / 256 % 256;
        if (var10 == 2 && (var11 == 27 || var11 == 46)) {
            int var13 = var8 + var5;
            int var14 = var9 + var6;
            int var15 = this.method157(var13, var14);
            if (var15 == -1) {
                return false;
            } else {
                mapTiles[var8][var9] = 16777216 + var12 * 256;
                //this.drawTile(var8, var9, var3, var4);
                int[] var16 = this.method158(var8, var9, var13, var14, var12, var15, var7, 0);
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

    private int method157(int var1, int var2) {
        if (var1 >= 0 && var1 < 49 && var2 >= 0 && var2 < 25) {
            int var3 = mapTiles[var1][var2] / 16777216;
            int var4 = mapTiles[var1][var2] / 65536 % 256;
            int var5 = mapTiles[var1][var2] / 256 % 256;
            if (var3 == 1 && var4 == 0 && var5 <= 15) {
                for (int var6 = 0; var6 < this.anInt2809; ++var6) {
                    if (this.playerX[var6] > (double) (var1 * 15) && this.playerX[var6] < (double) (var1 * 15 + 15 - 1) && this.playerY[var6] > (double) (var2 * 15) && this.playerY[var6] < (double) (var2 * 15 + 15 - 1)) {
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

    private int[] method158(int var1, int var2, int var3, int var4, int var5, int var6, boolean var9, int var10) {
        int[] var11 = new int[]{var3, var4, var6};
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

            var6 = this.method157(var3, var4);
            if (var6 >= 0) {
                var11 = this.method158(var1, var2, var3, var4, var5, var6, var9, var10 + 1);
            }
        }

        return var11;
    }

    private void method159(int var1, int var2) {
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

            //this.drawTile(var5, var6, var3, var4);
        }
    }

    private boolean method148(int var1, int var2) {
        if (var2 >= 4 && var2 <= 11) {
            if (var2 == 4) {
                this.aDoubleArray2829[var1] -= 0.025D;
            }

            if (var2 == 5) {
                this.aDoubleArray2829[var1] -= 0.025D * aDouble2798;
                this.aDoubleArray2828[var1] += 0.025D * aDouble2798;
            }

            if (var2 == 6) {
                this.aDoubleArray2828[var1] += 0.025D;
            }

            if (var2 == 7) {
                this.aDoubleArray2829[var1] += 0.025D * aDouble2798;
                this.aDoubleArray2828[var1] += 0.025D * aDouble2798;
            }

            if (var2 == 8) {
                this.aDoubleArray2829[var1] += 0.025D;
            }

            if (var2 == 9) {
                this.aDoubleArray2829[var1] += 0.025D * aDouble2798;
                this.aDoubleArray2828[var1] -= 0.025D * aDouble2798;
            }

            if (var2 == 10) {
                this.aDoubleArray2828[var1] -= 0.025D;
            }

            if (var2 == 11) {
                this.aDoubleArray2829[var1] -= 0.025D * aDouble2798;
                this.aDoubleArray2828[var1] -= 0.025D * aDouble2798;
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean method149(int var1, int var2, int var3) {
        int var4 = var2 / 5;
        int var5 = var3 / 5;
        short var6 = this.aShortArrayArrayArray2825[var4][var5][0];
        short var7 = this.aShortArrayArrayArray2825[var4][var5][1];
        if (var6 == 0 && var7 == 0) {
            return false;
        } else {
            if (this.aDouble2820 > 0.0D) {
                this.aDouble2820 -= 1.0E-4D;
            }

            this.aDoubleArray2828[var1] += this.aDouble2820 * (double) var6 * 5.0E-4D;
            this.aDoubleArray2829[var1] += this.aDouble2820 * (double) var7 * 5.0E-4D;
            return true;
        }
    }

    private double method150(int var1, double var2) {
        double var4 = this.method151(var1);
        double var6 = 0.75D * var2 / 6.5D;
        double var8 = 1.0D - var4;
        return var4 + var8 * var6;
    }

    private double method151(int var1) {
        return var1 != 0 && (var1 < 4 || var1 > 11) && var1 != 19 && var1 != 47 ? (var1 == 1 ? 0.92D : (var1 == 2 ? 0.8D : (var1 != 3 && var1 != 32 && var1 != 34 && var1 != 36 && var1 != 38 ? (var1 != 12 && var1 != 13 ? (var1 != 14 && var1 != 15 ? (var1 >= 20 && var1 <= 23 ? 0.995D : (var1 == 25 ? 0.96D : (var1 != 28 && var1 != 30 ? (var1 != 29 && var1 != 31 ? (var1 == 44 ? 0.9D : 1.0D) : 0.9D) : 1.0D))) : 0.95D) : 0.0D) : 0.9975D))) : 0.9935D;
    }

    private void method145(int var1, boolean var2) {
        if (this.aDoubleArray2821[var1] >= 0.0D && this.aDoubleArray2821[var1] >= 0.0D) {
            this.playerX[var1] = this.aDoubleArray2821[var1];
            this.playerY[var1] = this.aDoubleArray2822[var1];
        } else if (this.aDouble2817 >= 0.0D && this.aDouble2818 >= 0.0D) {
            this.playerX[var1] = this.aDouble2817;
            this.playerY[var1] = this.aDouble2818;
            if (var2) {
                this.aBooleanArray2834[var1] = false;
            }

        } else {
            this.playerX[var1] = 367.5D;
            this.playerY[var1] = 187.5D;
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
