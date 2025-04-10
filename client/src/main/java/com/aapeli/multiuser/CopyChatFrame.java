package com.aapeli.multiuser;

import com.aapeli.colorgui.TextArea;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class CopyChatFrame extends Frame implements ComponentListener, WindowListener {

    private java.awt.TextArea textArea;

    public void componentHidden(ComponentEvent e) {}

    public void componentShown(ComponentEvent e) {}

    public void componentMoved(ComponentEvent e) {}

    public void componentResized(ComponentEvent e) {
        Dimension d = this.getSize();
        Insets i = this.getInsets();
        this.textArea.setBounds(i.left, i.top, d.width - i.left - i.right, d.height - i.top - i.bottom);
    }

    public void windowOpened(WindowEvent e) {}

    public void windowClosed(WindowEvent e) {}

    public void windowClosing(WindowEvent e) {
        this.dispose();
    }

    public void windowActivated(WindowEvent e) {}

    public void windowDeactivated(WindowEvent e) {}

    public void windowIconified(WindowEvent e) {}

    public void windowDeiconified(WindowEvent e) {}

    protected void create(Component parent, TextArea textArea) {
        this.setTitle("Sheriff: Copy of chat");
        this.setSize(420, 190);
        this.setResizable(true);
        Point var3 = parent.getLocationOnScreen();
        Dimension dimensions = parent.getSize();
        this.setLocation(var3.x + dimensions.width / 2 - 210, var3.y + dimensions.height / 2 - 95);
        this.setVisible(true);
        Insets insets = this.getInsets();
        this.setLayout(null);
        this.textArea = new java.awt.TextArea(this.getChatText(textArea));
        this.textArea.setBounds(insets.left, insets.top, 420, 190);
        this.textArea.setEditable(false);
        this.textArea.setBackground(Color.white);
        this.textArea.setForeground(Color.black);
        this.add(this.textArea);
        this.addWindowListener(this);
        this.addComponentListener(this);
        this.toFront();
        this.requestFocus();
    }

    private String getChatText(TextArea textArea) {
        String[] lines = textArea.getTextWithTimestamps();
        int linesCount = lines.length;
        StringBuffer sb = new StringBuffer(linesCount * 20);

        for (String line : lines) {
            int lineLength = line.length();

            for (int j = 0; j < lineLength; ++j) {
                char chr = line.charAt(j);
                if (chr == '<') {
                    chr = '{';
                } else if (chr == '>') {
                    chr = '}';
                }

                sb.append(chr);
            }

            sb.append('\n');
        }

        return sb.toString();
    }
}
