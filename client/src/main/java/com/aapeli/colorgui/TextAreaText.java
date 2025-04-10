package com.aapeli.colorgui;

import java.awt.Color;

class TextAreaText {

    private long created;
    private Color color;
    private String text;
    private boolean bold;

    public TextAreaText(Color color, String text, boolean bold) {
        this.created = System.currentTimeMillis();
        this.color = color;
        this.text = text;
        this.bold = bold;
    }

    protected long getCreated() {
        return this.created;
    }

    protected Color getColor() {
        return this.color;
    }

    protected String getText() {
        return this.text;
    }

    protected boolean isBold() {
        return this.bold;
    }

    protected boolean isTextEmpty() {
        return this.text == null;
    }
}
