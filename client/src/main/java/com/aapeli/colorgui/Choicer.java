package com.aapeli.colorgui;

import com.aapeli.client.IPanel;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.ItemSelectable;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

public class Choicer extends IPanel implements ComponentListener, ItemListener, ItemSelectable {

    private Choice choice = new Choice();
    private boolean choiceMode = true;
    private List<ItemListener> listeners;
    private Object lock = new Object();

    public Choicer() {
        this.choice.setBackground(Color.white);
        this.choice.setForeground(Color.black);
        this.choice.addItemListener(this);
        this.listeners = new ArrayList<>();
        this.setLayout(null);
        this.choice.setLocation(0, 0);
        this.add(this.choice);
        this.addComponentListener(this);
    }

    public void componentHidden(ComponentEvent e) {}

    public void componentShown(ComponentEvent e) {}

    public void componentMoved(ComponentEvent e) {}

    public void componentResized(ComponentEvent e) {
        synchronized (this.lock) {
            Dimension size = this.getSize();
            this.choice.setSize(size);
        }
    }

    public void itemStateChanged(ItemEvent e) {
        synchronized (this.listeners) {
            if (!this.listeners.isEmpty()) {
                e = new ItemEvent(this, e.getID(), e.getItem(), e.getStateChange());
                for (ItemListener listener : this.listeners) {
                    listener.itemStateChanged(e);
                }
            }
        }
    }

    public Object[] getSelectedObjects() {
        synchronized (this.lock) {
            return this.choice.getSelectedObjects();
        }
    }

    public void setBackground(Color color) {
        synchronized (this.lock) {
            super.setBackground(color);
            this.choice.setBackground(color);
        }
    }

    public void setForeground(Color color) {
        synchronized (this.lock) {
            super.setForeground(color);

            this.choice.setForeground(color);
        }
    }

    public void addItem(String text) {
        synchronized (this.lock) {
            this.choice.addItem(text);
        }
    }

    public void removeItem(int i) {
        synchronized (this.lock) {
            this.choice.remove(i);
        }
    }

    public int getItemCount() {
        synchronized (this.lock) {
            return this.choice.getItemCount();
        }
    }

    public int getSelectedIndex() {
        synchronized (this.lock) {
            return this.choice.getSelectedIndex();
        }
    }

    public void select(int i) {
        this.setSelectedIndex(i);
    }

    public void setSelectedIndex(int i) {
        synchronized (this.lock) {
            this.choice.select(i);
        }
    }

    public void addItemListener(ItemListener listener) {
        synchronized (this.listeners) {
            this.listeners.add(listener);
        }
    }

    public void removeItemListener(ItemListener var1) {
        synchronized (this.listeners) {
            this.listeners.remove(var1);
        }
    }
}
