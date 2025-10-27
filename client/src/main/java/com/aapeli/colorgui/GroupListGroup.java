package com.aapeli.colorgui;

import java.awt.Image;

public class GroupListGroup {

    private String text;
    private Image icon;
    private int sortValue;

    public GroupListGroup(String text, Image icon, int sortValue) {
        this.text = text;
        this.icon = icon;
        this.sortValue = sortValue;
    }

    public String getText() {
        return this.text;
    }

    public Image getIcon() {
        return this.icon;
    }

    public int getSortValue() {
        return this.sortValue;
    }

    public void changeSortValue(int offset) {
        this.sortValue += offset;
    }
}
