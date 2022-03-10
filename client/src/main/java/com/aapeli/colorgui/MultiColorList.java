package com.aapeli.colorgui;

import com.aapeli.client.StringDraw;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.ItemSelectable;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ImageObserver;
import java.util.Enumeration;
import java.util.Vector;

public class MultiColorList extends Panel implements AdjustmentListener, MouseListener, ItemSelectable {
//todo refactor
    public static final int SELECTABLE_NONE = 0;
    public static final int SELECTABLE_ONE = 1;
    public static final int SELECTABLE_MULTI = 2;
    public static final int ID_CLICKED = 0;
    public static final int ID_RIGHTCLICKED = 1;
    public static final int ID_DOUBLECLICKED = 2;
    public static final int ORDER_ABC = 0;
    public static final int ORDER_CBA = 1;
    public static final int ORDER_123_FIRST = 2;
    public static final int ORDER_321_FIRST = 3;
    public static final int ORDER_123_ALL = 4;
    public static final int ORDER_321_ALL = 5;
    private static final Font aFont681 = new Font("Dialog", 1, 12);
    private static final Color aColor682 = new Color(255, 255, 255);
    private static final Color aColor683 = new Color(192, 192, 192);
    private static final Color aColor684 = new Color(64, 64, 64);
    private static final Color aColor685 = new Color(192, 192, 192);

    private Scrollbar aScrollbar686;
    private boolean aBoolean687;
    private Image anImage688;
    private int anInt689;
    private int anInt690;
    private Color aColor691;
    private Color aColor692;
    private FontMetrics aFontMetrics693;
    private String aString694;
    private char aChar695;
    private String[] aStringArray696;
    private int[] anIntArray697;
    private int anInt698;
    private int anInt699;
    private int anInt700;
    private int anInt701;
    private int anInt702;
    private int anInt703;
    private int[] anIntArray704;
    private int anInt705;
    private Vector aVector706;
    private int anInt707;
    private int anInt708;
    private int anInt709;
    private int anInt710;
    private Image anImage711;
    private Graphics aGraphics712;
    private Vector aVector713;
    private MultiColorListListener listListener;

    public MultiColorList(String[] var1, int[] var2, int var3, int var4, int var5) {
        this.aStringArray696 = var1;
        this.anIntArray697 = var2;
        this.anInt699 = var3;
        this.anInt700 = var4;
        this.anInt701 = var5;
        this.setSize(var4, var5);
        this.aString694 = null;
        this.aChar695 = 46;
        this.anInt698 = var1 != null ? var1.length : 0;
        this.aVector706 = new Vector();
        this.anInt705 = 0;
        this.anInt702 = var4 - 6 - 16;
        this.anInt703 = var5 / 16 - 1;
        this.aColor692 = aColor685;
        this.anInt707 = this.anInt708 = -1;
        this.anInt709 = -1;
        this.aColor691 = aColor682;
        this.setLayout((LayoutManager) null);
        this.aScrollbar686 = new Scrollbar(1);
        this.aScrollbar686.setBounds(var4 - 16 - 1, 1, 16, var5 - 2);
        this.aScrollbar686.setBlockIncrement(this.anInt703 - 1);
        this.aScrollbar686.setUnitIncrement(1);
        this.aBoolean687 = false;
        this.addMouseListener(this);
        this.aVector713 = new Vector();
    }

    public void addNotify() {
        super.addNotify();
        this.repaint();
    }

    public void paint(Graphics var1) {
        this.update(var1);
    }

