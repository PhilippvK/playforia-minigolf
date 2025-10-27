package com.aapeli.colorgui;

import com.aapeli.client.IPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

public class Button extends IPanel implements MouseMotionListener, MouseListener {

    public static final int BORDER_NONE = 0;
    public static final int BORDER_NORMAL = 1;
    public static final int BORDER_THICK = 2;
    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(192, 192, 192);
    private Color backgroundColor;
    private Color foregroundColor;
    private Color highlightedBackgroundColor;
    private Color highlightedBorderColorDefault;
    private Color borderColorDefault;
    private Color borderColor;
    private Image backgroundImage;
    private Image highlightedBackgroundImage;
    private Image foregroundImage;
    private Image highlightedForegroundImage;
    private Image iconImage;
    private int backgroundImageOffsetX;
    private int backgroundImageOffsetY;
    private int foregroundImageOffsetX;
    private int foregroundImageOffsetY;
    private int iconImageWidth;
    private int iconImageHeight;
    private boolean backgroundImageNoOffsets;
    private String label;
    private String secondaryLabel;
    private Font font;
    private Font secondaryFont;
    private boolean backgroundGradient;
    private boolean mouseHoveredOver;
    private boolean mousePressed;
    private int borderStyle;
    private List<ActionListener> listeners;
    private Image image;
    private Graphics graphics;
    private int width;
    private int height;

    public Button(String label) {
        this.setBackground(DEFAULT_BACKGROUND_COLOR);
        this.setForeground(FontConstants.black);
        this.setFont(FontConstants.font);
        this.setLabel(label);
        this.setSecondaryFont(new Font("Dialog", Font.PLAIN, 11));
        this.setSecondaryLabel(null);
        this.backgroundGradient = true;
        this.mouseHoveredOver = false;
        this.mousePressed = false;
        this.borderStyle = BORDER_NORMAL;
        this.listeners = new ArrayList<>();
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
    }

    public void addNotify() {
        super.addNotify();
        this.mouseHoveredOver = false;
        this.mousePressed = false;
        this.repaint();
    }

