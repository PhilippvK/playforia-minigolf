package org.moparforia.editor;

public class MapDecompressor {

    private static final String mapChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    int[][] mapTiles;

    public MapDecompressor() {
        this.mapTiles = new int[49][25];
    }

    public Map decompress(String mapData) throws Exception{
        String expandData = expandMap(mapData);
        System.out.println("input: "+mapData+"\nexpand:"+expandData);
        parseMap(expandData);
        Tile[][] tiles = convertCodesToTiles();

        return new Map(tiles);
    }

    public Tile[][] convertCodesToTiles() {
              Tile[][] result = new Tile[49][25];
        for (int y = 0; y < 25; y++) {
                   for (int x = 0; x < 49; x++) {
                       int tileCode = mapTiles[x][y];
                       int isNoSpecial = tileCode / 16777216;
                       int shapeIndex = tileCode / 65536 % 256; // Becomes the SpecialIndex if isNoSpecial==2
                       int foregroundElementIndex = tileCode / 256 % 256;
                       int backgroundElementIndex = tileCode % 256;
                       result[x][y] = new Tile(shapeIndex,foregroundElementIndex,backgroundElementIndex,isNoSpecial);
                   }
               }

    return result;
    }

    private void parseMap(final String data) {
        String mapData = data;
        int cursorIndex = 0;

        int tileX;
        for (int tileY = 0; tileY < 25; ++tileY) {
            for (tileX = 0; tileX < 49; ++tileX) {

                int currentMapIndex = mapChars.indexOf(mapData.charAt(cursorIndex));

                if (currentMapIndex <= 2) {  // if input= A,B or C
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

                    // (currentMapIndex << 24) + (mapcursor_one_ahead << 16) + (mapcursor_two_ahead << 8) + mapcursor_three_ahead;
                    this.mapTiles[tileX][tileY] = currentMapIndex * 256 * 256 * 256 + mapcursor_one_ahead * 256 * 256 + mapcursor_two_ahead * 256 + mapcursor_three_ahead;
                } else {

                    if (currentMapIndex == 3) {  // if input = D
                        this.mapTiles[tileX][tileY] = this.mapTiles[tileX - 1][tileY]; // tile to west is same as current
                    }

                    if (currentMapIndex == 4) { // if input = E;
                        this.mapTiles[tileX][tileY] = this.mapTiles[tileX][tileY - 1]; // tile to the north is same as current
                    }

                    if (currentMapIndex == 5) { // if input = F;
                        this.mapTiles[tileX][tileY] = this.mapTiles[tileX - 1][tileY - 1]; // tile to the northwest is same as current
                    }

                    if (currentMapIndex == 6) {  // if input = G;
                        this.mapTiles[tileX][tileY] = this.mapTiles[tileX - 2][tileY]; // 2 tiles west is same as current (skip a tile to the left)
                    }

                    if (currentMapIndex == 7) { // if input = H
                        this.mapTiles[tileX][tileY] = this.mapTiles[tileX][tileY - 2]; // 2 tiles north is same as current (skip the tile above)
                    }

                    if (currentMapIndex == 8) { // if input= I
                        this.mapTiles[tileX][tileY] = this.mapTiles[tileX - 2][tileY - 2]; // 2 tiles northwest is same as current (skip the diagonal)
                    }

                    ++cursorIndex;
                }
            }
        }


    }


    private static String expandMap(String mapString) {
        StringBuffer buffer = new StringBuffer(4900);
        int length = mapString.length();

        for (int cursor = 0; cursor < length; ++cursor) {
            int var5 = method123(mapString, cursor);
            if (var5 >= 2) {
                ++cursor;
            }

            if (var5 >= 10) {
                ++cursor;
            }

            if (var5 >= 100) {
                ++cursor;
            }

            if (var5 >= 1000) {
                ++cursor;
            }

            char var6 = mapString.charAt(cursor);

            for (int var7 = 0; var7 < var5; ++var7) {
                buffer.append(var6);
            }
        }

        return buffer.toString();
    }

    private static int method123(String input, int cursor) {
        String var3 = null;

        while (true) {
            char var4 = input.charAt(cursor);
            if (var4 < '0' || var4 > '9') {
                return var3 == null ? 1 : Integer.parseInt(var3);
            }

            if (var3 == null) {
                var3 = String.valueOf(var4);
            } else {
                var3 = var3 + var4;
            }

            ++cursor;
        }
    }


}
