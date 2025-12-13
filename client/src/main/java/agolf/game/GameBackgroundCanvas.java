package agolf.game;

import agolf.GameContainer;
import agolf.GolfGameFrame;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import org.moparforia.shared.tracks.Track;
import org.moparforia.shared.tracks.stats.TrackStats;

public class GameBackgroundCanvas extends Canvas {

    protected static final Color aColor75 = new Color(240, 240, 255);
    public static final int[] anIntArray78 = new int[] {3, 5, 8, 49};
    public static final int[] anIntArray79 = new int[] {2, 3, 5, 25};
    protected GameContainer gameContainer;
    private Image backgroundImg;
    protected Image image;
    private Graphics graphics;
    public Track track;
    public TrackStats trackStats;
    public Map map;
    private int trackWidth = 735;
    private int trackHeight = 375;

    protected GameBackgroundCanvas(GameContainer gameContainer, Image backgroundImage) {
        this.gameContainer = gameContainer;
        this.backgroundImg = backgroundImage;
        this.setSize(this.trackWidth, this.trackHeight);
        this.map = new Map(49, 25);
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

                this.map.updateTile(x, y, tile);
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
        int[] imageData = this.gameContainer.spriteManager.getPixelsFromTileCode(
                this.map.getTile(tileX, tileY).getCode());
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
        Tile oldTile = null;
        boolean trackTestMode =
                this.gameContainer.synchronizedTrackTestMode.get(); // controls when to draw starting positions

        Tile currentTile;
        int yPixels;
        int xPixels;
        int index;
        Map map = this.map;
        for (int tileY = 0; tileY < 25; ++tileY) {
            for (int tileX = 0; tileX < 49; ++tileX) {
                currentTile = map.getTile(tileX, tileY);
                if (!currentTile.equals(oldTile)) {
                    currentTile = map.getTile(tileX, tileY);

                    int currentCode = currentTile.getSpecialsettingCode(this.track.getSpecialSettings());
                    currentTileImageData = this.gameContainer.spriteManager.getPixelsFromTileCode(currentCode);

                    // draws debug points on starting positions
                    if (trackTestMode && currentTile.getCode() == 2) {
                        yPixels = currentTile.getYPixelsFromSpecialId();
                        if (yPixels != -1) {
                            for (xPixels = 6; xPixels <= 8; ++xPixels) {
                                for (index = 6; index <= 8; ++index) {
                                    currentTileImageData[xPixels * 15 + index] = yPixels;
                                }
                            }
                        }
                    }
                    oldTile = map.getTile(tileX, tileY);
                }

                for (xPixels = 0; xPixels < 15; ++xPixels) {
                    for (yPixels = 0; yPixels < 15; ++yPixels) {
                        mapPixels[(tileY * 15 + xPixels) * 735 + tileX * 15 + yPixels] =
                                currentTileImageData[xPixels * 15 + yPixels];
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
                                for (index = 1; index <= 7 && xPixels + index < 735 && yPixels + index < 375; ++index) {
                                    if (!this.castsShadow(
                                            xPixels + index, yPixels + index)) { // dont draw shadow on blocks
                                        var14 = xPixels + index;
                                        var15 = yPixels + index;
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
                            index = (int) (Math.random() * 11.0D) - 5;
                            this.shiftPixel(mapPixels, xPixels, yPixels, index, 735);
                        }
                    }
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
        return this.map.castShadow(x, y, this.track.getSpecialSettings());
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
}
