package com.aapeli.colorgui;

import com.aapeli.client.IPanel;
import com.aapeli.client.StringDraw;
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
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

public final class Spinner extends IPanel implements MouseListener, MouseMotionListener, ItemSelectable {

    private final List<String> items = new ArrayList<>();
    private int selectedIndex = -1;
    private int mouseClickMode = 0; // -1 == left control, +1 == right control
    private boolean mouseHoveredOver;
    private boolean mouseIsBeingDraggedOverValues;
    private boolean useNonDefaultFont;
    private Font font;
    private int fontSize;
    private final List<ItemListener> listeners;
    private Image image;
    private Graphics graphics;
    private int width;
    private int height;
    private SpinnerStateChangeDebounceThread debouceThread;
    private int stateChangeNotificationDelay;
    private final Object lock;

    public Spinner() {
        this.mouseHoveredOver = this.mouseIsBeingDraggedOverValues = false;
        this.useNonDefaultFont = false;
        this.font = null;
        this.fontSize = -1;
        this.debouceThread = null;
        this.stateChangeNotificationDelay = 0;
        this.lock = new Object();
        this.listeners = new ArrayList<>();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.width = this.height = -1;
    }

    public void update(Graphics g) {
        Dimension size = this.getSize();
        int width = size.width;
        int height = size.height;
        if (width > 0 && height > 0) {
            if (this.image == null || width != this.width || height != this.height) {
                this.image = this.createImage(width, height);
                this.graphics = this.image.getGraphics();
                this.width = width;
                this.height = height;
            }

            this.drawBackground(this.graphics);
            Color backgroundColor = this.getBackground();
            this.graphics.setColor(backgroundColor);
            this.graphics.fillRect(0, 0, width, height);
            Color foregroundColor = this.getForeground();
            if (this.mouseHoveredOver) {
                this.graphics.setColor(backgroundColor);
                this.graphics.drawLine(0, height - 1, width - 1, height - 1);
                this.graphics.setColor(this.getBlendedColor(backgroundColor, foregroundColor));
                double highlightedItemWidth = (double) (width - height * 2) / (double) this.items.size();
                this.graphics.drawLine(
                        (int) ((double) height + (double) this.selectedIndex * highlightedItemWidth + 0.5D),
                        height - 2,
                        (int) ((double) height + (double) (this.selectedIndex + 1) * highlightedItemWidth + 0.5D),
                        height - 2);
            }

            this.graphics.setColor(this.translateColor(backgroundColor, 24));
            this.graphics.drawLine(0, 0, width - 1, 0);
            this.graphics.setColor(this.translateColor(backgroundColor, -36));
            this.graphics.drawLine(0, height - 1, width - 1, height - 1);
            this.drawControlButtons(this.graphics, 0, 0, height, height, true, this.mouseClickMode == -1);
            this.drawControlButtons(this.graphics, width - height, 0, height, height, false, this.mouseClickMode == 1);
            this.graphics.setColor(foregroundColor);
            String selectedItemText = this.getSelectedItem();
            if (selectedItemText != null) {
                Font font = this.getFont();
                this.graphics.setFont(font);
                StringDraw.drawString(
                        this.graphics, selectedItemText, width / 2, height / 2 + font.getSize() * 3 / 8 + 1, 0);
            }

            g.drawImage(this.image, 0, 0, this);
        }
    }

    public void setFont(Font font) {
        this.useNonDefaultFont = true;
        super.setFont(font);
        this.repaint();
    }

    public Font getFont() {
        if (this.useNonDefaultFont) {
            return super.getFont();
        } else {
            int fontSize = this.getSize().height - 9;
            if (fontSize < 9) {
                fontSize = 9;
            }

            if (this.font == null || fontSize != this.fontSize) {
                this.font = new Font("Dialog", Font.PLAIN, fontSize);
                this.fontSize = fontSize;
            }

            return this.font;
        }
    }

    public void mouseEntered(MouseEvent e) {
        this.mouseHoveredOver = true;
        this.repaint();
    }

