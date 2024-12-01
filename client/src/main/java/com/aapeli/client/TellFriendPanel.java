package com.aapeli.client;

import com.aapeli.colorgui.ColorButton;
import com.aapeli.tools.DelayRepaint;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

class TellFriendPanel extends Panel implements ActionListener, FocusListener {

    protected static final Color aColor616 = new Color(240, 240, 240);
    private static final Color aColor617 = Color.black;
    private static final Color aColor618 = new Color(255, 224, 224);
    private static final Color aColor619 = new Color(144, 144, 240);
    private static final Color aColor620 = new Color(128, 255, 128);
    private static final Color aColor621 = new Color(255, 128, 128);
    private static final Color aColor622 = new Color(224, 224, 128);
    private static final Font aFont623 = new Font("Dialog", Font.PLAIN, 12);
    private static final Font aFont624 = new Font("Dialog", Font.PLAIN, 14);
    private static final Font aFont625 = new Font("Dialog", Font.PLAIN, 11);
    private static final Font aFont626 = new Font("Serif", Font.PLAIN, 18);
    private static final Font aFont627 = new Font("Dialog", Font.PLAIN, 16);
    private TextManager aTextManager628;
    private ImageManager anImageManager629;
    private TellFriendHandler tellFriendHandler;
    private TellFriendWindow tellFriendWindow;
    private InputTextField[] anInputTextFieldArray632;
    private ColorButton[] aColorButtonArray633;
    private ColorButton aColorButton634;
    private TextArea aTextArea635;
    private ColorButton aColorButton636;
    private ColorButton aColorButton637;
    private int anInt638;
    private int anInt639;
    private boolean aBoolean640;
    private Image anImage641;
    private Graphics aGraphics642;

    protected TellFriendPanel(TextManager var1, ImageManager var2, TellFriendHandler var3, TellFriendWindow var4) {
        this.aTextManager628 = var1;
        this.anImageManager629 = var2;
        this.tellFriendHandler = var3;
        this.tellFriendWindow = var4;
        this.setSize(500, 265);
        this.setBackground(aColor616);
        this.method480();
        this.anInt638 = 0;
        this.aBoolean640 = false;
        this.anInt639 = 1;
        this.method481(null);
    }

    public void addNotify() {
        super.addNotify();
        this.repaint();
    }

    public void paint(Graphics var1) {
        this.update(var1);
    }

    public void update(Graphics var1) {
        if (this.anImage641 == null) {
            this.anImage641 = this.createImage(500, 265);
            this.aGraphics642 = this.anImage641.getGraphics();
        }

        boolean var2 = false;
        Image var3 = this.anImageManager629.getShared("tf-background.gif");
        if (var3 != null) {
            this.aGraphics642.drawImage(var3, 0, 0, this);
        } else {
            this.aGraphics642.setColor(aColor616);
            this.aGraphics642.fillRect(0, 0, 500, 265);
            var2 = true;
        }

        this.aGraphics642.setColor(aColor617);
        if (this.anInt638 == 0) {
            this.aGraphics642.setFont(aFont623);
            StringDraw.drawStringWithMaxWidth(
                    this.aGraphics642, this.aTextManager628.getShared("TellFriend_Info"), 10, 25, -1, 480);
            this.aGraphics642.setFont(aFont624);
            StringDraw.drawStringWithMaxWidth(
                    this.aGraphics642, this.aTextManager628.getShared("TellFriend_EmailTitle"), 10, 90, -1, 480);
            this.aGraphics642.setFont(aFont625);
            StringDraw.drawStringWithMaxWidth(
                    this.aGraphics642, this.aTextManager628.getShared("TellFriend_MessageTitle"), 200, 90, -1, 480);
        } else if (this.anInt638 == 1) {
            this.aGraphics642.setFont(aFont626);
            StringDraw.drawStringWithMaxWidth(
                    this.aGraphics642, this.aTextManager628.getShared("TellFriend_EmailSent"), 290, 122, 0, 340);
            var3 = this.anImageManager629.getShared("tf-char-sent.gif");
            if (var3 != null) {
                this.aGraphics642.drawImage(var3, 40, 60, this);
            } else {
                var2 = true;
            }
        } else {
            this.aGraphics642.setFont(aFont627);
            StringDraw.drawStringWithMaxWidth(
                    this.aGraphics642, this.aTextManager628.getShared("TellFriend_EmailNotSent"), 310, 92, 0, 340);
            var3 = this.anImageManager629.getShared("tf-char-error.gif");
            if (var3 != null) {
                this.aGraphics642.drawImage(var3, 50, 120, this);
            } else {
                var2 = true;
            }
        }

        var1.drawImage(this.anImage641, 0, 0, this);
        if (!this.aBoolean640) {
            Image var4 = this.anImageManager629.getShared("tf-btn-send.gif");
            Image var5 = this.anImageManager629.getShared("tf-btn-close.gif");
            if (var4 != null && var5 != null) {
                this.aColorButton636.setIconImage(var4, 21, 19);
                this.aColorButton637.setIconImage(var5, 21, 19);
                this.aBoolean640 = true;
            } else {
                var2 = true;
            }
        }

        if (var2) {
            new DelayRepaint(this);
        }
    }