    public synchronized void update(Graphics var1) {
        if (this.anImage711 == null) {
            this.anImage711 = this.createImage(this.anInt700, this.anInt701);
            this.aGraphics712 = this.anImage711.getGraphics();
        }

        if (this.anImage688 == null) {
            this.aGraphics712.setColor(this.aColor691);
            this.aGraphics712.fillRect(0, 0, this.anInt700, this.anInt701);
        } else {
            this.aGraphics712.drawImage(this.anImage688, 0, 0, this.anInt700, this.anInt701, this.anInt689, this.anInt690, this.anInt689 + this.anInt700, this.anInt690 + this.anInt701, this);
        }

        this.anIntArray704 = null;
        int var2 = this.aVector706.size();
        byte var3 = 14;
        byte var4 = var3;
        this.aGraphics712.setFont(aFont681);
        this.aGraphics712.setColor(this.aColor692);
        if (var2 == 0) {
            String[][] var5 = new String[1][this.anInt698];

            int var6;
            for (var6 = 0; var6 < this.anInt698; ++var6) {
                var5[0][var6] = this.aStringArray696[var6];
            }

            this.anIntArray704 = this.method959(var5);

            for (var6 = 0; var6 < this.anInt698; ++var6) {
                this.aGraphics712.drawString(this.aStringArray696[var6], 3 + this.anIntArray704[var6], var4);
            }

            if (this.aString694 != null) {
                this.aGraphics712.setFont(Class94.aFont1575);
                StringDraw.drawStringWithMaxWidth(this.aGraphics712, this.aString694, this.anInt700 / 2, this.anInt701 / 2, 0, this.anInt700 * 9 / 10);
            }
        } else {
            Color[] var16 = new Color[this.anInt703 + 1];
            boolean[] var17 = new boolean[this.anInt703 + 1];
            boolean[] var7 = new boolean[this.anInt703 + 1];
            String[][] var8 = new String[this.anInt703 + 1][this.anInt698];
            Image[][] var9 = new Image[this.anInt703 + 1][this.anInt698];
            int var10 = this.aBoolean687 ? this.aScrollbar686.getValue() : 0;
            int var11 = var10;

            int var13;
            for (var13 = 0; var13 < this.anInt703 + 1 && var11 < var2; ++var13) {
                MultiColorListItem var12 = this.getItem(var11);
                var16[var13] = var12.getColor();
                var17[var13] = var12.isBold();
                var8[var13] = var12.getStrings();
                var9[var13] = var12.getImages();
                var7[var13] = var12.isSelected();
                ++var11;
            }

            this.anIntArray704 = this.method959(var8);

            for (var13 = 0; var13 < this.anInt698; ++var13) {
                this.aGraphics712.drawString(this.aStringArray696[var13], 3 + this.anIntArray704[var13], var4);
            }

            int var15 = var4 + 16;
            var11 = var10;

            for (var13 = 0; var13 < this.anInt703 + 1 && var11 < var2; ++var13) {
                this.aGraphics712.setFont(var17[var13] ? aFont681 : Class94.aFont1575);
                if (var7[var13]) {
                    this.aGraphics712.setColor(var16[var13]);
                    this.aGraphics712.fillRect(1, var15 - 12 - 1, this.anInt700 - 2, 16);
                    this.aGraphics712.setColor(this.aColor691);
                } else {
                    this.aGraphics712.setColor(var16[var13]);
                }

                for (int var14 = 0; var14 < this.anInt698; ++var14) {
                    if (var9[var13][var14] != null) {
                        this.aGraphics712.drawImage(var9[var13][var14], 3 + this.anIntArray704[var14] + 1, var15 - var3 + (8 - var9[var13][var14].getHeight((ImageObserver) null) / 2) + 1, this);
                    } else if (var8[var13][var14] != null) {
                        this.aGraphics712.drawString(var8[var13][var14], 3 + this.anIntArray704[var14], var15);
                    }
                }

                var15 += 16;
                ++var11;
            }
        }

        this.aGraphics712.setColor(aColor683);
        this.aGraphics712.drawRect(0, 0, this.anInt700 - 1, this.anInt701 - 1);
        this.aGraphics712.setColor(aColor684);
        this.aGraphics712.drawLine(0, 0, this.anInt700 - 1, 0);
        this.aGraphics712.drawLine(0, 0, 0, this.anInt701 - 1);
        var1.drawImage(this.anImage711, 0, 0, this);
    }

