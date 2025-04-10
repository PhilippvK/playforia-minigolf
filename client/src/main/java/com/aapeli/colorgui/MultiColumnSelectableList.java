package com.aapeli.colorgui;

import com.aapeli.client.StringDraw;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.ItemSelectable;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class MultiColumnSelectableList<T> extends Panel implements AdjustmentListener, MouseListener, ItemSelectable {
    public static final int SELECTABLE_NONE = 0;
    public static final int SELECTABLE_ONE = 1;
    public static final int SELECTABLE_MULTI = 2;
    public static final int ID_CLICKED = 0;
    public static final int ID_RIGHTCLICKED = 1;
    public static final int ID_DOUBLECLICKED = 2;
    private static final Font DEFAULT_BOLD_FONT = new Font("Dialog", Font.BOLD, 12);
    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color BORDER_COLOR_LIGHT = new Color(192, 192, 192);
    private static final Color BORDER_COLOR_DARK = new Color(64, 64, 64);
    private static final Color DEFAULT_FOREGROUND_COLOR = new Color(192, 192, 192);

    private Scrollbar scrollbar;
    private boolean scrollbarVisible;
    private Image backgroundImage;
    private int backgroundImageOffsetX;
    private int backgroundImageOffsetY;
    private Color backgroundColor;
    private Color foregroundColor;
    private FontMetrics fontMetrics;
    private String emptyListText;
    private String[] columnTitles;
    private SortOrder[] columnSortTypes;
    private int columnCount;
    private int sortColumnIndex;
    private int width;
    private int height;
    private int usableWidth;
    private int rows;
    private int[] columnWidths;
    private int selectable;
    private List<MultiColumnListItem<T>> items;
    private int lastMouseClickX;
    private int lastMouseClickY;
    private int rangeSelectionLastIndex;
    private int rangeSelectionState;
    private Image image;
    private Graphics imageGraphics;
    private List<ItemListener> listeners;
    private MultiColumnListListener listListener;

    public MultiColumnSelectableList(
            String[] columnTitles, SortOrder[] columnSortTypes, int sortColumnIndex, int width, int height) {
        this.columnTitles = columnTitles;
        this.columnSortTypes = columnSortTypes;
        this.sortColumnIndex = sortColumnIndex;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.emptyListText = null;
        this.columnCount = columnTitles != null ? columnTitles.length : 0;
        this.items = new ArrayList<>();
        this.selectable = 0;
        this.usableWidth = width - 6 - 16;
        this.rows = height / 16 - 1;
        this.foregroundColor = DEFAULT_FOREGROUND_COLOR;
        this.lastMouseClickX = this.lastMouseClickY = -1;
        this.rangeSelectionLastIndex = -1;
        this.backgroundColor = DEFAULT_BACKGROUND_COLOR;
        this.setLayout(null);
        this.scrollbar = new Scrollbar(1);
        this.scrollbar.setBounds(width - 16 - 1, 1, 16, height - 2);
        this.scrollbar.setBlockIncrement(this.rows - 1);
        this.scrollbar.setUnitIncrement(1);
        this.scrollbarVisible = false;
        this.addMouseListener(this);
        this.listeners = new ArrayList<>();
    }

    public void addNotify() {
        super.addNotify();
        this.repaint();
    }

    public void paint(Graphics graphics) {
        this.update(graphics);
    }

    public synchronized void update(Graphics graphics) {
        if (this.image == null) {
            this.image = this.createImage(this.width, this.height);
            this.imageGraphics = this.image.getGraphics();
        }

        if (this.backgroundImage == null) {
            this.imageGraphics.setColor(this.backgroundColor);
            this.imageGraphics.fillRect(0, 0, this.width, this.height);
        } else {
            this.imageGraphics.drawImage(
                    this.backgroundImage,
                    0,
                    0,
                    this.width,
                    this.height,
                    this.backgroundImageOffsetX,
                    this.backgroundImageOffsetY,
                    this.backgroundImageOffsetX + this.width,
                    this.backgroundImageOffsetY + this.height,
                    this);
        }

        this.columnWidths = null;
        int itemCount = this.items.size();
        byte rowHeight = 14;
        byte rowTextSize = rowHeight;
        this.imageGraphics.setFont(DEFAULT_BOLD_FONT);
        this.imageGraphics.setColor(this.foregroundColor);
        if (itemCount == 0) {
            String[][] text = new String[1][this.columnCount];

            for (int i = 0; i < this.columnCount; ++i) {
                text[0][i] = this.columnTitles[i];
            }

            this.columnWidths = this.getColumnWidths(text);

            for (int i = 0; i < this.columnCount; ++i) {
                this.imageGraphics.drawString(this.columnTitles[i], 3 + this.columnWidths[i], rowTextSize);
            }

            if (this.emptyListText != null) {
                this.imageGraphics.setFont(FontConstants.font);
                StringDraw.drawStringWithMaxWidth(
                        this.imageGraphics,
                        this.emptyListText,
                        this.width / 2,
                        this.height / 2,
                        0,
                        this.width * 9 / 10);
            }
        } else {
            Color[] colors = new Color[this.rows + 1];
            boolean[] bolds = new boolean[this.rows + 1];
            boolean[] selecteds = new boolean[this.rows + 1];
            String[][] strings = new String[this.rows + 1][this.columnCount];
            Image[][] images = new Image[this.rows + 1][this.columnCount];
            int itemIndexStart = this.scrollbarVisible ? this.scrollbar.getValue() : 0;
            int itemIndex = itemIndexStart;

            for (int i = 0; i < this.rows + 1 && itemIndex < itemCount; ++i) {
                MultiColumnListItem<T> item = this.getItem(itemIndex);
                colors[i] = item.getColor();
                bolds[i] = item.isBold();
                strings[i] = item.getStrings();
                images[i] = item.getImages();
                selecteds[i] = item.isSelected();
                ++itemIndex;
            }

            this.columnWidths = this.getColumnWidths(strings);

            for (int i = 0; i < this.columnCount; ++i) {
                this.imageGraphics.drawString(this.columnTitles[i], 3 + this.columnWidths[i], rowTextSize);
            }

            int y = rowTextSize + 16;
            itemIndex = itemIndexStart;

            for (int i = 0; i < this.rows + 1 && itemIndex < itemCount; ++i) {
                this.imageGraphics.setFont(bolds[i] ? DEFAULT_BOLD_FONT : FontConstants.font);
                if (selecteds[i]) {
                    this.imageGraphics.setColor(colors[i]);
                    this.imageGraphics.fillRect(1, y - 12 - 1, this.width - 2, 16);
                    this.imageGraphics.setColor(this.backgroundColor);
                } else {
                    this.imageGraphics.setColor(colors[i]);
                }

                for (int column = 0; column < this.columnCount; ++column) {
                    if (images[i][column] != null) {
                        this.imageGraphics.drawImage(
                                images[i][column],
                                3 + this.columnWidths[column] + 1,
                                y - rowHeight + (8 - images[i][column].getHeight(null) / 2) + 1,
                                this);
                    } else if (strings[i][column] != null) {
                        this.imageGraphics.drawString(strings[i][column], 3 + this.columnWidths[column], y);
                    }
                }

                y += 16;
                ++itemIndex;
            }
        }

        this.imageGraphics.setColor(BORDER_COLOR_LIGHT);
        this.imageGraphics.drawRect(0, 0, this.width - 1, this.height - 1);
        this.imageGraphics.setColor(BORDER_COLOR_DARK);
        this.imageGraphics.drawLine(0, 0, this.width - 1, 0);
        this.imageGraphics.drawLine(0, 0, 0, this.height - 1);
        graphics.drawImage(this.image, 0, 0, this);
    }

    public synchronized void mousePressed(MouseEvent evt) {
        int items = this.items.size();
        if (items != 0) {
            this.lastMouseClickX = evt.getX();
            this.lastMouseClickY = evt.getY();
            int y = this.lastMouseClickY - 12 - 4;
            int i;
            if (y < 0) {
                if (this.columnWidths != null) {
                    i = -1;
                    int x = evt.getX();

                    for (int column = 0; column < this.columnCount - 1 && i == -1; ++column) {
                        if (x >= this.columnWidths[column] && x < this.columnWidths[column + 1]) {
                            i = column;
                        }
                    }

                    if (i == -1 && x >= this.columnWidths[this.columnCount - 1]) {
                        i = this.columnCount - 1;
                    }

                    if (i >= 0) {
                        this.setSortColumnIndex(i);
                    }
                }

            } else {
                i = this.getItemIndex(y);
                if (i != -1) {
                    MultiColumnListItem<T> item = this.getItem(i);
                    boolean isRightClick = evt.getButton() == MouseEvent.BUTTON3;
                    boolean isDoubleClick = evt.getClickCount() == 2;
                    int eventId = isRightClick ? ID_RIGHTCLICKED : (isDoubleClick ? ID_DOUBLECLICKED : ID_CLICKED);
                    short newState = 701;
                    if (!item.isSelected()) {
                        if (this.selectable == SELECTABLE_NONE) {
                            return;
                        }

                        if (this.selectable == SELECTABLE_ONE) {
                            this.unselectAll();
                        }

                        item.setSelected(true);
                        newState = ItemEvent.SELECTED;
                    } else if (!isRightClick) {
                        item.setSelected(false);
                        newState = ItemEvent.DESELECTED;
                    }

                    if (this.selectable == SELECTABLE_MULTI) {
                        if (eventId == ID_CLICKED
                                && (newState == ItemEvent.SELECTED || newState == ItemEvent.DESELECTED)) {
                            if (this.rangeSelectionLastIndex >= 0 && evt.isShiftDown()) {
                                int startOfRange = Math.min(this.rangeSelectionLastIndex, i);
                                int endOfRange = Math.max(this.rangeSelectionLastIndex, i);

                                for (int j = startOfRange; j <= endOfRange; ++j) {
                                    this.getItem(j).setSelected(this.rangeSelectionState == 1);
                                }
                            }

                            this.rangeSelectionLastIndex = i;
                            this.rangeSelectionState = newState;
                        } else {
                            this.rangeSelectionLastIndex = -1;
                        }
                    }

                    if (isRightClick) {
                        this.update(this.getGraphics());
                    }

                    this.itemPressed(item, eventId, newState);
                    this.repaint();

                    if (isDoubleClick && listListener != null) {
                        listListener.mouseDoubleClicked(item);
                    }
                }
            }
        }
    }

    public void mouseReleased(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void adjustmentValueChanged(AdjustmentEvent e) {
        this.repaint();
    }

    public synchronized void addItemListener(ItemListener listener) {
        this.listeners.add(listener);
    }

    public synchronized void removeItemListener(ItemListener listener) {
        this.listeners.remove(listener);
    }

    public Object[] getSelectedObjects() {
        return this.getSelectedItems();
    }

    public void setSelectable(int selectable) {
        this.selectable = selectable;
        if (selectable == SELECTABLE_NONE) {
            this.unselectAll();
        } else if (selectable == SELECTABLE_ONE && this.getSelectedItemCount() > 1) {
            this.unselectAll();
        }
    }

    public void setBackgroundImage(Image backgroundImage, int backgroundImageOffsetX, int backgroundImageOffsetY) {
        this.backgroundImage = backgroundImage;
        this.backgroundImageOffsetX = backgroundImageOffsetX;
        this.backgroundImageOffsetY = backgroundImageOffsetY;
        this.repaint();
    }

    public void setBackground(Color backgroundColor) {
        super.setBackground(backgroundColor);
        this.backgroundColor = backgroundColor;
        this.repaint();
    }

    public void setForeground(Color foregroundColor) {
        super.setForeground(foregroundColor);
        this.foregroundColor = foregroundColor;
        this.repaint();
    }

    public void setEmptyListText(String emptyListText) {
        this.emptyListText = emptyListText;
        this.repaint();
    }

    public int getItemCount() {
        return this.items.size();
    }

    public synchronized int getSelectedItemCount() {
        int itemsCount = this.items.size();
        int selectedCount = 0;

        for (int i = 0; i < itemsCount; ++i) {
            if (this.getItem(i).isSelected()) {
                ++selectedCount;
            }
        }

        return selectedCount;
    }

    public synchronized void addItem(MultiColumnListItem<T> item) {
        int itemColumnCount = item.getColumnCount();
        if (this.columnCount == 0) {
            this.columnCount = itemColumnCount;
        } else if (itemColumnCount != this.columnCount) {
            System.out.println("Error: MultiColorList.addItem(...): ccount = " + itemColumnCount + " != "
                    + this.columnCount + " = noc");
            return;
        }

        int idx = this.getPositionFor(item);
        this.items.add(idx, item);
        int scrollbarValue = this.scrollbar.getValue();
        int scrollbarOffset = idx < scrollbarValue ? 1 : 0;
        if (scrollbarOffset == 0 && scrollbarValue + this.scrollbar.getVisibleAmount() == this.scrollbar.getMaximum()) {
            scrollbarOffset = 1;
        }

        this.configureScrollbar(scrollbarOffset);
        this.repaint();
    }

    public synchronized MultiColumnListItem<T> getItem(int i) {
        return this.items.get(i);
    }

    public synchronized MultiColumnListItem<T> getItem(int column, String text) {
        int itemsCount = this.items.size();
        if (itemsCount == 0) {
            return null;
        } else {
            for (int i = 0; i < itemsCount; ++i) {
                MultiColumnListItem<T> item = this.getItem(i);
                if (text.equals(item.getString(column))) {
                    return item;
                }
            }

            return null;
        }
    }

    public synchronized MultiColumnListItem<T> getSelectedItem() {
        MultiColumnListItem<T>[] selectedItems = this.getSelectedItems();
        return selectedItems == null ? null : (selectedItems.length != 1 ? null : selectedItems[0]);
    }

    public synchronized MultiColumnListItem<T>[] getSelectedItems() {
        return this.getItems(true);
    }

    public synchronized MultiColumnListItem<T>[] getAllItems() {
        return this.getItems(false);
    }

    public synchronized void removeItem(int column, String text) {
        this.removeItem(this.getItem(column, text));
    }

    public synchronized void removeItem(MultiColumnListItem<T> item) {
        int idx = this.items.indexOf(item);
        if (idx >= 0) {
            this.items.remove(idx);
            int scrollbarOffset = idx < this.scrollbar.getValue() ? -1 : 0;
            this.configureScrollbar(scrollbarOffset);
            this.repaint();
        }
    }

    public synchronized void removeAllItems() {
        if (this.items.size() != 0) {
            this.items.clear();
            this.configureScrollbar(0);
            this.repaint();
        }
    }

    public synchronized void removeAllSelections() {
        int itemCount = this.items.size();

        for (int i = 0; i < itemCount; ++i) {
            this.getItem(i).setSelected(false);
        }

        this.repaint();
    }

    public void setSelected(boolean selected) {
        this.setSelectedPrivate(selected);
    }

    public void selectAll() {
        this.setSelectedPrivate(true);
    }

    public void unselectAll() {
        this.setSelectedPrivate(false);
    }

    public void changeAlphaCol(int i) {
        this.setSortColumnIndex(i);
    }

    public synchronized void setSortColumnIndex(int sortColumnIndex) {
        if (sortColumnIndex != this.sortColumnIndex) {
            this.sortColumnIndex = sortColumnIndex;
            this.reSort();
        }
    }

    public synchronized void reSort() {
        int itemsCount = this.items.size();
        if (itemsCount != 0) {
            MultiColumnListItem<T>[] itemsCopy = this.getAllItems();
            this.items.clear();

            for (int i = 0; i < itemsCount; ++i) {
                this.items.add(this.getPositionFor(itemsCopy[i]), itemsCopy[i]);
            }

            this.repaint();
        }
    }

    public int[] getLastClickedMouseXY() {
        return new int[] {this.lastMouseClickX, this.lastMouseClickY};
    }

    public void setTitle(String text, int column) {
        this.columnTitles[column] = text;
        this.repaint();
    }

    public void setSortOrder(SortOrder sortOrder, int column) {
        this.columnSortTypes[column] = sortOrder;
        if (column == this.sortColumnIndex) {
            this.reSort();
        }
    }

    private synchronized void configureScrollbar(int offset) {
        int maximum = this.items.size();
        if (maximum <= this.rows) {
            if (this.scrollbarVisible) {
                this.scrollbar.removeAdjustmentListener(this);
                this.remove(this.scrollbar);
                this.scrollbarVisible = false;
            }

        } else {
            int value;
            if (!this.scrollbarVisible) {
                this.add(this.scrollbar);
                this.scrollbar.addAdjustmentListener(this);
                this.scrollbarVisible = true;
                value = 0;
            } else {
                value = this.scrollbar.getValue();
                if (value > maximum) {
                    value = maximum;
                }
            }

            value += offset;
            this.scrollbar.setValues(value, this.rows, 0, maximum);
        }
    }

    private synchronized MultiColumnListItem<T>[] getItems(boolean selectedItemsOnly) {
        int count = selectedItemsOnly ? this.getSelectedItemCount() : this.getItemCount();
        if (count == 0) {
            return null;
        } else {
            MultiColumnListItem<T>[] items = new MultiColumnListItem[count];
            int itemsCount = this.items.size();
            int itemIndex = 0;

            for (int i = 0; i < itemsCount; ++i) {
                MultiColumnListItem<T> item = this.getItem(i);
                if (!selectedItemsOnly || item.isSelected()) {
                    items[itemIndex] = item;
                    ++itemIndex;
                }
            }

            return items;
        }
    }

    private synchronized int getPositionFor(MultiColumnListItem<T> item) {
        int itemsCount = this.items.size();
        if (itemsCount == 0) {
            return 0;
        } else if (this.sortColumnIndex < 0) {
            return itemsCount;
        } else {
            String text = item.getString(this.sortColumnIndex);
            text = text != null ? text.toLowerCase() : "";

            for (int i = 0; i < itemsCount; ++i) {
                String otherText = this.getItem(i).getString(this.sortColumnIndex);
                otherText = otherText != null ? otherText.toLowerCase() : "";
                SortOrder sortOrder = this.columnSortTypes[this.sortColumnIndex];
                int compValue = sortOrder.getComparator().compare(text, otherText);
                if (compValue < 0) {
                    return i;
                }
            }

            return itemsCount;
        }
    }

    private int[] getColumnWidths(String[][] text) {
        if (this.fontMetrics == null) {
            this.fontMetrics = this.getFontMetrics(FontConstants.font);
        }

        int rows = text.length;
        int columns = text[0].length;
        int[] maxColumnWidths = new int[columns];

        for (int i = 0; i < columns; ++i) {
            maxColumnWidths[i] = this.fontMetrics.stringWidth(this.columnTitles[i]);
        }

        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                int textWidth = text[i][j] != null ? this.fontMetrics.stringWidth(text[i][j]) : 0;
                if (textWidth > maxColumnWidths[j]) {
                    maxColumnWidths[j] = textWidth;
                }
            }
        }

        int i = 0;

        for (int j = 0; j < columns; ++j) {
            i += maxColumnWidths[j];
        }

        double expandFactor = (double) this.usableWidth / (double) i;
        i = 0;
        int[] columnWidths = new int[columns];

        for (int column = 0; column < columns; ++column) {
            columnWidths[column] = i;
            i += (int) ((double) maxColumnWidths[column] * expandFactor);
        }

        return columnWidths;
    }

    private int getItemIndex(int yCoord) {
        int itemsCount = this.items.size();
        if (itemsCount == 0) {
            return -1;
        } else {
            int itemIndex = this.scrollbarVisible ? this.scrollbar.getValue() : 0;

            for (int i = 0; i < this.rows + 1 && itemIndex < itemsCount; ++itemIndex) {
                if (yCoord >= i * 16 && yCoord < (i + 1) * 16) {
                    return itemIndex;
                }

                ++i;
            }

            return -1;
        }
    }

    private synchronized void setSelectedPrivate(boolean selected) {
        int itemsCount = this.items.size();

        for (int i = 0; i < itemsCount; ++i) {
            this.getItem(i).setSelected(selected);
        }

        this.repaint();
    }

    private synchronized void itemPressed(MultiColumnListItem<T> item, int eventId, int newState) {
        if (this.listeners.size() != 0) {
            ItemEvent event = new ItemEvent(this, eventId, item, newState);
            for (ItemListener listener : this.listeners) {
                listener.itemStateChanged(event);
            }
        }
    }

    public void setListListener(MultiColumnListListener listListener) {
        this.listListener = listListener;
    }
}