    public synchronized void actionPerformed(ActionEvent var1) {
        Object var2 = var1.getSource();
        if (var2 == this.aColorButton634) {
            if (this.anInt639 < 4) {
                this.method482(false);
            }

        } else {
            int var3;
            int var4;
            for (var3 = 0; var3 < 4; ++var3) {
                if (var2 == this.anInputTextFieldArray632[var3]) {
                    if (var3 < this.anInt639) {
                        for (var4 = 0; var4 < this.anInt639; ++var4) {
                            if (this.anInputTextFieldArray632[var4]
                                            .getInputText(false)
                                            .length()
                                    == 0) {
                                this.anInputTextFieldArray632[var4].requestFocus();
                                return;
                            }
                        }

                        if (this.anInt639 < 4) {
                            this.method482(true);
                        }
                    }

                    return;
                }
            }

            int var5;
            for (var3 = 0; var3 < 4; ++var3) {
                if (var2 == this.aColorButtonArray633[var3]) {
                    if (this.anInt639 > 1 && var3 < this.anInt639) {
                        InputTextField var7 = this.anInputTextFieldArray632[var3];

                        for (var5 = var3; var5 < 3; ++var5) {
                            this.anInputTextFieldArray632[var5] = this.anInputTextFieldArray632[var5 + 1];
                        }

                        this.anInputTextFieldArray632[3] = this.method483();
                        --this.anInt639;
                        this.method481(var7);
                    }

                    return;
                }
            }

            if (var2 == this.aColorButton636) {
                String[] var6 = new String[this.anInt639];
                var4 = 0;

                for (var5 = 0; var5 < this.anInt639; ++var5) {
                    var6[var5] = this.anInputTextFieldArray632[var5].getInputText(false);
                    if (this.method484(var6[var5])) {
                        ++var4;
                    } else {
                        var6[var5] = null;
                    }
                }

                String var8 = this.aTextArea635.getText().trim();
                if (var8.length() == 0) {
                    var8 = null;
                }

                if (var4 > 0) {
                    if (this.tellFriendHandler.method1702(var6, var8, 79, 108, 97)) {
                        this.anInt638 = 1;
                    } else {
                        this.anInt638 = -1;
                    }

                    this.setVisible(false);
                    this.removeAll();
                    this.aColorButton637.setBackground(aColor622);
                    this.add(this.aColorButton637);
                    this.setVisible(true);
                    this.repaint();
                }

            } else {
                if (var2 == this.aColorButton637) {
                    this.tellFriendWindow.close();
                }
            }
        }
    }

    public void focusGained(FocusEvent var1) {}

    public synchronized void focusLost(FocusEvent var1) {
        for (int var3 = 0; var3 < this.anInt639; ++var3) {
            String var2 = this.anInputTextFieldArray632[var3].getInputText(false);
            if (var2.length() > 0) {
                this.anInputTextFieldArray632[var3].setBackground(this.method484(var2) ? Color.white : aColor618);
            }
        }
    }

