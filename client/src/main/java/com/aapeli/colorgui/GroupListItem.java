package com.aapeli.colorgui;

import java.awt.Color;
import java.awt.Image;

public class GroupListItem {

    public static final int COLOR_BLACK = 0;
    public static final int COLOR_RED = 1;
    public static final int COLOR_GREEN = 2;
    public static final int COLOR_BLUE = 3;
    public static final int COLOR_YELLOW = 4;
    public static final int COLOR_MAGENTA = 5;
    public static final int COLOR_CYAN = 6;
    public static final int COLOR_GRAY = 7;
    public static final int COLOR_WHITE = 8;
    private static final Color[] colors = new Color[] {
        new Color(0, 0, 0),
        new Color(240, 0, 0),
        new Color(0, 160, 0),
        new Color(0, 0, 255),
        new Color(144, 144, 0),
        new Color(176, 0, 176),
        new Color(0, 160, 176),
        new Color(112, 112, 112),
        new Color(255, 255, 255)
    };
    private Image icon;
    private Color color;
    private boolean bold;
    private String text;
    private Image iconAfterText;
    private Object data;
    private boolean selected;
    private int value;
    private boolean sortOverride;
    private GroupListGroup group;
    private SelectableGroupList selectableGroupList;

    public GroupListItem(Image icon, Color color, boolean bold, String text, Object data, boolean selected) {
        this.icon = icon;
        this.color = color;
        this.bold = bold;
        this.text = text;
        this.data = data;
        this.selected = selected;
        this.value = 0;
        this.sortOverride = false;
    }

    public static Color getColorById(int i) {
        return colors[i];
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    public Image getIcon() {
        return this.icon;
    }

    public void setIconAfterText(Image iconAfterText) {
        this.iconAfterText = iconAfterText;
    }

    public Image getIconAfterText() {
        return this.iconAfterText;
    }

    public void setColor(int i) {
        this.setColor(colors[i]);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isBold() {
        return this.bold;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
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

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void setSortOverride(boolean sortOverride) {
        this.sortOverride = sortOverride;
    }

    public boolean isSortOverride() {
        return this.sortOverride;
    }

    public void setGroup(GroupListGroup group) {
        this.group = group;
    }

    public GroupListGroup getGroup() {
        return this.group;
    }

    public void setGroupList(SelectableGroupList selectableGroupList) {
        this.selectableGroupList = selectableGroupList;
    }

    public SelectableGroupList getGroupList() {
        return this.selectableGroupList;
    }
}