    public void mouseExited(MouseEvent e) {
        this.mouseHoveredOver = false;
        this.mouseClickMode = 0;
        this.repaint();
    }

    public void mousePressed(MouseEvent e) {
        synchronized (this.lock) {
            Dimension size = this.getSize();
            int width = size.width;
            int arrowButtonWidth = size.height;
            int x = e.getX();
            int itemsCount = this.items.size();
            if (x < arrowButtonWidth) {
                --this.selectedIndex;
                if (this.selectedIndex < 0) {
                    this.selectedIndex = itemsCount - 1;
                }

                this.mouseClickMode = -1;
                this.mouseIsBeingDraggedOverValues = false;
            } else if (x >= width - arrowButtonWidth) {
                ++this.selectedIndex;
                if (this.selectedIndex >= itemsCount) {
                    this.selectedIndex = 0;
                }

                this.mouseClickMode = 1;
                this.mouseIsBeingDraggedOverValues = false;
            } else {
                int index = (x - arrowButtonWidth) * itemsCount / (width - arrowButtonWidth * 2);
                this.mouseIsBeingDraggedOverValues = true;
                if (index == this.selectedIndex) {
                    return;
                }

                this.selectedIndex = index;
            }
        }

        this.repaint();
        this.notifyListenersWithDelay();
    }

    public void mouseReleased(MouseEvent e) {
        if (this.mouseClickMode != 0) {
            this.mouseClickMode = 0;
            this.repaint();
        }

        this.mouseIsBeingDraggedOverValues = false;
    }

