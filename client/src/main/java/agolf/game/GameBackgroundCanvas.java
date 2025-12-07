package agolf.game;

import agolf.GameContainer;
import agolf.GolfGameFrame;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

public class GameBackgroundCanvas extends Canvas {

    protected static final Color aColor75 = new Color(240, 240, 255);
    public static final int[] anIntArray78 = new int[] {3, 5, 8, 49};
    public static final int[] anIntArray79 = new int[] {2, 3, 5, 25};
    protected GameContainer gameContainer;
    private Image backgroundImg;
    protected Image image;
    private Graphics graphics;
    public Track track;
    private int trackWidth = 735;
    private int trackHeight = 375;

    protected GameBackgroundCanvas(GameContainer gameContainer, Image backgroundImage) {
        this.gameContainer = gameContainer;
        this.backgroundImg = backgroundImage;
        this.setSize(this.trackWidth, this.trackHeight);
        Map map = new Map(trackWidth, trackHeight);
        this.track = new Track(map);
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
            g.setColor(GolfGameFrame.colourGameBackground);
            g.fillRect(0, 0, this.trackWidth, this.trackHeight);
        } else {
            g.drawImage(this.image, 0, 0, this);
        }
    }

    // this useless func is called when we get start packet
    // iniatilize variables
    // draws map as grass
    // this.gameCanvas.createMap(16777216);
    // 16777216 == grass
    protected void createMap(int tile) {
        if (this.image == null) {
            this.image = this.createImage(this.trackWidth, this.trackHeight);
            if (this.image == null) {
                this.image = this.backgroundImg;
            }

            this.graphics = this.image.getGraphics();
        }

        Image var2 = this.gameContainer.imageManager.createImage(
                this.gameContainer.spriteManager.getPixelsFromTileCode(tile), 15, 15);
        this.graphics.setColor(aColor75);

        for (int y = 0; y < 25; ++y) {
            for (int x = 0; x < 49; ++x) {

                this.track.map.setTile(x, y, tile);
                if (tile == 0) {
                    this.graphics.fillRect(x * 15, y * 15, 15, 15);
                } else {
                    this.graphics.drawImage(var2, x * 15, y * 15, this);
                }
            }
        }

        this.repaint();
    }

    protected Image getTileImageAt(int tileX, int tileY) {
        int[] imageData = this.gameContainer.spriteManager.getPixelsFromTileCode(this.track.map.getTile(tileX, tileY));
        if (this.gameContainer.graphicsQualityIndex >= 2) {
            for (int x = 0; x < 15; ++x) {
                for (int y = 0; y < 15; ++y) {
                    for (int z = 1; z <= 7 && tileX * 15 + y - z > 0 && tileY * 15 + x - z > 0; ++z) {
                        if (this.castsShadow(tileX * 15 + y - z, tileY * 15 + x - z)) {
                            this.shiftPixel(imageData, y, x, -8, 15);
                        }
                    }

                    this.shiftPixel(imageData, y, x, (int) (Math.random() * 11.0D) - 5, 15);
                }
            }
        }

        Image resultImage = this.gameContainer.imageManager.createImage(imageData, 15, 15);
        this.graphics.drawImage(resultImage, tileX * 15, tileY * 15, this);
        return resultImage;
    }

    public void drawMap() {
        int[] mapPixels = new int[275625];
        int[] currentTileImageData = null;
        int oldTile = -1;
        boolean trackTestMode =
                this.gameContainer.synchronizedTrackTestMode.get(); // controls when to draw starting positions

        int currentTile;
        int yPixels;
        int xPixels;
        int var13;
        Map map = this.track.map;
        for (int tileY = 0; tileY < 25; ++tileY) {
            for (int tileX = 0; tileX < 49; ++tileX) {

                if (map.getTile(tileX, tileY) != oldTile) {
                    currentTile = map.getTile(tileX, tileY);

                    int specialStatus = currentTile / 16777216;

                    int currentTileSpecialId = currentTile / 65536 % 256 + 24;
                    int background = currentTile / 256 % 256;

                    if (specialStatus == 2) {
                        // 16777216 == blank tile with grass
                        // 34144256 == teleport blue exit with grass
                        // 34078720 == teleport start with grass

                        // 0:false => mines invisible  0:true => mines visible

                        if (!this.track.trackSpecialSettings[0]
                                && (currentTileSpecialId == 28 || currentTileSpecialId == 30)) {
                            currentTile = 16777216 + background * 256;
                        }

                        // 1:false => magnets invisible  1:true => magnets visible

                        if (!this.track.trackSpecialSettings[1]
                                && (currentTileSpecialId == 44 || currentTileSpecialId == 45)) {
                            currentTile = 16777216 + background * 256;
                        }

                        // 2:false => teleport colorless  2:true => normal colors
                        if (!this.track.trackSpecialSettings[2]) {
                            if (currentTileSpecialId == 34
                                    || currentTileSpecialId == 36
                                    || currentTileSpecialId == 38) {
                                currentTile = 34078720 + background * 256;
                            }

                            if (currentTileSpecialId == 35
                                    || currentTileSpecialId == 37
                                    || currentTileSpecialId == 39) {
                                currentTile = 34144256 + background * 256;
                            }
                        }
                    }

                    currentTileImageData = this.gameContainer.spriteManager.getPixelsFromTileCode(currentTile);

                    oldTile = map.getTile(tileX, tileY);

                    // draws debug points on starting positions
                    if (trackTestMode && specialStatus == 2) {
                        yPixels = -1;
                        // Starting Point common
                        if (currentTileSpecialId == 24) {
                            yPixels = 16777215;
                        }

                        // Start blue
                        if (currentTileSpecialId == 48) {
                            yPixels = 11579647;
                        }
                        // Start red
                        if (currentTileSpecialId == 49) {
                            yPixels = 16752800;
                        }
                        // Start yellow
                        if (currentTileSpecialId == 50) {
                            yPixels = 16777088;
                        }
                        // Start green
                        if (currentTileSpecialId == 51) {
                            yPixels = 9502608;
                        }

                        if (yPixels != -1) {
                            for (xPixels = 6; xPixels <= 8; ++xPixels) {
                                for (var13 = 6; var13 <= 8; ++var13) {
                                    currentTileImageData[xPixels * 15 + var13] = yPixels;
                                }
                            }
                        }
                    }
                }

                for (currentTile = 0; currentTile < 15; ++currentTile) {
                    for (yPixels = 0; yPixels < 15; ++yPixels) {
                        mapPixels[(tileY * 15 + currentTile) * 735 + tileX * 15 + yPixels] =
                                currentTileImageData[currentTile * 15 + yPixels];
                    }
                }
            }
        }

        try {
            int var14;
            int var15;
            if (this.gameContainer.graphicsQualityIndex > 0) {
                for (yPixels = 0; yPixels < 375; ++yPixels) {
                    for (xPixels = 0; xPixels < 735; ++xPixels) {
                        boolean var25;
                        boolean var27;
                        // creates light and dark spot for solids
                        // top and left side is brighter
                        // bottom and right side is darker
                        if (this.castsShadow(xPixels, yPixels)) {
                            var25 = this.castsShadow(xPixels - 1, yPixels - 1);
                            var27 = this.castsShadow(xPixels + 1, yPixels + 1);
                            if (!var25
                                    && var27
                                    && !this.castsShadow(xPixels, yPixels - 1)
                                    && !this.castsShadow(xPixels - 1, yPixels)) {
                                this.shiftPixel(mapPixels, xPixels, yPixels, 128, 735);
                            } else {
                                if (!var25 && var27) {
                                    // shift pixels towards 255/white
                                    this.shiftPixel(mapPixels, xPixels, yPixels, 24, 735);
                                }

                                if (!var27 && var25) {
                                    // shift pixels towards 0/black
                                    this.shiftPixel(mapPixels, xPixels, yPixels, -24, 735);
                                }
                            }

                            // draws shadow
                            if (this.gameContainer.graphicsQualityIndex >= 2) {
                                for (var13 = 1; var13 <= 7 && xPixels + var13 < 735 && yPixels + var13 < 375; ++var13) {
                                    if (!this.castsShadow(
                                            xPixels + var13, yPixels + var13)) { // dont draw shadow on blocks
                                        var14 = xPixels + var13;
                                        var15 = yPixels + var13;
                                        // shift pixels towards black to create shadow
                                        this.shiftPixel(mapPixels, var14, var15, -8, 735);
                                    }
                                }
                            }
                        }

                        // creates light and dark spots to teleport starts
                        if (map.isTeleportStart(xPixels, yPixels)) {
                            var25 = map.isTeleportStart(xPixels - 1, yPixels - 1);
                            var27 = map.isTeleportStart(xPixels + 1, yPixels + 1);
                            if (!var25
                                    && var27
                                    && !map.isTeleportStart(xPixels, yPixels - 1)
                                    && !map.isTeleportStart(xPixels - 1, yPixels)) {
                                this.shiftPixel(mapPixels, xPixels, yPixels, 16, 735);
                            } else {
                                if (!var25 && var27) {
                                    // shift pixels towards 255/white
                                    this.shiftPixel(mapPixels, xPixels, yPixels, 16, 735);
                                }

                                if (!var27 && var25) {
                                    // shift pixels towards 0/black
                                    this.shiftPixel(mapPixels, xPixels, yPixels, -16, 735);
                                }
                            }
                        }

                        // creates grain effect on tiles
                        if (this.gameContainer.graphicsQualityIndex >= 2) {
                            var13 = (int) (Math.random() * 11.0D) - 5;
                            this.shiftPixel(mapPixels, xPixels, yPixels, var13, 735);
                        }
                    }
                }
            }

            int[][] var26 = this.gameContainer.spriteManager.method1138();
            currentTile = -1;

            if (currentTile >= 0) {
                double var16 = 0.4D - (double) currentTile * 0.05D;
                var14 = 0;
                var15 = 0;
                int var18 = 735;
                int var19 = 375;
                int var20 = 0;
                int var21 = 0;

                for (int var22 = var15; var22 < var15 + var19; ++var22) {
                    for (int var23 = var14; var23 < var14 + var18; ++var23) {
                        if (currentTile < 3
                                || currentTile == 3
                                        && map.getColMap(var23, var22) >= 0
                                        && map.getColMap(var23, var22) <= 15) {
                            mapPixels[var22 * 735 + var23] = this.method129(
                                    mapPixels[var22 * 735 + var23], var26[currentTile][var21 * var18 + var20], var16);
                        }

                        ++var20;
                    }

                    ++var21;
                    var20 = 0;
                }
            }
        } catch (OutOfMemoryError e) {
        }

        this.graphics.drawImage(
                this.gameContainer.imageManager.createImage(mapPixels, this.trackWidth, this.trackHeight), 0, 0, this);
    }

    private boolean castsShadow(int x, int y) {
        // trackSpecialSettings[3]
        // 3:false => illusion walls shadowless  3:true => illusion walls shadows
        return this.track.map.castShadow(x, y, this.track.trackSpecialSettings);
    }

    // adds offset to r,b,g channels of pixels[x][y] then clamps the value to valid range
    private void shiftPixel(int[] pixels, int x, int y, int offset, int width) {
        int pixel = pixels[y * width + x] & 16777215;
        int red = pixel / 65536 % 256;
        int green = pixel / 256 % 256;
        int blue = pixel % 256;
        red += offset;
        if (red < 0) {
            red = 0;
        }

        if (red > 255) {
            red = 255;
        }

        green += offset;
        if (green < 0) {
            green = 0;
        }

        if (green > 255) {
            green = 255;
        }

        blue += offset;
        if (blue < 0) {
            blue = 0;
        }

        if (blue > 255) {
            blue = 255;
        }

        pixels[y * width + x] = -16777216 + red * 256 * 256 + green * 256 + blue;
    }

    private int method129(int var1, int var2, double var3) {
        long var5 = ((long) var2 & 4278190080L) >> 24;
        if (var5 < 255L) {
            return var1;
        } else {
            int var7 = (var1 & 16711680) >> 16;
            int var8 = (var1 & 65280) >> 8;
            int var9 = var1 & 255;
            int var10 = (var2 & 16711680) >> 16;
            int var11 = (var2 & 65280) >> 8;
            int var12 = var2 & 255;
            int var13 = var10 - var7;
            int var14 = var11 - var8;
            int var15 = var12 - var9;
            int var16 = (int) ((double) var7 + (double) var13 * var3 + 0.5D);
            int var17 = (int) ((double) var8 + (double) var14 * var3 + 0.5D);
            int var18 = (int) ((double) var9 + (double) var15 * var3 + 0.5D);
            return (int) (4278190080L + (long) (var16 << 16) + (long) (var17 << 8) + (long) var18);
        }
    }
}