    public synchronized void mousePressed(MouseEvent evt) {
        int var2 = this.aVector706.size();
        if (var2 != 0) {
            this.anInt707 = evt.getX();
            this.anInt708 = evt.getY();
            int var3 = this.anInt708 - 12 - 4;
            int var4;
            if (var3 < 0) {
                if (this.anIntArray704 != null) {
                    var4 = -1;
                    int var13 = evt.getX();

                    for (int var14 = 0; var14 < this.anInt698 - 1 && var4 == -1; ++var14) {
                        if (var13 >= this.anIntArray704[var14] && var13 < this.anIntArray704[var14 + 1]) {
                            var4 = var14;
                        }
                    }

                    if (var4 == -1 && var13 >= this.anIntArray704[this.anInt698 - 1]) {
                        var4 = this.anInt698 - 1;
                    }

                    if (var4 >= 0) {
                        this.setSortColumnIndex(var4);
                    }
                }

            } else {
                var4 = this.method960(var3);
                if (var4 != -1) {
                    MultiColorListItem var5 = this.getItem(var4);
                    boolean isMetaDown = evt.isMetaDown();
                    boolean isDoubleClick = evt.getClickCount() == 2;
                    int var8 = isMetaDown ? 1 : (isDoubleClick ? 2 : 0);
                    short var9 = 701;
                    if (!var5.isSelected()) {
                        if (this.anInt705 == 0) {
                            return;
                        }

                        if (this.anInt705 == 1) {
                            this.unselectAll();
                        }

                        var5.setSelected(true);
                        var9 = 1;
                    } else if (!isMetaDown) {
                        var5.setSelected(false);
                        var9 = 2;
                    }

                    if (this.anInt705 == 2) {
                        if (var8 == 0 && (var9 == 1 || var9 == 2)) {
                            if (this.anInt709 >= 0 && evt.isShiftDown()) {
                                int var10 = Math.min(this.anInt709, var4);
                                int var11 = Math.max(this.anInt709, var4);

                                for (int var12 = var10; var12 <= var11; ++var12) {
                                    this.getItem(var12).setSelected(this.anInt710 == 1);
                                }
                            }

                            this.anInt709 = var4;
                            this.anInt710 = var9;
                        } else {
                            this.anInt709 = -1;
                        }
                    }

                    if (isMetaDown) {
                        this.update(this.getGraphics());
                    }

                    this.method962(var5, var8, var9);
                    this.repaint();

                    if(isDoubleClick && listListener != null) {
                        listListener.mouseDoubleClicked(var5);
                    }
                }
            }
        }
    }

    public void mouseReleased(MouseEvent var1) {
    }

    public void mouseClicked(MouseEvent var1) {
    }

    public void mouseEntered(MouseEvent var1) {
    }

    public void mouseExited(MouseEvent var1) {
    }

    public void adjustmentValueChanged(AdjustmentEvent var1) {
        this.repaint();
    }

    public synchronized void addItemListener(ItemListener var1) {
        this.aVector713.addElement(var1);
    }

    public synchronized void removeItemListener(ItemListener var1) {
        this.aVector713.removeElement(var1);
    }

    public Object[] getSelectedObjects() {
        return this.getSelectedItems();
    }

    public void setSelectable(int var1) {
        this.anInt705 = var1;
        if (var1 == 0) {
            this.unselectAll();
        } else if (var1 == 1 && this.getSelectedItemCount() > 1) {
            this.unselectAll();
        }

    }

    public void setBackgroundImage(Image var1, int var2, int var3) {
        this.anImage688 = var1;
        this.anInt689 = var2;
        this.anInt690 = var3;
        this.repaint();
    }

    public void setBackground(Color var1) {
        super.setBackground(var1);
        this.aColor691 = var1;
        this.repaint();
    }

    public void setForeground(Color var1) {
        super.setForeground(var1);
        this.aColor692 = var1;
        this.repaint();
    }

    public void setEmptyListText(String var1) {
        this.aString694 = var1;
        this.repaint();
    }

    public void setDecimalSeparator(char var1) {
        this.aChar695 = var1;
    }

    public int getItemCount() {
        return this.aVector706.size();
    }

    public synchronized int getSelectedItemCount() {
        int var1 = this.aVector706.size();
        int var2 = 0;

        for (int var3 = 0; var3 < var1; ++var3) {
            if (this.getItem(var3).isSelected()) {
                ++var2;
            }
        }

        return var2;
    }

//    public void addItem(String[] var1) {
//        this.addItem(new MultiColorListItem(var1));
//    }

    public synchronized void addItem(MultiColorListItem var1) {
        int var2 = var1.getColumnCount();
        if (this.anInt698 == 0) {
            this.anInt698 = var2;
        } else if (var2 != this.anInt698) {
            System.out.println("Error: MultiColorList.addItem(...): ccount = " + var2 + " != " + this.anInt698 + " = noc");
            return;
        }

        int var3 = this.method957(var1);
        this.aVector706.insertElementAt(var1, var3);
        int var4 = this.aScrollbar686.getValue();
        int var5 = var3 < var4 ? 1 : 0;
        if (var5 == 0 && var4 + this.aScrollbar686.getVisibleAmount() == this.aScrollbar686.getMaximum()) {
            var5 = 1;
        }

        this.method955(var5);
        this.repaint();
    }

    public synchronized MultiColorListItem getItem(int var1) {
        return (MultiColorListItem) ((MultiColorListItem) this.aVector706.elementAt(var1));
    }