    private void method480() {
        this.setLayout(null);
        this.anInputTextFieldArray632 = new InputTextField[4];

        int var1;
        for (var1 = 0; var1 < 4; ++var1) {
            this.anInputTextFieldArray632[var1] = this.method483();
        }

        this.aColorButtonArray633 = new ColorButton[4];

        for (var1 = 0; var1 < 4; ++var1) {
            this.aColorButtonArray633[var1] = new ColorButton(this.aTextManager628.getShared("TellFriend_EmailRemove"));
            this.aColorButtonArray633[var1].setBackground(new Color(255, 64, 64));
            this.aColorButtonArray633[var1].setForeground(new Color(255, 224, 224));
            this.aColorButtonArray633[var1].setSize(17, 17);
            this.aColorButtonArray633[var1].addActionListener(this);
        }

        this.aColorButton634 = new ColorButton(this.aTextManager628.getShared("TellFriend_EmailAdd"));
        this.aColorButton634.setSize(110, 20);
        this.aColorButton634.setBackground(aColor619);
        this.aColorButton634.addActionListener(this);
        this.aTextArea635 = new TextArea("", 1, 1, 1);
        this.aTextArea635.setBounds(200, 100, 290, 115);
        this.aTextArea635.setEditable(true);
        this.aTextArea635.setBackground(Color.white);
        this.aTextArea635.setForeground(Color.black);
        this.add(this.aTextArea635);
        this.aColorButton636 = new ColorButton(this.aTextManager628.getShared("TellFriend_ButtonSend"));
        this.aColorButton636.setBounds(300, 230, 100, 25);
        this.aColorButton636.setBackground(aColor620);
        this.aColorButton636.addActionListener(this);
        this.add(this.aColorButton636);
        this.aColorButton637 = new ColorButton(this.aTextManager628.getShared("TellFriend_ButtonClose"));
        this.aColorButton637.setBounds(410, 230, 80, 25);
        this.aColorButton637.setBackground(aColor621);
        this.aColorButton637.addActionListener(this);
        this.add(this.aColorButton637);
    }

    private void method481(InputTextField var1) {
        this.setVisible(false);

        int var2;
        for (var2 = 0; var2 < 4; ++var2) {
            this.remove(this.anInputTextFieldArray632[var2]);
            this.remove(this.aColorButtonArray633[var2]);
        }

        this.remove(this.aColorButton634);
        if (var1 != null) {
            this.remove(var1);
        }

        for (var2 = 0; var2 < this.anInt639; ++var2) {
            this.anInputTextFieldArray632[var2].setLocation(10, 100 + var2 * 30);
            this.add(this.anInputTextFieldArray632[var2]);
        }

        if (this.anInt639 > 1) {
            for (var2 = 0; var2 < this.anInt639; ++var2) {
                this.aColorButtonArray633[var2].setLocation(165, 100 + var2 * 30);
                this.add(this.aColorButtonArray633[var2]);
            }
        }

        if (this.anInt639 < 4) {
            this.aColorButton634.setLocation(10, 100 + this.anInt639 * 30);
            this.add(this.aColorButton634);
        }

        this.setVisible(true);
    }

    private void method482(boolean var1) {
        ++this.anInt639;
        this.method481(null);
        if (var1) {
            this.anInputTextFieldArray632[this.anInt639 - 1].requestFocus();
        }
    }

    private InputTextField method483() {
        InputTextField var1 = new InputTextField(this.aTextManager628.getShared("TellFriend_EmailHelp"), 60);
        var1.setSize(150, 25);
        var1.addFocusListener(this);
        var1.addActionListener(this);
        return var1;
    }

    private boolean method484(String var1) {
        int var2 = var1.indexOf(64);
        if (var2 == -1) {
            return false;
        } else {
            String var3 = var1.substring(0, var2);
            if (var3.length() == 0) {
                return false;
            } else {
                String var4 = var1.substring(var2 + 1);
                if (var4.indexOf(64) >= 0) {
                    return false;
                } else {
                    var2 = var4.lastIndexOf(46);
                    if (var2 == -1) {
                        return false;
                    } else {
                        var4 = var4.substring(var2 + 1);
                        int var5 = var4.length();
                        return var5 >= 2 && var5 <= 6;
                    }
                }
            }
        }
    }
}
