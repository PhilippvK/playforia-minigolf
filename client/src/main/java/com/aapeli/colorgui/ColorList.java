package com.aapeli.colorgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.ItemSelectable;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public final class ColorList extends Panel implements ComponentListener, AdjustmentListener, MouseListener, ItemSelectable {

    public static final int SELECTABLE_NONE = 0;
    public static final int SELECTABLE_ONE = 1;
    public static final int SELECTABLE_MULTI = 2;
    public static final int ID_CLICKED = 0;
    public static final int ID_RIGHTCLICKED = 1;
    public static final int ID_DOUBLECLICKED = 2;
    public static final int SORT_NONE = 0;
    public static final int SORT_TEXT_ABC = 1;
    public static final int SORT_TEXT_CBA = 2;
    public static final int SORT_VALUE_123 = 3;
    public static final int SORT_VALUE_321 = 4;
    protected static final Color backgroundColor = new Color(255, 255, 255);
    private static final Color borderMedium = new Color(192, 192, 192);
    private static final Color borderDark = new Color(64, 64, 64);
    private Scrollbar scrollbar;
    private boolean scrollbarVisible;
    private Image backgroundImage;
    private int backgroundImageoffsetX;
    private int backgroundImageOffsetY;
    private Font font;
    private Font fontBold;
    private int iconWidth;
    private int width;
    private int height;
    private int rowHeight;
    private int rows;
    private int selectable;
    private int sorting;
    private String title;
    private Color titleColor;
    private List<ColorListItem> items;
    private List<ColorListNode> nodes;
    private int lastMouseClickX;
    private int lastMouseClickY;
    private int rangeSelectionLastIndex;
    private int rangeSelectionState;
    private int groupsDrawn;
    private Image image;
    private Graphics imageGraphics;
    private int imageWidth;
    private int imageHeight;
    private List<ItemListener> listeners;


    public ColorList(int width, int height) {
        this(width, height, FontConstants.font, 0, 0);
    }

    public ColorList(int width, int height, Font font) {
        this(width, height, font, 0, 0);
    }

    public ColorList(int width, int height, int iconWidth, int rowHeight) {
        this(width, height, FontConstants.font, iconWidth, rowHeight);
    }

    public ColorList(int width, int height, Font font, int iconWidth, int rowHeight) {
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.font = font;
        int fontSize = font.getSize();
        this.fontBold = new Font(font.getName(), Font.BOLD, fontSize);
        this.iconWidth = iconWidth;
        this.items = new ArrayList<>();
        this.selectable = 0;
        this.sorting = 0;
        this.rowHeight = (Math.max(fontSize, rowHeight)) + 4;
        this.rows = height / this.rowHeight;
        this.lastMouseClickX = -1;
        this.lastMouseClickY = -1;
        this.imageWidth = -1;
        this.imageHeight = -1;
        this.rangeSelectionLastIndex = -1;
        this.groupsDrawn = 0;
        this.setLayout(null);
        this.scrollbar = new Scrollbar(1);
        this.scrollbar.setBounds(width - 16 - 1, 1, 16, height - 2);
        this.scrollbar.setBlockIncrement(this.rows - 1);
        this.scrollbar.setUnitIncrement(1);
        this.scrollbarVisible = false;
        this.addComponentListener(this);
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

    public void update(Graphics g) {
        if (this.image == null || this.width != this.imageWidth || this.height != this.imageHeight) {
            this.image = this.createImage(this.width, this.height);
            this.imageGraphics = this.image.getGraphics();
            this.imageWidth = this.width;
            this.imageHeight = this.height;
        }

        if (this.backgroundImage == null) {
            this.imageGraphics.setColor(backgroundColor);
            this.imageGraphics.fillRect(0, 0, this.width, this.height);
        } else {
            this.imageGraphics.drawImage(this.backgroundImage, 0, 0, this.width, this.height, this.backgroundImageoffsetX, this.backgroundImageOffsetY, this.backgroundImageoffsetX + this.width, this.backgroundImageOffsetY + this.height, this);
        }

        ColorListItemGroup currentGroup = null;
        synchronized (this) {
            this.nodes = new ArrayList<>();
            int y = 0;
            ColorListNode node;
            if (this.title != null) {
                node = new ColorListNode(1, y, this.width - 2, this.rowHeight, this.iconWidth, this.backgroundImage != null, this.fontBold, this.titleColor, this.title, null);
                this.nodes.add(node);
                node.draw(this.imageGraphics, this);
                y += this.rowHeight;
            }

            int itemsCount = this.items.size();
            int groupsDrawn = 0;
            if (itemsCount > 0) {
                int rowsToDraw = this.title != null ? this.rows - 1 : this.rows;
                int rowsRemaining = rowsToDraw;
                int itemIndex = this.scrollbarVisible ? this.scrollbar.getValue() : 0;
                boolean hasMultipleGroups = this.hasMultipleGroups();

                for (int i = 0; i < rowsRemaining + 1 && itemIndex < itemsCount; ++i) {
                    ColorListItem item = this.getItem(itemIndex);
                    if (hasMultipleGroups) {
                        ColorListItemGroup group = item.getGroup();
                        if (group != currentGroup) {
                            node = new ColorListNode(1, y, this.width - 2, this.rowHeight, this.iconWidth, this.backgroundImage != null, this.fontBold, Color.darkGray, group.getText(), group.getIcon());
                            this.nodes.add(node);
                            node.draw(this.imageGraphics, this);
                            currentGroup = group;
                            y += this.rowHeight;
                            --rowsRemaining;
                            ++groupsDrawn;
                        }
                    }

                    node = new ColorListNode(1, y, this.width - 2, this.rowHeight, this.iconWidth, this.backgroundImage != null, this.font, this.fontBold, item);
                    this.nodes.add(node);
                    node.draw(this.imageGraphics, this);
                    y += this.rowHeight;
                    ++itemIndex;
                }

                if (groupsDrawn != this.groupsDrawn) {
                    this.groupsDrawn = groupsDrawn;
                    if (this.scrollbar != null) {
                        this.scrollbar.setValues(this.scrollbar.getValue(), rowsToDraw - groupsDrawn, 0, itemsCount);
                    }
                }
            }
        }

        this.imageGraphics.setColor(borderMedium);
        this.imageGraphics.drawRect(0, 0, this.width - 1, this.height - 1);
        this.imageGraphics.setColor(borderDark);
        this.imageGraphics.drawLine(0, 0, this.width - 1, 0);
        this.imageGraphics.drawLine(0, 0, 0, this.height - 1);
        g.drawImage(this.image, 0, 0, this);
    }

    private boolean hasMultipleGroups() {
        ColorListItemGroup group = null;

        for (ColorListItem colorListItem: this.items) {
            ColorListItemGroup group2 = colorListItem.getGroup();
            if (group2 != null) {
                if (group != null && group2 != group) {
                    return true;
                }

                group = group2;
            }
        }

        return false;
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
        Dimension size = this.getSize();
        this.width = size.width;
        this.height = size.height;
        this.rows = this.height / this.rowHeight;
        this.scrollbar.setBounds(this.width - 16 - 1, 1, 16, this.height - 2);
        this.scrollbar.setBlockIncrement(this.rows - 1);
        this.configureScrollbar(false, 0);
        this.repaint();
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        this.repaint();
    }

    public synchronized void mousePressed(MouseEvent e) {
        this.lastMouseClickX = e.getX();
        this.lastMouseClickY = e.getY();
        ColorListItem item = this.getItemAt(this.lastMouseClickY);
        if (item != null) {
            boolean isRightClick = e.getButton() == MouseEvent.BUTTON3;
            boolean isDoubleClick = e.getClickCount() == 2;
            int eventId = isRightClick ? ID_RIGHTCLICKED : (isDoubleClick ? ID_DOUBLECLICKED : ID_CLICKED);
            short newState = 701;
            if (!item.isSelected()) {
                if (this.selectable == SELECTABLE_NONE) {
                    return;
                }

                if (this.selectable == SELECTABLE_ONE) {
                    this.removeAllSelections();
                }

                item.setSelected(true);
                newState = ItemEvent.SELECTED;
            } else if (!isRightClick) {
                item.setSelected(false);
                newState = ItemEvent.DESELECTED;
            }

            if (this.selectable == SELECTABLE_MULTI) {
                int i = this.items.indexOf(item);
                if (eventId == ID_CLICKED && (newState == ItemEvent.SELECTED || newState == ItemEvent.DESELECTED)) {
                    if (this.rangeSelectionLastIndex >= 0 && e.isShiftDown()) {
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
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
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
            this.removeAllSelections();
        } else if (selectable == SELECTABLE_ONE && this.getSelectedItemCount() > 1) {
            this.removeAllSelections();
        }

    }

    public void setSorted(boolean sorted) {
        this.setSorting(sorted ? SORT_TEXT_ABC : SORT_NONE);
    }

    public void setSorting(int sorting) {
        if (sorting != this.sorting) {
            this.sorting = sorting;
            if (sorting != SORT_NONE) {
                this.reSort();
            }
        }
    }

    public synchronized void reSort() {
        int itemsCount = this.items.size();
        if (itemsCount != 0) {
            ColorListItem[] newItems = new ColorListItem[itemsCount];

            for (int i = 0; i < itemsCount; ++i) {
                newItems[i] = this.getItem(i);
            }

            this.items.clear();

            for (int i = 0; i < itemsCount; ++i) {
                this.items.add(this.getPositionFor(newItems[i]), newItems[i]);
            }

            this.repaint();
        }
    }

    public int getSorting() {
        return this.sorting;
    }

    public void setBackgroundImage(Image backgroundImage, int backgroundImageOffsetX, int backgroundImageOffsetY) {
        this.backgroundImage = backgroundImage;
        this.backgroundImageoffsetX = backgroundImageOffsetX;
        this.backgroundImageOffsetY = backgroundImageOffsetY;
        this.repaint();
    }

    public void setTitle(String title, Color titleColor) {
        this.title = title;
        this.titleColor = titleColor;
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

    public void addItem(String text) {
        this.addItem(new ColorListItem(text));
    }

    public synchronized void addItem(ColorListItem item) {
        item.setColorListReference(this);
        int position = this.getPositionFor(item);
        this.items.add(position, item);
        int scrollbarValue = this.scrollbar.getValue();
        int scrollbarOffset = position < scrollbarValue ? 1 : 0;
        if (scrollbarOffset == 0 && scrollbarValue > 0 && scrollbarValue + this.scrollbar.getVisibleAmount() == this.scrollbar.getMaximum()) {
            scrollbarOffset = 1;
        }

        this.configureScrollbar(this.sorting == 0, scrollbarOffset);
        this.repaint();
    }

    public synchronized ColorListItem getItem(int i) {
        return this.items.get(i);
    }

    public synchronized ColorListItem getItem(String text) {
        int itemsCount = this.items.size();
        if (itemsCount == 0) {
            return null;
        } else {
            for (int i = 0; i < itemsCount; ++i) {
                ColorListItem item = this.getItem(i);
                if (text.equals(item.getText())) {
                    return item;
                }
            }

            return null;
        }
    }

    public synchronized ColorListItem getSelectedItem() {
        ColorListItem[] selectedItems = this.getSelectedItems();
        return selectedItems == null ? null : (selectedItems.length != 1 ? null : selectedItems[0]);
    }

    public synchronized ColorListItem[] getSelectedItems() {
        return this.getItems(true);
    }

    public synchronized ColorListItem[] getAllItems() {
        return this.getItems(false);
    }

    public synchronized ColorListItem removeItem(String text) {
        ColorListItem item = this.getItem(text);
        return item == null ? null : this.removeItem(item);
    }

    public synchronized ColorListItem removeItem(ColorListItem item) {
        int idx = this.items.indexOf(item);
        if (idx >= 0) {
            this.items.remove(idx);
            int scrollbarOffset = idx < this.scrollbar.getValue() ? -1 : 0;
            this.configureScrollbar(false, scrollbarOffset);
            this.repaint();
        }

        return item;
    }

    public synchronized void removeAllItems() {
        if (this.items.size() != 0) {
            this.items.clear();
            this.configureScrollbar(false, 0);
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

    public int[] getLastClickedMouseXY() {
        return new int[]{this.lastMouseClickX, this.lastMouseClickY};
    }

    private synchronized void configureScrollbar(boolean scrollToBottom, int offset) {
        int maximum = this.items.size();
        int visible = this.rows;
        if (this.title != null) {
            --visible;
        }

        visible -= this.groupsDrawn;
        if (maximum <= visible) {
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
                if (value > maximum || scrollToBottom) {
                    value = maximum;
                }
            }

            value += offset;
            this.scrollbar.setValues(value, visible, 0, maximum);
        }
    }

    private ColorListItem getItemAt(int y) {
        int nodesCount = this.nodes.size();
        if (nodesCount == 0) {
            return null;
        } else {
            for (int i = 0; i < nodesCount; ++i) {
                ColorListNode node = this.nodes.get(i);
                if (node.containsYCoordinate(y)) {
                    return node.getItem();
                }
            }

            return null;
        }
    }

    private synchronized ColorListItem[] getItems(boolean selectedItemsOnly) {
        int count = selectedItemsOnly ? this.getSelectedItemCount() : this.getItemCount();
        if (count == 0) {
            return null;
        } else {
            ColorListItem[] items = new ColorListItem[count];
            int itemsCount = this.items.size();
            int itemIndex = 0;

            for (int i = 0; i < itemsCount; ++i) {
                ColorListItem item = this.getItem(i);
                if (!selectedItemsOnly || item.isSelected()) {
                    items[itemIndex] = item;
                    ++itemIndex;
                }
            }

            return items;
        }
    }

    private synchronized int getPositionFor(ColorListItem item) {
        int itemsCount = this.items.size();
        if (itemsCount == 0) {
            return 0;
        } else {
            int groupSortValue = this.getGroupSortValue(item);
            int groupPosition = this.getGroupStartPosition(groupSortValue, itemsCount);
            return groupPosition == itemsCount ? itemsCount : this.getPositionFor(item, groupSortValue, groupPosition, itemsCount);
        }
    }

    private int getGroupSortValue(ColorListItem item) {
        ColorListItemGroup group = item.getGroup();
        return group != null ? group.getSortValue() : Integer.MAX_VALUE;
    }

    private int getGroupStartPosition(int groupSortValue, int itemsCount) {
        for (int i = 0; i < itemsCount; ++i) {
            int otherGroupSortValue = this.getGroupSortValue(this.items.get(i));
            if (groupSortValue <= otherGroupSortValue) {
                return i;
            }
        }

        return itemsCount;
    }

    private int getNextGroupPosition(int groupSortValue, int groupPosition, int itemsCount) {
        for (int i = groupPosition; i < itemsCount; ++i) {
            int sortValue = this.getGroupSortValue(this.items.get(i));
            if (sortValue > groupSortValue) {
                return i;
            }
        }

        return itemsCount;
    }

    private int getPositionFor(ColorListItem item, int groupSortValue, int groupPosition, int itemsCount) {
        int nextGroupPosition = this.getNextGroupPosition(groupSortValue, groupPosition, itemsCount);
        if (nextGroupPosition == groupPosition) {
            return groupPosition;
        } else {
            boolean sortOverride = item.isSortOverride();
            boolean otherItemSortOverride;
            if (this.sorting != SORT_TEXT_ABC && this.sorting != SORT_TEXT_CBA) {
                int value = item.getValue();

                for (int i = groupPosition; i < nextGroupPosition; ++i) {
                    ColorListItem otherItem = this.getItem(i);
                    otherItemSortOverride = otherItem.isSortOverride();
                    if (sortOverride && !otherItemSortOverride) {
                        return i;
                    }

                    if (sortOverride == otherItemSortOverride) {
                        int otherItemValue = otherItem.getValue();
                        if (this.sorting == SORT_VALUE_123) {
                            if (value < otherItemValue) {
                                return i;
                            }
                        } else if (value > otherItemValue) {
                            return i;
                        }
                    }
                }

                return nextGroupPosition;
            } else {
                String sortKey = this.getNormalizedSortKey(item.getText());

                for (int i = groupPosition; i < nextGroupPosition; ++i) {
                    ColorListItem otherItem = this.getItem(i);
                    otherItemSortOverride = otherItem.isSortOverride();
                    if (sortOverride && !otherItemSortOverride) {
                        return i;
                    }

                    if (sortOverride == otherItemSortOverride) {
                        String otherItemSortKey = this.getNormalizedSortKey(otherItem.getText());
                        if (this.sorting == SORT_TEXT_ABC) {
                            if (sortKey.compareTo(otherItemSortKey) < 0) {
                                return i;
                            }
                        } else if (sortKey.compareTo(otherItemSortKey) > 0) {
                            return i;
                        }
                    }
                }

                return nextGroupPosition;
            }
        }
    }

    private String getNormalizedSortKey(String itemText) {
        itemText = itemText.toLowerCase().trim();
        int length = itemText.length();
        StringBuffer sb = new StringBuffer(length);

        for (int i = 0; i < length; ++i) {
            char c = itemText.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == 'ä' || c == 'ö' || c == 'å') {
                sb.append(c);
            }

            if (c == '~') {
                sb.append('\u00ff');
            }
        }

        return sb.toString().trim();
    }

    private synchronized void itemPressed(ColorListItem item, int eventId, int newState) {
        if (this.listeners.size() != 0) {
            ItemEvent event = new ItemEvent(this, eventId, item, newState);
            for (ItemListener itemListener: this.listeners) {
                itemListener.itemStateChanged(event);
            }
        }
    }

}
