package com.aapeli.multiuser;

import com.aapeli.colorgui.Button;
import java.awt.Color;
import java.awt.Graphics;

public class RoundedUpperCornersButton extends Button {

    public RoundedUpperCornersButton(String label) {
        super(label);
    }

    public void clearBackground(Graphics g, int var2, int var3) {
        g.fillRect(1, 1, var2 - 1, var3 - 1);
    }

    public void drawBorder(Graphics g, int width, int height) {
        boolean normalState = this.isNormalState();
        Color[] borderColors = this.getLightAndDarkBorderColors();
        g.setColor(normalState ? borderColors[0] : borderColors[1]);
        g.drawLine(0, 0, width - 2, 0);
        g.drawLine(0, 0, 0, height - 1);
        g.setColor(normalState ? borderColors[1] : borderColors[0]);
        g.drawLine(width - 1, height - 1, 1, height - 1);
        g.drawLine(width - 1, height - 1, width - 1, 0);
    }
}
