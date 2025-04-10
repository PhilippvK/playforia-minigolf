package com.aapeli.colorgui;

import com.aapeli.client.StringDraw;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

class GroupListNode {

    private int x;
    private int y;
    private int width;
    private int height;
    private int iconWidth;
    private boolean hasBackgroundImage;
    private Font font;
    private Font fontBold;
    private GroupListItem item;
    private Color color;
    private String text;
    private Image icon;
    private boolean hasIcon;

    /** Node for items (e.g. players) */
    protected GroupListNode(
            int x,
            int y,
            int width,
            int height,
            int iconWidth,
            boolean hasBackgroundImage,
            Font font,
            Font fontBold,
            GroupListItem item) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.iconWidth = iconWidth;
        this.hasBackgroundImage = hasBackgroundImage;
        this.font = font;
        this.fontBold = fontBold;
        this.item = item;
        this.hasIcon = false;
    }

    /** Node for groups (e.g. languages) */
    protected GroupListNode(
            int x,
            int y,
            int width,
            int height,
            int iconWidth,
            boolean hasBackgroundImage,
            Font font,
            Color color,
            String text,
            Image icon) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.iconWidth = iconWidth;
        this.hasBackgroundImage = hasBackgroundImage;
        this.font = font;
        this.color = color;
        this.text = text;
        this.icon = icon;
        this.hasIcon = icon != null;
    }

    protected void draw(Graphics g, SelectableGroupList selectableGroupList) {
        if (this.item != null) {
            this.drawItem(g, selectableGroupList);
        } else {
            this.drawGroup(g, selectableGroupList);
        }
    }

    protected boolean containsYCoordinate(int y) {
        return y >= this.y && y < this.y + this.height;
    }

    protected GroupListItem getItem() {
        return this.item;
    }

    private void drawItem(Graphics g, SelectableGroupList selectableGroupList) {
        Color color = this.item.getColor();
        if (this.item.isSelected()) {
            g.setColor(color);
            g.fillRect(this.x, this.y, this.width, this.height);
            color = this.getForegroundColor(color);
        }

        this.drawText(
                g,
                selectableGroupList,
                this.item.getIcon(),
                color,
                this.item.isBold() ? this.fontBold : this.font,
                this.item.getText(),
                this.item.getIconAfterText());
    }

    private void drawGroup(Graphics g, SelectableGroupList selectableGroupList) {
        if (this.hasIcon) {
            g.setColor(new Color(224, 224, 224));
            g.fillRect(this.x, this.y, this.width, this.height);
        }

        this.drawText(g, selectableGroupList, this.icon, this.color, this.font, this.text, null);
    }

    private void drawText(
            Graphics g,
            SelectableGroupList selectableGroupList,
            Image icon,
            Color color,
            Font font,
            String text,
            Image iconAfterText) {
        int x = 4;
        if (icon != null) {
            g.drawImage(
                    icon, x, this.y + this.height / 2 - icon.getHeight(selectableGroupList) / 2, selectableGroupList);
            int iconWidth = this.iconWidth > 0 ? this.iconWidth : icon.getWidth(null);
            x += iconWidth + 3;
        }

        g.setColor(color);
        g.setFont(font);
        x += StringDraw.drawString(g, text, x, this.y + this.height * 3 / 4 + 1, -1);
        if (iconAfterText != null) {
            x += 4;
            g.drawImage(
                    iconAfterText,
                    x,
                    this.y + this.height / 2 - iconAfterText.getHeight(selectableGroupList) / 2,
                    selectableGroupList);
        }
    }

    private Color getForegroundColor(Color backgroundColor) {
        if (!this.hasBackgroundImage) {
            return SelectableGroupList.backgroundColor;
        } else {
            int r = backgroundColor.getRed();
            int g = backgroundColor.getGreen();
            int b = backgroundColor.getBlue();
            return new Color(255 - r, 255 - g, 255 - b);
        }
    }
}