    public void mouseClicked(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {
        synchronized (this.lock) {
            if (this.mouseIsBeingDraggedOverValues) {
                Dimension size = this.getSize();
                int itemsCount = this.items.size();
                int index = (e.getX() - size.height) * itemsCount / (size.width - size.height * 2);
                if (index < 0) {
                    index = 0;
                } else if (index >= itemsCount) {
                    index = itemsCount - 1;
                }

                if (index != this.selectedIndex) {
                    this.selectedIndex = index;
                    this.repaint();
                    this.notifyListenersWithDelay();
                }
            }
        }
    }

    public Object[] getSelectedObjects() {
        String selectedItem = this.getSelectedItem();
        return selectedItem == null ? null : new Object[] {selectedItem};
    }

    public int addItem(String item) {
        synchronized (this.items) {
            this.items.add(item);
            if (this.selectedIndex == -1) {
                this.selectedIndex = 0;
                this.repaint();
            }

            return this.items.size() - 1;
        }
    }

    public String getItem(int i) {
        synchronized (this.items) {
            return this.items.get(i);
        }
    }

    public String removeItem(int i) {
        synchronized (this.items) {
            String item = this.items.get(i);
            this.items.remove(i);
            if (this.selectedIndex >= i) {
                --this.selectedIndex;
                if (this.selectedIndex == -1 && !this.items.isEmpty()) {
                    this.selectedIndex = 0;
                }

                this.repaint();
            }

            return item;
        }
    }

    public boolean removeAllItems() {
        synchronized (this.items) {
            if (!this.items.isEmpty()) {
                this.items.clear();
                this.selectedIndex = -1;
                this.repaint();
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean setSelectedIndex(int i) {
        synchronized (this.items) {
            if (i >= 0 && i < this.items.size()) {
                if (i != this.selectedIndex) {
                    this.selectedIndex = i;
                    this.repaint();
                    return true;
                } else {
                    return false;
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public String getSelectedItem() {
        synchronized (this.items) {
            return this.selectedIndex == -1 ? null : this.getItem(this.selectedIndex);
        }
    }

    public int getItemCount() {
        return this.items.size();
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

    public void setItemEventProcessDelay(int stateChangeNotificationDelay) {
        if (stateChangeNotificationDelay < 0) {
            throw new IllegalArgumentException();
        } else {
            this.stateChangeNotificationDelay = stateChangeNotificationDelay;
        }
    }

    private void drawControlButtons(
            Graphics g, int x, int y, int width, int height, boolean arrowFacingLeft, boolean mouseClickedOver) {
        Color backgroundColor = this.translateColor(this.getBackground(), -32);
        g.setColor(backgroundColor);
        g.fillRect(x, y, width, height);
        double arrowStartX = (double) x + (double) width / 3.0D + 0.5D;
        double arrowEndX = (double) x + (double) width * 2.0D / 3.0D - 0.5D;
        int[] arrowXCoords = new int[] {
            (int) (arrowFacingLeft ? arrowEndX : arrowStartX),
            (int) (arrowFacingLeft ? arrowEndX : arrowStartX),
            (int) (arrowFacingLeft ? arrowStartX : arrowEndX)
        };
        int[] arrowYCoords = new int[] {
            (int) ((double) y + (double) height / 3.0D),
            (int) ((double) y + (double) height * 2.0D / 3.0D),
            (int) ((double) y + (double) height / 2.0D)
        };
        int[] arrowXBorder = new int[] {
            arrowXCoords[0] + (arrowFacingLeft ? 1 : -1),
            arrowXCoords[1] + (arrowFacingLeft ? 1 : -1),
            arrowXCoords[2] + (arrowFacingLeft ? -1 : 1)
        };
        int[] arrowYBorder = new int[] {arrowYCoords[0] - 1, arrowYCoords[1] + 1, arrowYCoords[2]};
        Color foregroundColor = this.getForeground();
        g.setColor(this.getBlendedColor(backgroundColor, foregroundColor));
        g.fillPolygon(arrowXBorder, arrowYBorder, 3);
        g.setColor(foregroundColor);
        g.fillPolygon(arrowXCoords, arrowYCoords, 3);
        Color borderColor1 = this.translateColor(backgroundColor, 24);
        Color borderColor2 = this.translateColor(backgroundColor, -36);
        g.setColor(mouseClickedOver ? borderColor2 : borderColor1);
        g.drawRect(x, y, width - 1, height - 1);
        g.drawRect(x + 1, y + 1, width - 3, height - 3);
        g.setColor(mouseClickedOver ? borderColor1 : borderColor2);
        g.drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
        g.drawLine(x + 2, y + height - 2, x + width - 1, y + height - 2);
        g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
        g.drawLine(x + width - 2, y + 1, x + width - 2, y + height - 1);
    }

    private Color translateColor(Color color, int offset) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        if (r == 0 && g == 0 && b == 0 && offset < 0) {
            return new Color(16, 16, 16);
        } else if (r == 255 && g == 255 && b == 255 && offset > 0) {
            return new Color(240, 240, 240);
        } else {
            r = this.translateColorChannel(r, offset);
            g = this.translateColorChannel(g, offset);
            b = this.translateColorChannel(b, offset);
            return new Color(r, g, b);
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

    private Color getBlendedColor(Color color1, Color color2) {
        int r = (color1.getRed() * 2 + color2.getRed()) / 3;
        int g = (color1.getGreen() * 2 + color2.getGreen()) / 3;
        int b = (color1.getBlue() * 2 + color2.getBlue()) / 3;
        return new Color(r, g, b);
    }

    private void notifyListenersWithDelay() {
        synchronized (this.listeners) {
            if (!this.listeners.isEmpty()) {
                if (this.debouceThread != null) {
                    this.debouceThread.stop();
                    this.debouceThread = null;
                }

                if (this.stateChangeNotificationDelay == 0) {
                    this.notifyListeners();
                } else {
                    this.debouceThread = new SpinnerStateChangeDebounceThread(this, this.stateChangeNotificationDelay);
                }
            }
        }
    }

    void notifyListeners() {
        String selectedItem = this.getSelectedItem();
        if (selectedItem != null) {
            ItemEvent event = new ItemEvent(this, 701, selectedItem, ItemEvent.SELECTED);
            for (ItemListener listener : listeners) {
                listener.itemStateChanged(event);
            }
        }
    }
}
