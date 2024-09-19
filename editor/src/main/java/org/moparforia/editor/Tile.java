package org.moparforia.editor;

public class Tile {

    /**
     * Sprite index of the shape mask.
     */
    private int shapeIndex;
    /**
     * Sprite index of the foreground Element tile.
     */
    private int backgroundElementIndex;
    /**
     * Sprite index of the background Element tile (only used when two are layered I THINK)...
     */
    private int foregroundElementIndex;
    /**
     * Code for whether we're drawing a special sprite or just two elements together.
     * 1=normal
     * 2=special.
     */
    private int isSpecial;

    public Tile() {
        this(0, 0, 0, 1); // Default tile is grass tile
    }

    public Tile(int shape, int background, int foreground, int isSpecial) {
        this.shapeIndex = shape;
        this.backgroundElementIndex = background;
        this.foregroundElementIndex = foreground;
        this.isSpecial = isSpecial;
    }

    public void setShapeIndex(int shapeIndex) {
        this.shapeIndex = shapeIndex;
    }

    public void setBackground(int index) {
        this.backgroundElementIndex = index;
    }

    public void setForeground(int index) {
        this.foregroundElementIndex = index;
    }

    public void setSpecial(boolean special) {
        isSpecial = special ? 2 : 1;
    }

    public boolean isSpecial() {
        return isSpecial == 2;
    }

    public boolean isStartPosition() {
        return isSpecial() && (shapeIndex == 0 || (shapeIndex >= 21 && shapeIndex <= 24));
    }

    public boolean isHole() {
        return isSpecial() && shapeIndex == 1 ;
    }

    public boolean isPassable() {
        return !(!isSpecial() && (backgroundElementIndex == 12 ||
                backgroundElementIndex ==13 ||
                (backgroundElementIndex >=16 && backgroundElementIndex <=18) ));
    }

    public Tile clone() {
        return new Tile(shapeIndex,backgroundElementIndex,foregroundElementIndex,isSpecial);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Tile) {
            Tile t = (Tile) o;
            if (this.getTileCode() == t.getTileCode()) {
                return true;
            } else {
                return false; // valid tile, not the same
            }
        } else {
            return false; // no alid
        }
    }

    @Override
    public String toString() {
        return "tilecode:" + getTileCode() + " shape:" + shapeIndex + " back:" + backgroundElementIndex + " " +
                "fore:"+foregroundElementIndex+" special:"+isSpecial;
    }

    public int getTileCode() {
        return (isSpecial << 24) | (shapeIndex << 16) | (backgroundElementIndex << 8) | foregroundElementIndex;
    }
}
