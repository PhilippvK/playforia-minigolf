package com.aapeli.colorgui;

import com.aapeli.client.IPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class Checkbox extends IPanel implements ItemSelectable, MouseListener {

    public static final int ALIGN_LEFT = -1;
    public static final int ALIGN_CENTER = 0;
    public static final int ALIGN_RIGHT = 1;
    private static final Color DEFAULT_BORDER_COLOR = new Color(248, 248, 248);
    private static final Color CHECKMARK_COLOR = Color.black;
    private Font font;
    private Color foregroundColor;
    private Color borderColor;
    private Color checkmarkColor;
    private Color borderColorLight;
    private Color borderColorDark;
    private Image backgroundImage;
    private int backgroundImageOffsetX;
    private int backgroundImageOffsetY;
    private String text;
    private int alignment;
    private boolean checked;
    private CheckboxGroup checkboxGroup;
    private List<ItemListener> listeners;
    private Image image;
    private Graphics graphics;
    private int width;
    private int height;

    public Checkbox(String text) {
        this(text, false);
    }

    public Checkbox(String text, boolean checked) {
        this.text = text;
        this.checked = checked;
        this.listeners = new ArrayList<>();
        this.alignment = -1;
        this.setFont(FontConstants.font);
        this.setForeground(FontConstants.black);
        this.setBorderColor(DEFAULT_BORDER_COLOR);
        this.setCheckmarkColor(CHECKMARK_COLOR);
        this.addMouseListener(this);
        this.checkboxGroup = null;
    }

    public void update(Graphics g) {
        Dimension size = this.getSize();
        int totalWidth = size.width;
        int totalHeight = size.height;
        if (this.image == null || totalWidth != this.width || totalHeight != this.height) {
            this.image = this.createBuffer(totalWidth, totalHeight);
            this.graphics = this.getGraphics(this.image);
            this.width = totalWidth;
            this.height = totalHeight;
        }

        if (this.backgroundImage != null) {
            this.graphics.drawImage(
                    this.backgroundImage,
                    0,
                    0,
                    totalWidth,
                    totalHeight,
                    this.backgroundImageOffsetX,
                    this.backgroundImageOffsetY,
                    this.backgroundImageOffsetX + totalWidth,
                    this.backgroundImageOffsetY + totalHeight,
                    this);
        } else {
            this.drawBackground(this.graphics);
        }

        Font sizeAdjustedFont = this.text != null
                ? this.getSizeAdjustedFont(this.font, this.text, totalWidth - (totalHeight + 4))
                : null;
        int x = 0;
        int width;
        if (this.alignment == ALIGN_CENTER || this.alignment == ALIGN_RIGHT) {
            width = totalHeight
                    + 4
                    + (sizeAdjustedFont != null
                            ? this.getFontMetrics(sizeAdjustedFont).stringWidth(this.text)
                            : 0);
            if (this.alignment == 0) {
                x = totalWidth / 2 - width / 2;
            } else {
                x = totalWidth - 2 - width;
            }
        }

        width = totalHeight - 4;
        if (this.checkboxGroup == null) {
            this.drawBorders(
                    this.graphics,
                    x + 2,
                    2,
                    width,
                    width,
                    this.adjustColorForDisabled(this.borderColor),
                    this.adjustColorForDisabled(this.borderColorLight),
                    this.adjustColorForDisabled(this.borderColorDark));
        } else {
            this.graphics.setColor(this.adjustColorForDisabled(this.borderColor));
            this.graphics.fillRect(x + 3, 3, width - 2, width - 2);
            this.graphics.setColor(this.adjustColorForDisabled(this.borderColorLight));
            this.graphics.drawLine(x + 3, width + 1, x + width, width + 1);
            this.graphics.drawLine(x + width + 1, 3, x + width + 1, width);
            this.graphics.fillRect(x + width, 3, 1, 1);
            this.graphics.fillRect(x + width, width, 1, 1);
            this.graphics.setColor(this.adjustColorForDisabled(this.borderColorDark));
            this.graphics.drawLine(x + 3, 2, x + width, 2);
            this.graphics.drawLine(x + 2, 3, x + 2, width);
            this.graphics.fillRect(x + 3, 3, 1, 1);
            this.graphics.fillRect(x + 3, width, 1, 1);
        }

        if (this.checked) {
            this.graphics.setColor(this.adjustColorForDisabled(this.checkmarkColor));
            if (this.checkboxGroup == null) {
                width -= 4;
                int checkmarkStartX = width / 3;
                int checkmarkHeight = checkmarkStartX - 1;
                int checkmarkEndX = width - checkmarkStartX - 2;
                this.graphics.drawLine(
                        x + 4 + checkmarkStartX,
                        4 + width - 2,
                        x + 4 + checkmarkStartX - checkmarkHeight,
                        4 + width - 2 - checkmarkHeight);

                this.graphics.drawLine(
                        x + 4 + checkmarkStartX,
                        4 + width - 2 - 1,
                        x + 4 + checkmarkStartX - checkmarkHeight,
                        4 + width - 2 - checkmarkHeight - 1);

                this.graphics.drawLine(
                        x + 4 + checkmarkStartX,
                        4 + width - 2 - 2,
                        x + 4 + checkmarkStartX - checkmarkHeight,
                        4 + width - 2 - checkmarkHeight - 2);
                this.graphics.drawLine(
                        x + 4 + checkmarkStartX,
                        4 + width - 2,
                        x + 4 + checkmarkStartX + checkmarkEndX,
                        4 + width - 2 - checkmarkEndX);
                this.graphics.drawLine(
                        x + 4 + checkmarkStartX,
                        4 + width - 2 - 1,
                        x + 4 + checkmarkStartX + checkmarkEndX,
                        4 + width - 2 - checkmarkEndX - 1);
                this.graphics.drawLine(
                        x + 4 + checkmarkStartX,
                        4 + width - 2 - 2,
                        x + 4 + checkmarkStartX + checkmarkEndX,
                        4 + width - 2 - checkmarkEndX - 2);
            } else {
                width -= 6;
                this.graphics.fillRect(x + 6, 5, width - 2, width);
                this.graphics.fillRect(x + 5, 6, width, width - 2);
            }
        }

        if (sizeAdjustedFont != null) {
            this.graphics.setFont(sizeAdjustedFont);
            this.graphics.setColor(this.adjustColorForDisabled(this.foregroundColor));
            this.drawText(
                    this.graphics,
                    this.text,
                    x + totalHeight + 4,
                    totalHeight / 2 + sizeAdjustedFont.getSize() * 3 / 8 + 1);
        }

        g.drawImage(this.image, 0, 0, this);
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {
        boolean newState = !this.checked;
        if (this.checkboxGroup == null || this.checkboxGroup.checkboxClicked(newState)) {
            this.realSetState(newState);
            this.notifyListeners();
        }
    }

    public void addItemListener(ItemListener listener) {
        synchronized (this.listeners) {
            this.listeners.add(listener);
        }
    }

    public void removeItemListener(ItemListener listener) {
        synchronized (this.listeners) {
            this.listeners.remove(listener);
        }
    }

    public Object[] getSelectedObjects() {
        if (!this.checked) {
            return null;
        } else {
            return new Object[] {this};
        }
    }

    public void setLabel(String label) {
        this.text = label;
        this.repaint();
    }

    public String getLabel() {
        return this.text;
    }

    public void setFont(Font font) {
        this.font = font;
        this.repaint();
    }

    public void setAlign(int alignment) {
        this.alignment = alignment;
        this.repaint();
    }

    public void setBackgroundImage(Image image, int offsetX, int offsetY) {
        this.backgroundImage = image;
        this.backgroundImageOffsetX = offsetX;
        this.backgroundImageOffsetY = offsetY;
        this.repaint();
    }

    public void setForeground(Color foreground) {
        this.foregroundColor = foreground;
        this.repaint();
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        this.borderColorLight = this.translateColor(color, 32);
        this.borderColorDark = this.translateColor(color, -48);
        this.repaint();
    }

    public void setCheckmarkColor(Color color) {
        this.checkmarkColor = color;
        this.repaint();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.repaint();
    }

    public void setState(boolean state) {
        if (this.checked != state) {
            if (this.checkboxGroup == null || this.checkboxGroup.checkboxClicked(state)) {
                this.realSetState(state);
            }
        }
    }

    public boolean getState() {
        return this.checked;
    }

    public void click() {
        this.mouseReleased(null);
    }

    public Dimension getPreferredSize() {
        int preferredHeight = 3 + this.font.getSize() + 3;
        return new Dimension(
                preferredHeight + 4 + this.getFontMetrics(this.font).stringWidth(this.text) + 4, preferredHeight);
    }

    public void setGroup(CheckboxGroup group) {
        this.checkboxGroup = group;
        group.addCheckbox(this);
        this.repaint();
    }

    public Image createBuffer(int width, int height) {
        return this.createImage(width, height);
    }

    public Graphics getGraphics(Image image) {
        return image.getGraphics();
    }

    public void drawText(Graphics graphics, String text, int x, int y) {
        graphics.drawString(text, x, y);
    }

    public void realSetState(boolean checked) {
        this.checked = checked;
        this.repaint();
    }

    private Color translateColor(Color color, int offset) {
        return new Color(
                this.translateColor(color.getRed(), offset),
                this.translateColor(color.getGreen(), offset),
                this.translateColor(color.getBlue(), offset));
    }

    private int translateColor(int base, int offset) {
        base += offset;
        if (base < 0) {
            base = 0;
        } else if (base > 255) {
            base = 255;
        }

        return base;
    }

    private Font getSizeAdjustedFont(Font font, String text, int width) {
        int stringWidth = this.getFontMetrics(font).stringWidth(text);
        if (stringWidth <= width) {
            return font;
        } else {
            int fontSize = font.getSize();

            do {
                Font newFont = font;
                int newWidth = stringWidth;
                --fontSize;
                font = new Font(font.getName(), font.getStyle(), fontSize);
                stringWidth = this.getFontMetrics(font).stringWidth(text);
                if (stringWidth >= newWidth) {
                    return newFont;
                }
            } while (stringWidth > width && fontSize > 9);

            return font;
        }
    }

    private void notifyListeners() {
        synchronized (this.listeners) {
            if (this.listeners.size() != 0) {
                ItemEvent event = new ItemEvent(this, 0, this, 701);
                for (ItemListener listener : listeners) {
                    listener.itemStateChanged(event);
                }
            }
        }
    }

    private Color adjustColorForDisabled(Color color) {
        if (this.isEnabled()) {
            return color;
        } else {
            Color background = this.getBackground();
            int r = (color.getRed() + background.getRed() * 2) / 3;
            int g = (color.getGreen() + background.getGreen() * 2) / 3;
            int b = (color.getBlue() + background.getBlue() * 2) / 3;
            return new Color(r, g, b);
        }
    }

    private void drawBorders(
            Graphics graphics,
            int x,
            int y,
            int width,
            int height,
            Color borderColor,
            Color borderColorLight,
            Color borderColorDark) {
        graphics.setColor(borderColor);
        graphics.fillRect(x, y, width, height);
        graphics.setColor(borderColorLight);
        graphics.drawRect(x, y, width - 1, height - 1);
        graphics.setColor(borderColorDark);
        graphics.drawLine(x, y, x + width - 2, y);
        graphics.drawLine(x, y, x, y + height - 1);
    }
}