    public synchronized MultiColorListItem getItem(int var1, String var2) {
        int var3 = this.aVector706.size();
        if (var3 == 0) {
            return null;
        } else {
            for (int var5 = 0; var5 < var3; ++var5) {
                MultiColorListItem var4 = this.getItem(var5);
                if (var2.equals(var4.getString(var1))) {
                    return var4;
                }
            }

            return null;
        }
    }

    public synchronized MultiColorListItem getSelectedItem() {
        MultiColorListItem[] var1 = this.getSelectedItems();
        return var1 == null ? null : (var1.length != 1 ? null : var1[0]);
    }

    public synchronized MultiColorListItem[] getSelectedItems() {
        return this.method956(true);
    }

    public synchronized MultiColorListItem[] getAllItems() {
        return this.method956(false);
    }

    public synchronized void removeItem(int var1, String var2) {
        this.removeItem(this.getItem(var1, var2));
    }

    public synchronized void removeItem(MultiColorListItem var1) {
        int var2 = this.aVector706.indexOf(var1);
        if (var2 >= 0) {
            this.aVector706.removeElementAt(var2);
            int var3 = var2 < this.aScrollbar686.getValue() ? -1 : 0;
            this.method955(var3);
            this.repaint();
        }

    }

    public synchronized void removeAllItems() {
        if (this.aVector706.size() != 0) {
            this.aVector706.removeAllElements();
            this.method955(0);
            this.repaint();
        }
    }

    public synchronized void removeAllSelections() {
        int var1 = this.aVector706.size();

        for (int var2 = 0; var2 < var1; ++var2) {
            this.getItem(var2).setSelected(false);
        }

        this.repaint();
    }

    public void setSelected(boolean var1) {
        this.method961(var1);
    }

    public void selectAll() {
        this.method961(true);
    }

    public void unselectAll() {
        this.method961(false);
    }

    public void changeAlphaCol(int var1) {
        this.setSortColumnIndex(var1);
    }

    public synchronized void setSortColumnIndex(int var1) {
        if (var1 != this.anInt699) {
            this.anInt699 = var1;
            this.reSort();
        }
    }

    public synchronized void reSort() {
        int var1 = this.aVector706.size();
        if (var1 != 0) {
            MultiColorListItem[] var2 = this.getAllItems();
            this.aVector706.removeAllElements();

            for (int var3 = 0; var3 < var1; ++var3) {
                this.aVector706.insertElementAt(var2[var3], this.method957(var2[var3]));
            }

            this.repaint();
        }
    }

    public int[] getLastClickedMouseXY() {
        int[] var1 = new int[]{this.anInt707, this.anInt708};
        return var1;
    }

    public void setTitle(String var1, int var2) {
        this.aStringArray696[var2] = var1;
        this.repaint();
    }

    public void setSortOrder(int var1, int var2) {
        this.anIntArray697[var2] = var1;
        if (var2 == this.anInt699) {
            this.reSort();
        }

    }

    private synchronized void method955(int var1) {
        int var2 = this.aVector706.size();
        if (var2 <= this.anInt703) {
            if (this.aBoolean687) {
                this.aScrollbar686.removeAdjustmentListener(this);
                this.remove(this.aScrollbar686);
                this.aBoolean687 = false;
            }

        } else {
            int var3;
            if (!this.aBoolean687) {
                this.add(this.aScrollbar686);
                this.aScrollbar686.addAdjustmentListener(this);
                this.aBoolean687 = true;
                var3 = 0;
            } else {
                var3 = this.aScrollbar686.getValue();
                if (var3 > var2) {
                    var3 = var2;
                }
            }

            var3 += var1;
            this.aScrollbar686.setValues(var3, this.anInt703, 0, var2);
        }
    }

    private synchronized MultiColorListItem[] method956(boolean var1) {
        int var2 = var1 ? this.getSelectedItemCount() : this.getItemCount();
        if (var2 == 0) {
            return null;
        } else {
            MultiColorListItem[] var3 = new MultiColorListItem[var2];
            int var4 = this.aVector706.size();
            int var5 = 0;

            for (int var7 = 0; var7 < var4; ++var7) {
                MultiColorListItem var6 = this.getItem(var7);
                if (!var1 || var6.isSelected()) {
                    var3[var5] = var6;
                    ++var5;
                }
            }

            return var3;
        }
    }

