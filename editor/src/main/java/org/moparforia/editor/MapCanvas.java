package org.moparforia.editor;

import org.moparforia.editor.util.RectangleDragSelector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class MapCanvas extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    SpriteManager spriteManager = null;
    Map m = null;
    Tile selectedTile = null;
    BufferedImage buffer = null;

    IEditor editor;

    RectangleDragSelector rectangleDragSelector;

    boolean renderGrid = true;

    private String name = "";
    private int category = 0;

    public MapCanvas(IEditor editor) {
        addMouseMotionListener(this);
        setSize(735, 375);
        addMouseListener(this);
        addMouseWheelListener(this);
        setDoubleBuffered(true);
        buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        this.editor = editor;
        rectangleDragSelector = new RectangleDragSelector();
    }

    public void updateProperties(String name, int category) {
        this.name = name;
        this.category = category;
    }

    public String getTrackName() {
        return name;
    }

    public int getTrackCategory() {
        return category;
    }

    /**
     * returns the following sprites in this format
     * a[0][] = shapes
     * a[1][] = elements
     * a[2][] = specials
     * a[3][] = balls
     *
     * @return 2d array of all sprites
     */
    public Image[][] getSpriteSet() {
        if (spriteManager != null) {
            return spriteManager.getSprites();
        } else {
            return null;
        }
    }

    public boolean setSpriteManager(SpriteManager sm) {
        if (sm != null) {
            this.spriteManager = sm;
            return true;
        }
        return false;
    }

    public boolean setMap(Map m) {
        if (m != null) {
            this.m = m;
            repaint();
            return true;
        } else {
            return false;
        }
    }

    public Map getMap() {
        return m;
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }

    public boolean setSelectedTile(Tile t) {
        if (t != null) {
            this.selectedTile = t;
            return true;
        } else {
            return false;
        }

    }

    public void toggleGrid() {
        renderGrid = !renderGrid;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        if (spriteManager != null && m != null) {
            for (int tileX = 0; tileX < 49; tileX++) {
                for (int tileY = 0; tileY < 25; tileY++) {
                    int[] tilePixels = spriteManager.getPixelsFromTileCode(m.getTile(tileX, tileY).getTileCode());
                    int y = 0;
                    for (int pixel = 0; pixel < tilePixels.length; pixel++) {
                        if (pixel % 15 == 0 && pixel != 0)
                            y++;
                        buffer.setRGB(tileX * 15 + (pixel % 15), tileY * 15 + y, tilePixels[pixel]);
                    }
                }
            }

            g2d.drawImage(buffer, null, 0, 0);
        }
        if (renderGrid) {
            g.setColor(Color.black);
            for (int x = 0; x < 49; x++) {
                for (int y = 0; y < 25; y++) {
                    g.drawRect(x * 15, y * 15, 15, 15);
                }
            }
        }
        if (editor.getDrawMode() == 1) {
            rectangleDragSelector.draw(g);
        }
    }

    private void drawTile(MouseEvent e) {
        int tileX = e.getX() / 15;
        int tileY = e.getY() / 15;

        Tile oldTile = m.getTile(tileX, tileY);
        Tile newTile = getSelectedTile();

        if (m != null && newTile != null) {
            if (!oldTile.equals(newTile)) { // only set tile if not the same
                m.setTile(tileX, tileY, newTile);
            }
        }
    }

    private void fillRect() {
        for (int x = rectangleDragSelector.getX1(); x < rectangleDragSelector.getX2(); x++) {
            for (int y = rectangleDragSelector.getY1(); y < rectangleDragSelector.getY2(); y++) {
                Tile oldTile = m.getTile(x, y);
                Tile newTile = getSelectedTile();

                if (!oldTile.equals(newTile)) { // only set tile if not the same
                    m.setTile(x, y, newTile);
                }
            }
        }
        rectangleDragSelector.reset();
    }

    public void mouseClicked(MouseEvent e) {
        switch(editor.getDrawMode()) {
            case 0:
                drawTile(e);
        }
        repaint();
    }

    public void mouseDragged(MouseEvent e) {
        switch(editor.getDrawMode()) {
            case 0:
                drawTile(e);
            case 1:
                rectangleDragSelector.refresh((int)Math.ceil(e.getX() / 15), (int)Math.ceil(e.getY() / 15));
        }

        repaint();
    }

    public void mousePressed(MouseEvent e) {
        switch(editor.getDrawMode()) {
            case 1:
            rectangleDragSelector.reset();
        }
    }

    public void mouseReleased(MouseEvent e) {
        switch(editor.getDrawMode()) {
            case 0:
                return;
            case 1:
                fillRect();
        }
        repaint();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseMoved(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        /*boolean scrollDown = e.getWheelRotation() == 1;
        if (!e.isControlDown() && !e.isShiftDown()) {
            if (scrollDown)
                ((TrackEditor) spriteManager.frame).editPanel.selectNextElementButton();// :(
            else
                ((TrackEditor) spriteManager.frame).editPanel.selectPrevElementButton();
        } else if (e.isControlDown() && !e.isShiftDown()) {
            if (scrollDown)
                ((TrackEditor) spriteManager.frame).editPanel.selectNextElement2Button();// :(
            else
                ((TrackEditor) spriteManager.frame).editPanel.selectPrevElement2Button();
        } else if (!e.isControlDown() && e.isShiftDown()) {
            //specials?
        }*/
    }
}