    public void update(Graphics g) {
        Dimension buttonSize = this.getSize();
        int width = buttonSize.width;
        int height = buttonSize.height;
        if (this.image == null || width != this.width || height != this.height) {
            this.image = this.createBuffer(width, height);
            this.graphics = this.image.getGraphics();
            this.width = width;
            this.height = height;
        }

        this.drawBackground(this.graphics);
        boolean highlighted = this.isHighlighted();
        if (this.backgroundImage != null && this.highlightedBackgroundImage != null) {
            Image backgroundImage = highlighted ? this.highlightedBackgroundImage : this.backgroundImage;
            if (!this.backgroundImageNoOffsets) {
                this.graphics.drawImage(
                        backgroundImage,
                        0,
                        0,
                        width,
                        height,
                        this.backgroundImageOffsetX,
                        this.backgroundImageOffsetY,
                        this.backgroundImageOffsetX + width,
                        this.backgroundImageOffsetY + height,
                        this);
            } else {
                this.graphics.drawImage(backgroundImage, 0, 0, width, height, this);
            }
        } else {
            Color color = highlighted ? this.highlightedBackgroundColor : this.backgroundColor;
            if (this.backgroundGradient) {
                this.drawGradient(this.graphics, color, width, height);
            } else {
                this.graphics.setColor(color);
                this.clearBackground(this.graphics, width, height);
            }
        }

        if (this.borderColor != null) {
            this.graphics.setColor(this.borderColor);
        }

        this.drawBorder(this.graphics, width, height);
        if (this.foregroundImage != null) {
            this.graphics.drawImage(
                    this.isNormalState() ? this.foregroundImage : this.highlightedForegroundImage,
                    this.foregroundImageOffsetX > 0 ? width / 2 - this.foregroundImageOffsetX / 2 : 0,
                    this.foregroundImageOffsetY > 0 ? height / 2 - this.foregroundImageOffsetY / 2 : 0,
                    this);
        }

        int labelWidth = width / 2;
        int iconImageWidthNeeded = 0;
        int size;
        int x;
        if (this.iconImage != null) {
            size = (height - this.iconImageHeight) / 2;
            x = this.drawIcon(this.graphics, this.iconImage, size);
            labelWidth = width / 2 + size + this.iconImageWidth / 2 - 1;
            iconImageWidthNeeded = x + this.iconImageWidth + 1;
        }

        if (this.label != null) {
            this.graphics.setColor(this.foregroundColor);
            if (this.secondaryLabel == null) {
                Font sizeAdjustedFont = this.getSizeAdjustedFont(this.getFont(this.font), this.label, width - 2);
                this.graphics.setFont(sizeAdjustedFont);
                x = labelWidth - this.getFontMetrics(sizeAdjustedFont).stringWidth(this.label) / 2;
                if (x < iconImageWidthNeeded) {
                    x = iconImageWidthNeeded;
                }

                this.graphics.drawString(this.label, x, height / 2 + sizeAdjustedFont.getSize() * 3 / 8 + 1);
            } else {
                size = this.font.getSize();
                x = this.secondaryFont.getSize();
                if (x > size) {
                    size = x;
                }

                String label = this.label + "  ";
                String secondaryLabel = "  " + this.secondaryLabel;
                Font sizeAdjustedFont =
                        this.getSizeAdjustedFont(this.getFont(this.font), label + secondaryLabel, width - 2);
                Font sizeAdjustedSecondaryFont =
                        this.getSizeAdjustedFont(this.getFont(this.secondaryFont), label + secondaryLabel, width - 2);
                int labelTextWidth = this.getFontMetrics(sizeAdjustedFont).stringWidth(label);
                int secondaryLabelTextWidth =
                        this.getFontMetrics(sizeAdjustedSecondaryFont).stringWidth(secondaryLabel);
                this.graphics.setFont(sizeAdjustedFont);
                this.graphics.drawString(
                        label,
                        labelWidth - (labelTextWidth + secondaryLabelTextWidth) / 2,
                        height / 2 + size * 3 / 8 + 1);
                this.graphics.setFont(sizeAdjustedSecondaryFont);
                this.graphics.drawString(
                        secondaryLabel,
                        labelWidth - (labelTextWidth + secondaryLabelTextWidth) / 2 + labelTextWidth,
                        height / 2 + size * 3 / 8 + 1);
            }
        }

        g.drawImage(this.image, 0, 0, this);
    }

    public void mouseEntered(MouseEvent e) {
        this.mouseHoveredOver = true;
        this.repaint();
    }

    public void mouseExited(MouseEvent e) {
        this.mouseHoveredOver = false;
        this.mousePressed = false;
        this.repaint();
    }

    public void mousePressed(MouseEvent e) {
        this.mousePressed = true;
        this.repaint();
    }

    public void mouseReleased(MouseEvent e) {
        boolean mousePressed = this.mousePressed;
        this.mousePressed = false;
        this.repaint();
        if (mousePressed) {
            this.processActionEvent();
        }
    }

