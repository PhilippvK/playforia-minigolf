package com.aapeli.colorgui;

import com.aapeli.client.IPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TextArea extends IPanel implements ComponentListener, AdjustmentListener {

    public static final int COLOR_BLACK = 0;
    public static final int COLOR_RED = 1;
    public static final int COLOR_GREEN = 2;
    public static final int COLOR_BLUE = 3;
    public static final int COLOR_YELLOW = 4;
    public static final int COLOR_MAGENTA = 5;
    public static final int COLOR_CYAN = 6;
    public static final int COLOR_GRAY = 7;
    public static final int COLOR_WHITE = 8;
    public static final int BORDER_NONE = 0;
    public static final int BORDER_BEVELED = 1;
    public static final int BORDER_NONE_ROUNDCORNER = 2;
    private static final Color[] colors = new Color[] {
        new Color(0, 0, 0),
        new Color(224, 0, 0),
        new Color(0, 160, 0),
        new Color(0, 0, 240),
        new Color(160, 128, 0),
        new Color(160, 0, 160),
        new Color(0, 144, 160),
        new Color(112, 112, 112),
        new Color(255, 255, 255)
    };
    private static final Color backgroundImageBorderColor = new Color(255, 255, 255);
    private static final Color borderLight = new Color(192, 192, 192);
    private static final Color borderDark = new Color(64, 64, 64);
    private Scrollbar scrollbar;
    private boolean hasScrollbar;
    private Image backgroundImage;
    private int backgroundOffsetX;
    private int backgroundOffsetY;
    private Font font;
    private Font fontBold;
    private FontMetrics fontMetrics;
    private int fontSize;
    private int width;
    private int height;
    private int maxLineWidth;
    private int lineHeight;
    private int scrollWindowNumberOfLines;
    private List<TextAreaText> lines;
    private List<TextAreaText> texts;
    private Image image;
    private Graphics graphics;
    private int imageWidth;
    private int imageHeight;
    private int borderStyle;
    private Object synchronizationObject;

    public TextArea(int width, int height, Font font) {
        this.synchronizationObject = new Object();
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        if (font == null) {
            font = FontConstants.font;
        }

        this.font = font;
        this.fontMetrics = this.getFontMetrics(font);
        this.fontSize = font.getSize();
        this.fontBold = new Font(font.getName(), Font.BOLD, font.getSize());
        this.lines = new ArrayList<>();
        this.texts = new ArrayList<>();
        this.backgroundImage = null;
        this.lineHeight = this.fontSize + 3;
        this.calculateLineStats();
        this.borderStyle = 1;
        this.addComponentListener(this);
        this.setLayout(null);
        this.scrollbar = new Scrollbar(1);
        this.calculateScrollbarStats();
        this.scrollbar.setUnitIncrement(1);
        this.hasScrollbar = false;
    }

    public void update(Graphics g) {
        if (this.image == null || this.width != this.imageWidth || this.height != this.imageHeight) {
            this.image = this.createImage(this.width, this.height);
            this.graphics = this.image.getGraphics();
            this.imageWidth = this.width;
            this.imageHeight = this.height;
        }

        if (this.backgroundImage == null) {
            this.graphics.setColor(backgroundImageBorderColor);
            if (this.borderStyle == 2) {
                this.drawBackground(this.graphics);
                this.graphics.fillRect(4, 0, this.width - 8, this.height);
                this.graphics.fillRect(2, 1, this.width - 4, this.height - 2);
                this.graphics.fillRect(1, 2, this.width - 2, this.height - 4);
                this.graphics.fillRect(0, 4, this.width, this.height - 8);
            } else {
                this.graphics.fillRect(0, 0, this.width, this.height);
            }
        } else {
            this.drawBackground(this.graphics);
            this.graphics.drawImage(
                    this.backgroundImage,
                    0,
                    0,
                    this.width,
                    this.height,
                    this.backgroundOffsetX,
                    this.backgroundOffsetY,
                    this.backgroundOffsetX + this.width,
                    this.backgroundOffsetY + this.height,
                    this);
        }

        synchronized (this.synchronizationObject) {
            int linesCount = this.lines.size();
            if (linesCount > 0) {
                int y = this.fontSize;
                int scrollbarOffset = this.hasScrollbar ? this.scrollbar.getValue() : 0;

                for (int i = 0; i <= this.scrollWindowNumberOfLines && scrollbarOffset < linesCount; ++i) {
                    TextAreaText text = this.lines.get(scrollbarOffset);
                    if (!text.isTextEmpty()) {
                        this.graphics.setFont(text.isBold() ? this.fontBold : this.font);
                        this.graphics.setColor(text.getColor());
                        this.graphics.drawString(text.getText(), 3, y);
                    }

                    y += this.lineHeight;
                    ++scrollbarOffset;
                }
            }
        }

        if (this.borderStyle == 1) {
            this.graphics.setColor(borderLight);
            this.graphics.drawRect(0, 0, this.width - 1, this.height - 1);
            this.graphics.setColor(borderDark);
            this.graphics.drawLine(0, 0, this.width - 1, 0);
            this.graphics.drawLine(0, 0, 0, this.height - 1);
        }

        g.drawImage(this.image, 0, 0, this);
    }

    public void componentShown(ComponentEvent e) {}

    public void componentHidden(ComponentEvent e) {}

    public void componentMoved(ComponentEvent e) {}

    public void componentResized(ComponentEvent e) {
        Dimension size = this.getSize();
        this.width = size.width;
        this.height = size.height;
        this.resized();
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        this.repaint();
    }

    public void setBackgroundImage(Image image) {
        this.setBackgroundImage(image, 0, 0);
    }

    public void setBackgroundImage(Image image, int offsetX, int offsetY) {
        this.backgroundImage = image;
        this.backgroundOffsetX = offsetX;
        this.backgroundOffsetY = offsetY;
        this.repaint();
    }

    public void clear() {
        this.reset(true);
    }

    public void addText() {
        this.addText(null, null, false);
    }

    public void addText(int i, String text) {
        this.addText(colors[i], text, false);
    }

    public void addText(Color color, String text) {
        this.addText(color, text, false);
    }

    public void addText(int i, String text, boolean scrollToBottom) {
        this.addText(colors[i], text, scrollToBottom);
    }

    public void addText(Color color, String text, boolean scrollToBottom) {
        this.addText(color, text, false, scrollToBottom);
    }

    public void addImportantLine(int i, String text) {
        this.addText(colors[i], text, true, true);
    }

    public String[] getTextWithTimestamps() {
        synchronized (this.synchronizationObject) {
            int textCount = this.texts.size();
            String[] textWithTimestamps = new String[textCount];
            if (textCount > 0) {
                for (int i = 0; i < textCount; ++i) {
                    TextAreaText text = this.texts.get(i);
                    if (text.isTextEmpty()) {
                        textWithTimestamps[i] = "";
                    } else {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date(text.getCreated()));
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);
                        textWithTimestamps[i] = "["
                                + (hour < 10 ? "0" : "")
                                + hour
                                + ":"
                                + (minute < 10 ? "0" : "")
                                + minute
                                + "] "
                                + text.getText();
                    }
                }
            }

            return textWithTimestamps;
        }
    }

    public void setDrawBorders(boolean shouldDrawBorder) {
        this.setBorderStyle(shouldDrawBorder ? 1 : 0);
    }

    public void setBorderStyle(int borderStyle) {
        this.borderStyle = borderStyle;
        this.repaint();
    }

    public Font getFont() {
        return this.font;
    }

    private void reset(boolean clearText) {
        synchronized (this.synchronizationObject) {
            this.lines.clear();
            if (clearText) {
                this.texts.clear();
            }

            this.remove(this.scrollbar);
            this.hasScrollbar = false;
        }

        this.repaint();
    }

    private void resized() {
        synchronized (this.synchronizationObject) {
            this.calculateLineStats();
            this.calculateScrollbarStats();
            this.reset(false);
            int textsLength = this.texts.size();
            if (textsLength == 0) {
                return;
            }

            int i = 0;

            while (true) {
                if (i >= textsLength) {
                    this.updateScrollWindow(0, true);
                    break;
                }

                TextAreaText text = this.texts.get(i);
                this.splitTextToLines(text.getColor(), text.getText(), text.isBold());
                ++i;
            }
        }

        this.repaint();
    }

    private void calculateLineStats() {
        this.maxLineWidth = this.width - 6 - 16;
        this.scrollWindowNumberOfLines = this.height / this.lineHeight;
    }

    private void calculateScrollbarStats() {
        this.scrollbar.setBounds(this.width - 16 - 1, 1, 16, this.height - 2);
        this.scrollbar.setBlockIncrement(this.scrollWindowNumberOfLines - 1);
    }

    private void addText(Color color, String text, boolean bold, boolean scrollToBottom) {
        synchronized (this.synchronizationObject) {
            this.texts.add(new TextAreaText(color, text, bold));
            int lineCount = this.lines.size();
            this.splitTextToLines(color, text, bold);
            this.updateScrollWindow(lineCount, scrollToBottom);
        }

        this.repaint();
    }

    private void splitTextToLines(Color color, String text, boolean bold) {
        synchronized (this.synchronizationObject) {
            int textWidth = text != null ? this.fontMetrics.stringWidth(text) : 0;
            if (textWidth <= this.maxLineWidth) {
                this.addLine(color, text, bold);
            } else {
                int length = text.length();
                int i = length - 1;

                while (this.fontMetrics.stringWidth(text.substring(0, i)) > this.maxLineWidth) {
                    --i;
                    if (i <= 5) {
                        this.addLine(color, text, bold);
                        return;
                    }
                }

                int j = i;
                while (j > 3 && text.charAt(j) != ' ') {
                    --j;
                }

                if (j == 3) {
                    j = i;
                }

                this.addLine(color, text.substring(0, j), bold);
                this.splitTextToLines(color, (text.charAt(j) == ' ' ? " " : "  ") + text.substring(j), bold);
            }
        }
    }

    private void addLine(Color color, String text, boolean bold) {
        synchronized (this.synchronizationObject) {
            this.lines.add(new TextAreaText(color, text, bold));
        }
    }

    private void updateScrollWindow(int scrollbarPosition, boolean scrollToBottom) {
        synchronized (this.synchronizationObject) {
            int linesCount = this.lines.size();
            if (linesCount > this.scrollWindowNumberOfLines) {
                int newScrollbarOffset = linesCount - this.scrollWindowNumberOfLines;
                if (!this.hasScrollbar) {
                    this.add(this.scrollbar);
                    this.scrollbar.addAdjustmentListener(this);
                    this.hasScrollbar = true;
                } else {
                    int currentScrollbarOffset = this.scrollbar.getValue();
                    if (!scrollToBottom
                            && currentScrollbarOffset + this.scrollWindowNumberOfLines < scrollbarPosition) {
                        newScrollbarOffset = currentScrollbarOffset;
                    }
                }

                this.scrollbar.setValues(newScrollbarOffset, this.scrollWindowNumberOfLines, 0, linesCount);
            }
        }
    }
}