    private synchronized int method957(MultiColorListItem var1) {
        int var2 = this.aVector706.size();
        if (var2 == 0) {
            return 0;
        } else if (this.anInt699 < 0) {
            return var2;
        } else {
            String var3 = var1.getString(this.anInt699);
            var3 = var3 != null ? var3.toLowerCase() : "";

            for (int var10 = 0; var10 < var2; ++var10) {
                String var4 = this.getItem(var10).getString(this.anInt699);
                var4 = var4 != null ? var4.toLowerCase() : "";
                if (this.anIntArray697[this.anInt699] != 0 && this.anIntArray697[this.anInt699] != 1) {
                    double var6 = this.method958(var3, this.anIntArray697[this.anInt699]);
                    double var8 = this.method958(var4, this.anIntArray697[this.anInt699]);
                    if (this.anIntArray697[this.anInt699] != 2 && this.anIntArray697[this.anInt699] != 4) {
                        if (var6 > var8) {
                            return var10;
                        }
                    } else if (var6 < var8) {
                        return var10;
                    }
                } else {
                    int var5 = var3.compareTo(var4);
                    if (this.anIntArray697[this.anInt699] == 0) {
                        if (var5 < 0) {
                            return var10;
                        }
                    } else if (var5 > 0) {
                        return var10;
                    }
                }
            }

            return var2;
        }
    }

    private double method958(String var1, int var2) {
        int var3 = var1.length();
        if (var3 == 0) {
            return Double.MAX_VALUE;
        } else {
            StringBuffer var4 = new StringBuffer(var3);
            boolean var5 = false;

            for (int var7 = 0; var7 < var3; ++var7) {
                char var6 = var1.charAt(var7);
                if (var6 == 45 && var4.length() == 0) {
                    var4.append(var6);
                } else if (var6 == this.aChar695 && var5) {
                    var4.append('.');
                    var5 = true;
                } else if (var6 >= 48 && var6 <= 57) {
                    var4.append(var6);
                    if (!var5) {
                        var5 = true;
                    }
                } else if (var2 == 2 || var2 == 3) {
                    var7 = var3;
                }
            }

            var1 = var4.toString();
            if (var1.length() == 0) {
                return -1.7976931348623157E308D;
            } else if (var1.equals("-")) {
                return -1.7976931348623157E308D;
            } else {
                return (new Double(var1)).doubleValue();
            }
        }
    }

    private int[] method959(String[][] var1) {
        if (this.aFontMetrics693 == null) {
            this.aFontMetrics693 = this.getFontMetrics(Class94.aFont1575);
        }

        int var2 = var1.length;
        int var3 = var1[0].length;
        int[] var5 = new int[var3];

        int var6;
        for (var6 = 0; var6 < var3; ++var6) {
            var5[var6] = this.aFontMetrics693.stringWidth(this.aStringArray696[var6]);
        }

        int var7;
        for (var6 = 0; var6 < var2; ++var6) {
            for (var7 = 0; var7 < var3; ++var7) {
                int var4 = var1[var6][var7] != null ? this.aFontMetrics693.stringWidth(var1[var6][var7]) : 0;
                if (var4 > var5[var7]) {
                    var5[var7] = var4;
                }
            }
        }

        var6 = 0;

        for (var7 = 0; var7 < var3; ++var7) {
            var6 += var5[var7];
        }

        double var11 = 1.0D * (double) this.anInt702 / (double) var6;
        var6 = 0;
        int[] var9 = new int[var3];

        for (int var10 = 0; var10 < var3; ++var10) {
            var9[var10] = var6;
            var6 += (int) ((double) var5[var10] * var11);
        }

        return var9;
    }

    private int method960(int var1) {
        int var2 = this.aVector706.size();
        if (var2 == 0) {
            return -1;
        } else {
            int var3 = this.aBoolean687 ? this.aScrollbar686.getValue() : 0;

            for (int var4 = 0; var4 < this.anInt703 + 1 && var3 < var2; ++var3) {
                if (var1 >= var4 * 16 && var1 < (var4 + 1) * 16) {
                    return var3;
                }

                ++var4;
            }

            return -1;
        }
    }

    private synchronized void method961(boolean var1) {
        int var2 = this.aVector706.size();

        for (int var3 = 0; var3 < var2; ++var3) {
            this.getItem(var3).setSelected(var1);
        }

        this.repaint();
    }

    private synchronized void method962(MultiColorListItem var1, int var2, int var3) {
        if (this.aVector713.size() != 0) {
            ItemEvent var4 = new ItemEvent(this, var2, var1, var3);
            Enumeration var5 = this.aVector713.elements();

            while (var5.hasMoreElements()) {
                ((ItemListener) ((ItemListener) var5.nextElement())).itemStateChanged(var4);
            }

        }
    }

    public void setListListener(MultiColorListListener listListener) {
        this.listListener = listListener;
    }
}
