package com.aapeli.colorgui;

import org.moparforia.shared.game.Lobby;

import java.awt.Color;
import java.awt.Image;

public class MultiColorListItem {



    private Color colour;
    private Color colourOverride;
    private boolean bold;
    private String[] strings;
    private Image[] images;
    private Lobby lobby;
    private boolean selected;


//    public MultiColorListItem(int colourIndex, boolean bold, String[] strings) {
//        this(colours[colourIndex], bold, strings, (Object) null, false);
//    }
//
//    public MultiColorListItem(int colourIndex, String[] strings, Lobby data) {
//        this(colours[colourIndex], false, strings, data, false);
//    }
//
//    public MultiColorListItem(int colourIndex, boolean bold, String[] strings, Lobby lobby) {
//        this(colours[colourIndex], bold, strings, data, false);
//    }
//
//    public MultiColorListItem(int colourIndex, boolean bold, String[] strings, Lobby lobby, boolean selected) {
//        this(colours[colourIndex], bold, strings, data, selected);
//    }

    public MultiColorListItem(Color colour, boolean bold, String[] strings, Lobby lobby, boolean selected) {
        this.colour = colour;
        this.bold = bold;
        this.strings = strings;
        this.lobby = lobby;
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

        s = s + "} data=\"" + this.lobby + "\" selected=" + this.selected + "]";
        return s;
    }

    public void setColor(Color colour) {
        this.colour = colour;
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

    public void setData(Lobby lobby) {
        this.lobby = lobby;
    }

    public Lobby getData() {
        return this.lobby;
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
