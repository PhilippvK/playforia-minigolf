package agolf.game;

import agolf.SpriteManager;
import java.util.StringTokenizer;

class Map {
    // String mapData;
    private static final String mapChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private byte[][] collisionMap;
    private boolean[] latestTileSpeciality;
    private Tile[][] tiles;
    private int width;
    private int height;
    private int[][] tileCodeArray;
    public int index = 0;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        this.tileCodeArray = new int[width][height]; // To remain compatible with hackedshot
    }

    /*
     * The below is the map parsing:
     * firstly the input map is "expanded", any letter preceeding by a number is duplicated that number times.
     * If input letter is A,B,C, the letter + the next three are concatenated into one int (4 * bytes)
     * If input letters are D,E,F,G,H,I, the current tile is exactly the same as an adjacent one so
     * one is selected, depending on the input letter.
     */
    public void parse(String inputData) {
        StringTokenizer subTokenizer = new StringTokenizer(inputData, ",");
        String mapData = this.expandMap(subTokenizer.nextToken());
        int cursorIndex = 0;

        int tileX;
        for (int tileY = 0; tileY < 25; ++tileY) {
            for (tileX = 0; tileX < 49; ++tileX) {

                int currentMapIndex = mapChars.indexOf(mapData.charAt(cursorIndex));

                if (currentMapIndex <= 2) { // if input= A,B or C
                    int mapcursor_one_ahead;
                    int mapcursor_two_ahead;
                    int mapcursor_three_ahead;

                    if (currentMapIndex == 1) { // if input = B.
                        mapcursor_one_ahead = mapChars.indexOf(mapData.charAt(cursorIndex + 1));
                        mapcursor_two_ahead = mapChars.indexOf(mapData.charAt(cursorIndex + 2));
                        mapcursor_three_ahead = mapChars.indexOf(mapData.charAt(cursorIndex + 3));
                        cursorIndex += 4;
                    } else { // if input = A or C
                        mapcursor_one_ahead = mapChars.indexOf(mapData.charAt(cursorIndex + 1));
                        mapcursor_two_ahead = mapChars.indexOf(mapData.charAt(cursorIndex + 2));
                        mapcursor_three_ahead = 0;
                        cursorIndex += 3;
                    }

                    // (currentMapIndex << 24) + (mapcursor_one_ahead << 16) +
                    // (mapcursor_two_ahead << 8) + mapcursor_three_ahead;
                    int tileCode = currentMapIndex * 256 * 256 * 256
                            + mapcursor_one_ahead * 256 * 256
                            + mapcursor_two_ahead * 256
                            + mapcursor_three_ahead;
                    this.updateTile(tileX, tileY, tileCode);
                    this.tileCodeArray[tileX][tileY] = tileCode;
                } else {
                    if (currentMapIndex == 3) { // if input = D
                        this.updateTile(tileX, tileY, this.tiles[tileX - 1][tileY].getCode());
                        // tile to west is same as current
                    }

                    if (currentMapIndex == 4) { // if input = E;
                        this.updateTile(tileX, tileY, this.tiles[tileX][tileY - 1].getCode());
                        // tile to the north is same as current
                    }

                    if (currentMapIndex == 5) { // if input = F;
                        this.updateTile(tileX, tileY, this.tiles[tileX - 1][tileY - 1].getCode());
                        // tile to the northwest is same as
                    }

                    if (currentMapIndex == 6) { // if input = G;
                        this.updateTile(tileX, tileY, this.tiles[tileX - 2][tileY].getCode());
                        // 2 tiles west is same as current (skip a tile to the left)
                    }

                    if (currentMapIndex == 7) { // if input = H
                        this.updateTile(tileX, tileY, this.tiles[tileX][tileY - 2].getCode());
                        // 2 tiles north is same as current (skip the tile above)
                    }

                    if (currentMapIndex == 8) { // if input= I
                        this.updateTile(tileX, tileY, this.tiles[tileX - 2][tileY - 2].getCode());
                        // 2 tiles northwest is same as current (skip the diagonal)
                    }

                    ++cursorIndex;
                }
            }
        }

        if (subTokenizer.hasMoreTokens()) {
            mapData = subTokenizer.nextToken();
            if (!mapData.startsWith("Ads:")) {
                // Something fishy going on, maybe throw an error
            }
            // Ads are not used anymore, nothing to parse here.
        }
    }

    public byte getColMap(int x, int y) {
        return this.collisionMap[x][y];
    }

    public byte[][] getColMap() {
        return this.collisionMap;
    }

    // public void setTile(int x, int y, Tile tile) {
    //     this.tiles[x][y] = tile;
    // }

    public Tile getTile(int x, int y) {
        return this.tiles[x][y];
    }

    public int[][] getTileCodeArray() {
        return this.tileCodeArray;
    }

    public void checkSolids(SpriteManager manager) {
        this.latestTileSpeciality = new boolean[2];
        this.latestTileSpeciality[0] = this.latestTileSpeciality[1] = false;
        this.collisionMap = new byte[735][375];

        for (int y = 0; y < 25; ++y) {
            for (int x = 0; x < 49; ++x) {
                this.collisionMap(x, y, manager);
            }
        }
    }

    public boolean isTeleportStart(int x, int y) {
        return x >= 0 && x < this.width && y >= 0 && y < this.height
                ? this.getColMap(x, y) == 32
                        || this.getColMap(x, y) == 34
                        || this.getColMap(x, y) == 36
                        || this.getColMap(x, y) == 38
                : false;
    }

    public boolean castShadow(int x, int y, boolean[] specialSettings) {
        // trackSpecialSettings[3]
        // 3:false => illusion walls shadowless  3:true => illusion walls shadows
        return x >= 0 && x < this.width && y >= 0 && y < this.height
                ? this.getColMap(x, y) >= 16
                        && this.getColMap(x, y) <= 23
                        && (specialSettings[3] || !specialSettings[3] && this.getColMap(x, y) != 19)
                : false;
    }

    public void updateTile(int x, int y, int tileCode) {
        if (tiles[x][y] == null) {
            tiles[x][y] = new Tile(tileCode);
        } else {
            tiles[x][y].update(tileCode);
        }
    }

    protected void collisionMap(int tileX, int tileY, SpriteManager manager) {
        Tile tile = this.getTile(tileX, tileY);
        int special = tile.getSpecial();
        int shape = tile.getShapeReduced();
        int background = tile.getBackground();
        int foreground = tile.getForeground();
        int pixel = Integer.MIN_VALUE;

        if (special == 1 && (background == 19 || foreground == 19)) { // IF HAX BLOCK
            this.latestTileSpeciality[0] = true;
        } else if (special == 2 && shape == 2) {
            this.latestTileSpeciality[1] = true;
        }

        int[][] mask = manager.getPixelMask(special, shape);

        for (int y = 0; y < 15; ++y) {
            for (int x = 0; x < 15; ++x) {

                if (special == 1) {
                    pixel = mask[x][y] == 1 ? background : foreground;

                } else if (special == 2) {
                    shape += 24;
                    pixel = mask[x][y] == 1 ? background : shape;
                    // 24 StartPosition
                    if (shape == 24) {
                        pixel = background;
                    }
                    // 26 Fake Hole
                    if (shape == 26) {
                        pixel = background;
                    }
                    // Teleport Exits
                    if (shape == 33 || shape == 35 || shape == 37 || shape == 39) {
                        pixel = background;
                    }

                    // Bricks
                    if (shape >= 40 && shape <= 43) {
                        pixel = shape;
                    }

                    // magnet
                    if (shape == 44) {
                        pixel = background != 12 && background != 13 && background != 14 && background != 15
                                ? shape
                                : background;
                    }

                    // magnet repel
                    if (shape == 45) {
                        pixel = background;
                    }

                    shape -= 24;
                }

                this.collisionMap[tileX * 15 + x][tileY * 15 + y] = (byte) pixel;
            }
        }
    }

    protected boolean[] getLatestTileSpeciality() {
        return this.latestTileSpeciality;
    }

    private String expandMap(String mapString) {
        StringBuffer buffer = new StringBuffer(4900);
        int length = mapString.length();

        for (int var4 = 0; var4 < length; ++var4) {
            int var5 = this.readNumber(mapString, var4);
            if (var5 >= 2) {
                ++var4;
            }

            if (var5 >= 10) {
                ++var4;
            }

            if (var5 >= 100) {
                ++var4;
            }

            if (var5 >= 1000) {
                ++var4;
            }

            char var6 = mapString.charAt(var4);

            buffer.append(String.valueOf(var6).repeat(Math.max(0, var5)));
        }

        return buffer.toString();
    }

    private int readNumber(String mapStr, int charIdx) {
        String result = null;

        while (true) {
            char digit = mapStr.charAt(charIdx);
            if (digit < '0' || digit > '9') {
                return result == null ? 1 : Integer.parseInt(result);
            }

            if (result == null) {
                result = String.valueOf(digit);
            } else {
                result = result + digit;
            }

            ++charIdx;
        }
    }
}
