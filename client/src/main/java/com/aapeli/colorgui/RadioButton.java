package com.aapeli.colorgui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;

public class RadioButton extends Button {

    private RadioButtonGroup group;
    private boolean selected;
    private boolean selectedTextIsBold;
    private boolean altStyle;

    public RadioButton(String label, RadioButtonGroup group) {
        super(label);
        this.group = group;
        if (group != null) {
            group.addButton(this);
        }

        this.selected = false;
        this.selectedTextIsBold = false;
        this.altStyle = false;
    }

    public RadioButton(String label, RadioButtonGroup group, boolean selected) {
        super(label);
        this.group = group;
        group.addButton(this);
        this.selected = selected;
        this.selectedTextIsBold = false;
        this.setBackgroundGradient(false);
        this.setBorder(BORDER_THICK);
        this.altStyle = true;
    }

    public void mousePressed(MouseEvent e) {
        if (this.setState(!this.selected)) {
            this.processActionEvent();
        }
    }

    public void mouseReleased(MouseEvent e) {}

    public boolean setState(boolean state) {
        if (this.selected == state) {
            return true;
        } else if (this.group != null && !this.group.trySetState(state)) {
            return false;
        } else {
            this.realSetState(state);
            return true;
        }
    }

    public boolean getState() {
        return this.selected;
    }

    public void click() {
        this.mousePressed(null);
    }

    public void boldSelected(boolean selectedTextIsBold) {
        this.selectedTextIsBold = selectedTextIsBold;
    }

    public boolean isNormalState() {
        return !this.selected;
    }

    public boolean isHighlighted() {
        return this.selected ? true : super.isHighlighted();
    }

    public boolean isBolded() {
        return this.selected && this.selectedTextIsBold || this.altStyle;
    }

    public void clearBackground(Graphics g, int width, int height) {
        if (!this.altStyle) {
            super.clearBackground(g, width, height);
        } else {
            int border = this.getBorder();
            border = border == BORDER_NONE ? BORDER_NONE : (border == BORDER_NORMAL ? BORDER_NORMAL : BORDER_THICK);
            g.fillRect(border, border, width - border - border, height - border);
        }
    }

    public void drawBorder(Graphics g, int width, int height) {
        if (!this.altStyle) {
            super.drawBorder(g, width, height);
        } else {
            int border = this.getBorder();
            if (border != BORDER_NONE) {
                boolean thickBorder = border == BORDER_THICK;
                if (!this.selected) {
                    if (thickBorder) {
                        g.drawRect(0, 2, width - 1, height - 3);
                        g.drawRect(1, 1, width - 3, height - 3);
                    } else {
                        g.drawLine(1, 1, width - 2, 1);
                        g.drawLine(0, 2, 0, height - 1);
                        g.drawLine(width - 1, 2, width - 1, height - 1);
                        g.drawLine(0, height - 1, width - 1, height - 1);
                    }
                } else if (thickBorder) {
                    g.drawLine(1, 0, width - 2, 0);
                    g.drawLine(0, 1, width - 1, 1);
                    g.drawLine(0, 1, 0, height - 1);
                    g.drawLine(1, 0, 1, height - 1);
                    g.drawLine(width - 1, 1, width - 1, height - 1);
                    g.drawLine(width - 2, 0, width - 2, height - 1);
                } else {
                    g.drawLine(1, 0, width - 2, 0);
                    g.drawLine(0, 1, 0, height - 1);
                    g.drawLine(width - 1, 1, width - 1, height - 1);
                }
            }
        }
    }

    public int drawIcon(Graphics g, Image icon, int y) {
        int x = Math.max(y, 5);

        g.drawImage(icon, x, y, this);
        return x;
    }

    public void realSetState(boolean selected) {
        this.selected = selected;
        this.repaint();
    }
}
