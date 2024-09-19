package org.moparforia.editor;

public class Map {

    public Tile[][] tiles = new Tile[49][25];


    public Map(Tile[][] init) throws Exception {
        boolean valid = true;
        if (init.length == 49 && init[0].length == 25) {
            for (int y = 0; y < tiles[0].length; y++) {
                for (int x = 0; x < tiles.length; x++) {
                    if (init[x][y] == null) {
                        throw new Exception("Invalid Map Size: must be 49*25");
                    }
                }
            }

            tiles = init;
        }
    }

    public Map() {
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                tiles[x][y] = new Tile();
            }
        }
    }

    public boolean setTile(int x, int y, Tile t) {
        if (x >= 0 && x < 49 && y >= 0 && y < 25 && t != null) {
            tiles[x][y] = t.clone();
            return true;
        } else {
            return false;
        }
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < 49 && y >= 0 && y < 25) {
            return tiles[x][y];
        } else {
            return null;
        }
    }

    public boolean copyMap(Map m) {
        if (m != null) {
            for (int y = 0; y < tiles[0].length; y++) {
                for (int x = 0; x < tiles.length; x++) {
                    tiles[x][y] = m.tiles[x][y];
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public Tile[][] toTileArray() {
        return tiles;
    }


    public void printMap() {
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                System.out.print(Integer.toString(tiles[x][y].getTileCode(),16) + " | ");
            }
            System.out.println();
        }
    }

}
