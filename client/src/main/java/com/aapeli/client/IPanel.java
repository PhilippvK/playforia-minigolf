package com.aapeli.client;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;

public abstract class IPanel extends Panel {

    private Image backgroundImage;
    private int backgroundImageXOffset;
    private int backgroundImageYOffset;
    private Object lock = new Object();

    public void paint(Graphics graphics) {
        this.update(graphics);
    }

    public void update(Graphics graphics) {
        this.drawBackground(graphics);
    }

    public void setBackground(Image image) {
        this.setBackground(image, 0, 0);
    }

    public void setBackground(Image image, int xOffset, int yOffset) {
        this.backgroundImage = image;
        this.backgroundImageXOffset = xOffset;
        this.backgroundImageYOffset = yOffset;
        this.recursiveRepaint();
    }

    public void setBackground(ImageManager imageManager, String imageKey) {
        this.setBackground(imageManager, imageKey, 0, 0);
    }

    public void setBackground(ImageManager imageManager, String imageKey, int xOffset, int yOffset) {
        Image image = imageManager.getGameImage(imageKey);
        if (image != null) {
            this.setBackground(image, xOffset, yOffset);
        } else {
            synchronized (this.lock) {
                this.setBackground(imageManager.getShared(imageKey), xOffset, yOffset);
            }
        }
    }

    public void setSharedBackground(ImageManager imageManager, String imageKey, int xOffset, int yOffset) {
        synchronized (this.lock) {
            this.setBackground(imageManager.getShared(imageKey), xOffset, yOffset);
        }
    }

    public void drawBackground(Graphics graphics) {
        if (!this.drawBackgroundImage(graphics)) {
            Component parent = this.getParent();
            if (parent == null) {
                parent = this;
            }

            Dimension size = this.getSize();
            graphics.setColor(parent.getBackground());
            graphics.fillRect(0, 0, size.width, size.height);
        }
    }

    public boolean drawBackgroundImage(Graphics graphics) {
        Object[] backgroundData = this.getBackgroundAndLocation(0, 0);
        if (backgroundData == null) {
            return false;
        } else {
            Image backgroundImage = (Image) backgroundData[0];
            int backgroundImageXOffset = (Integer) backgroundData[1];
            int backgroundImageYOffset = (Integer) backgroundData[2];
            Dimension size = this.getSize();
            graphics.drawImage(
                    backgroundImage,
                    0,
                    0,
                    size.width,
                    size.height,
                    -backgroundImageXOffset,
                    -backgroundImageYOffset,
                    -backgroundImageXOffset + size.width,
                    -backgroundImageYOffset + size.height,
                    null);
            return true;
        }
    }

    public void recursiveRepaint() {
        this.repaint();
        Component[] components = this.getComponents();
        if (components != null) {
            int componentsCount = components.length;
            if (componentsCount != 0) {
                for (Component component : components) {
                    if (component instanceof IPanel) {
                        ((IPanel) component).recursiveRepaint();
                    } else {
                        component.repaint();
                    }
                }
            }
        }
    }

    public Object[] getBackgroundAndLocation(int offsetX, int offsetY) {
        if (this.backgroundImage != null) {
            return new Object[] {
                this.backgroundImage, this.backgroundImageXOffset + offsetX, this.backgroundImageYOffset + offsetY
            };
        } else {
            Container parent = this.getParent();
            if (parent == null) {
                return null;
            } else if (!(parent instanceof IPanel iPanelParent)) {
                return null;
            } else {
                Point location = this.getLocation();
                offsetX -= location.x;
                offsetY -= location.y;
                return iPanelParent.getBackgroundAndLocation(offsetX, offsetY);
            }
        }
    }
}