    public void mouseClicked(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {}

    public void setBackground(Color background) {
        if (background == null) {
            background = DEFAULT_BACKGROUND_COLOR;
        }

        super.setBackground(background);
        this.backgroundColor = background;
        this.highlightedBackgroundColor = this.translateColor(background, 32);
        this.highlightedBorderColorDefault = this.translateColor(background, 48);
        this.borderColorDefault = this.translateColor(background, -48);
        this.repaint();
    }

    public void setBackgroundGradient(boolean gradient) {
        this.backgroundGradient = gradient;
    }

    public void setForeground(Color foreground) {
        if (foreground == null) {
            foreground = FontConstants.black;
        }

        this.foregroundColor = foreground;
        this.repaint();
    }

    public void setBackgroundImage(Image image) {
        this.setBackgroundImage(image, image, 0, 0);
    }

    public void setBackgroundImage(Image backgroundImage, Image highlightedBackgroundImage) {
        this.setBackgroundImage(backgroundImage, highlightedBackgroundImage, 0, 0);
    }

    public void setBackgroundImage(Image image, int offsetX, int offsetY) {
        this.setBackgroundImage(image, image, offsetX, offsetY);
    }

    public void setBackgroundImage(Image backgroundImage, Image highlightedBackgroundImage, int offsetX, int offsetY) {
        this.backgroundImage = backgroundImage;
        this.highlightedBackgroundImage = highlightedBackgroundImage;
        this.backgroundImageOffsetX = offsetX;
        this.backgroundImageOffsetY = offsetY;
        this.backgroundImageNoOffsets = false;
        this.repaint();
    }

    public void setFittedBackgroundImage(Image backgroundImage, Image highlightedBackgroundImage) {
        this.backgroundImage = backgroundImage;
        this.highlightedBackgroundImage = highlightedBackgroundImage;
        this.backgroundImageNoOffsets = true;
        this.repaint();
    }

    public void setForegroundImage(Image image) {
        this.setForegroundImage(image, image, 0, 0);
    }

    public void setForegroundImage(Image foregroundImage, Image highlightedForegroundImage) {
        this.setForegroundImage(foregroundImage, highlightedForegroundImage, 0, 0);
    }

    public void setForegroundImage(Image foregroundImage, int offsetX, int offsetY) {
        this.setForegroundImage(foregroundImage, foregroundImage, offsetX, offsetY);
    }

    public void setForegroundImage(Image foregroundImage, Image highlightedForegroundImage, int offsetX, int offsetY) {
        this.foregroundImage = foregroundImage;
        this.highlightedForegroundImage = highlightedForegroundImage;
        this.foregroundImageOffsetX = offsetX;
        this.foregroundImageOffsetY = offsetY;
        this.repaint();
    }

    public void setIconImage(Image image) {
        if (image != null) {
            this.setIconImage(image, image.getWidth(null), image.getHeight(null));
        } else {
            this.setIconImage(null, -1, -1);
        }
    }

    public void setIconImage(Image image, int width, int height) {
        this.iconImage = image;
        this.iconImageWidth = width;
        this.iconImageHeight = height;
        this.repaint();
    }

    public void setFont(Font font) {
        this.font = font;
        this.repaint();
    }

    public void setLabel(String label) {
        this.label = label;
        this.repaint();
    }

    public String getLabel() {
        return this.label;
    }

    public void setSecondaryFont(Font secondaryFont) {
        this.secondaryFont = secondaryFont;
        this.repaint();
    }

    public void setSecondaryLabel(String secondaryLabel) {
        if (secondaryLabel != this.secondaryLabel) {
            this.secondaryLabel = secondaryLabel;
            this.repaint();
        }
    }

    public String getSecondaryLabel() {
        return this.secondaryLabel;
    }

    public void setBorder(int borderStyle) {
        this.borderStyle = borderStyle;
    }

    public int getBorder() {
        return this.borderStyle;
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        this.repaint();
    }

    public void click() {
        this.mousePressed = true;
        this.mouseReleased(null);
    }

    public Dimension getPreferredSize() {
        return new Dimension(
                13 + this.getFontMetrics(this.font).stringWidth(this.label) + 13, 5 + this.font.getSize() + 5);
    }

    public void addActionListener(ActionListener listener) {
        synchronized (this.listeners) {
            this.listeners.add(listener);
        }
    }

    public void removeActionListener(ActionListener listener) {
        synchronized (this.listeners) {
            this.listeners.remove(listener);
        }
    }

    public Image createBuffer(int width, int height) {
        return this.createImage(width, height);
    }

    public boolean isNormalState() {
        return !this.mousePressed;
    }

    public void processActionEvent() {
        synchronized (this.listeners) {
            if (this.listeners.size() != 0) {
                ActionEvent event = new ActionEvent(this, 1001, this.label);
                for (ActionListener listener : listeners) {
                    listener.actionPerformed(event);
                }
            }
        }
    }

    public boolean isHighlighted() {
        return this.mouseHoveredOver;
    }

    public boolean isBolded() {
        return false;
    }

    public void clearBackground(Graphics g, int width, int height) {
        g.fillRect(0, 0, width, height);
    }

    public void drawBorder(Graphics g, int width, int height) {
        if (this.borderStyle != BORDER_NONE) {
            boolean isNormalState = this.isNormalState();
            boolean normalBorders = this.borderStyle == BORDER_NORMAL;
            if (this.borderColor == null) {
                g.setColor(isNormalState ? this.borderColorDefault : this.highlightedBorderColorDefault);
            }

            if (normalBorders) {
                g.drawRect(0, 0, width - 1, height - 1);
            } else {
                g.drawRect(0, 0, width - 1, height - 1);
                g.drawRect(1, 1, width - 3, height - 3);
            }

            if (this.borderColor == null) {
                g.setColor(isNormalState ? this.highlightedBorderColorDefault : this.borderColorDefault);
            }

            if (normalBorders) {
                g.drawLine(0, 0, width - 1, 0);
                g.drawLine(0, 0, 0, height - 1);
            } else {
                g.drawLine(0, 0, width - 2, 0);
                g.drawLine(0, 1, width - 3, 1);
                g.drawLine(0, 0, 0, height - 1);
                g.drawLine(1, 0, 1, height - 2);
            }
        }
    }

    public int drawIcon(Graphics g, Image image, int size) {
        g.drawImage(image, size, size, this);
        return size;
    }

    public Color[] getLightAndDarkBorderColors() {
        return new Color[] {this.highlightedBorderColorDefault, this.borderColorDefault};
    }

    private Color translateColor(Color color, int offset) {
        int r = color.getRed() + offset;
        int g = color.getGreen() + offset;
        int b = color.getBlue() + offset;
        if (r < 0) {
            r = 0;
        } else if (r > 255) {
            r = 255;
        }

        if (g < 0) {
            g = 0;
        } else if (g > 255) {
            g = 255;
        }

        if (b < 0) {
            b = 0;
        } else if (b > 255) {
            b = 255;
        }

        return new Color(r, g, b);
    }

    private void drawGradient(Graphics graphics, Color color, int width, int height) {
        byte borderSize = 0;
        if (this.borderStyle != BORDER_NONE) {
            borderSize = 1;
        }

        int originalR = color.getRed();
        int originalG = color.getGreen();
        int originalB = color.getBlue();
        int r = originalR;
        int g = originalG;
        int b = originalB;

        for (int i = height / 2; i >= borderSize; --i) {
            graphics.setColor(new Color(r, g, b));
            graphics.drawLine(borderSize, i, width - 1 - borderSize, i);
            r = this.translateColorChannel(r, 3);
            g = this.translateColorChannel(g, 3);
            b = this.translateColorChannel(b, 3);
        }

        r = originalR;
        g = originalG;
        b = originalB;

        for (int i = height / 2 + 1; i < height - borderSize; ++i) {
            r = this.translateColorChannel(r, -3);
            g = this.translateColorChannel(g, -3);
            b = this.translateColorChannel(b, -3);
            graphics.setColor(new Color(r, g, b));
            graphics.drawLine(borderSize, i, width - 1 - borderSize, i);
        }
    }

    private int translateColorChannel(int original, int offset) {
        original += offset;
        if (original < 0) {
            original = 0;
        } else if (original > 255) {
            original = 255;
        }

        return original;
    }

    private Font getFont(Font font) {
        return this.isBolded() ? new Font(font.getName(), Font.BOLD, font.getSize()) : font;
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
}
