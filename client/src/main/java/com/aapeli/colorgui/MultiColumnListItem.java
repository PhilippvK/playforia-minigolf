package com.aapeli.colorgui;

import java.awt.Color;
import java.awt.Image;

public class MultiColumnListItem {

    public static final int COLOR_BLACK = 0;
    public static final int COLOR_RED = 1;
    public static final int COLOR_GREEN = 2;
    public static final int COLOR_BLUE = 3;
    public static final int COLOR_YELLOW = 4;
    public static final int COLOR_MAGENTA = 5;
    public static final int COLOR_CYAN = 6;
    public static final int COLOR_GRAY = 7;
    public static final int COLOR_WHITE = 8;
    private static final Color[] colours = new Color[] {
        new Color(0, 0, 0), new Color(240, 0, 0), new Color(0, 160, 0),
        new Color(0, 0, 255), new Color(144, 144, 0), new Color(176, 0, 176),
        new Color(0, 160, 176), new Color(112, 112, 112), new Color(255, 255, 255)
    };

    private Color colour;
    private Color colourOverride;
    private boolean bold;
    private String[] strings;
    private Image[] images;
    private Object data;
    private boolean selected;

    public MultiColumnListItem(int colourIndex, String[] strings, Object data) {
        this(colours[colourIndex], false, strings, data, false);
    }

    public MultiColumnListItem(int colourIndex, boolean bold, String[] strings, Object data, boolean selected) {
        this(colours[colourIndex], bold, strings, data, selected);
    }

    public MultiColumnListItem(Color colour, boolean bold, String[] strings, Object data, boolean selected) {
        this.colour = colour;
        this.bold = bold;
        this.strings = strings;
        this.data = data;
        this.selected = selected;
        this.images = new Image[strings.length];
        this.colourOverride = null;
    }

    public String toString() {
        String s = "[MultiColorListItem: strings={";

        for (int index = 0; index < this.strings.length; ++index) {
            s = s + this.strings[index];
            if (index < this.strings.length - 1) {
                s = s + ',';
            }
        }

        s = s + "} data=\"" + this.data + "\" selected=" + this.selected + "]";
        return s;
    }

    public void setColor(int colourIndex) {
        this.setColor(colours[colourIndex]);
    }

    public void setColor(Color colour) {
        this.colour = colour;
    }

    public void setOverrideColor(int colourIndex) {
        this.setOverrideColor(colourIndex >= 0 ? colours[colourIndex] : null);
    }

    public void setOverrideColor(Color colour) {
        this.colourOverride = colour;
    }

    public Color getColor() {
        return this.colourOverride != null ? this.colourOverride : this.colour;
    }

    public void setBold(boolean var1) {
        this.bold = var1;
    }

    public boolean isBold() {
        return this.bold;
    }

    public void setString(int index, String value) {
        this.strings[index] = value;
    }

    public String getString(int value) {
        return this.strings[value];
    }

    public String[] getStrings() {
        return this.strings;
    }

    public void setImage(int index, Image value) {
        this.images[index] = value;
    }

    public Image getImage(int index) {
        return this.images[index];
    }

    public Image[] getImages() {
        return this.images;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return this.data;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public int getColumnCount() {
        return this.strings.length;
    }
}
