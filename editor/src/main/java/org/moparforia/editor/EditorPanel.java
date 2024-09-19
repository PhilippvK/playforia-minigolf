package org.moparforia.editor;

import org.moparforia.shared.tracks.Track;
import org.moparforia.shared.tracks.TrackCategory;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class EditorPanel extends JPanel {

    MapCanvas canvas;
    JPanel shapesPanel, elementsPanel, elements2Panel, specialsPanel, menuPanel;
    JButton[] shapeButtons, elementButtons, element2Buttons, specialButtons;
    Image[][] spriteSet;
    int currentElementSelection = 0, currentShapeSelection = 0, currentSpecialSelection = 0;
    int currentElement2Selection = 0;
    Border borderGrey;
    Border borderBlack;
    Track currentTrack;


    public EditorPanel(MapCanvas canvas) {
        this.canvas = canvas;
        setLayout(new GridLayout(3, 2));
        init();
    }

    private void init() {

        elementsPanel = new JPanel();
        elementsPanel.setLayout(new GridLayout(6, 4, 2, 2));
        elements2Panel = new JPanel();
        elements2Panel.setLayout(new GridLayout(6, 4, 2, 2));
        shapesPanel = new JPanel();
        shapesPanel.setLayout(new GridLayout(7, 4, 2, 2));
        specialsPanel = new JPanel();
        specialsPanel.setLayout(new GridLayout(7, 4, 2, 2));

        borderGrey = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2);
        borderBlack = BorderFactory.createLineBorder(Color.BLACK, 2);

        spriteSet = canvas.getSpriteSet();
        elementButtons = createButtons(spriteSet[1], elementSelection);
        element2Buttons = createButtons(spriteSet[1], element2Selection);
        shapeButtons = createButtons(spriteSet[0], shapeSelection);
        specialButtons = createButtons(spriteSet[2], specialSelection);

        for (JButton butt : elementButtons) {
            elementsPanel.add(butt);
        }
        for (JButton butt : element2Buttons) {
            elements2Panel.add(butt);
        }
        for (JButton butt : shapeButtons) {
            shapesPanel.add(butt);
        }
        for (JButton butt : specialButtons) {
            specialsPanel.add(butt);
        }

        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        JButton loadMap = new JButton("loadMap!??!");
        loadMap.addActionListener(changeMap);
        menuPanel.add(loadMap);
        JButton saveMap = new JButton("saveMap!??!");
        saveMap.addActionListener(generateMap);
        menuPanel.add(saveMap);

        this.add(elementsPanel);
        this.add(elements2Panel);
        this.add(shapesPanel);
        this.add(specialsPanel);
        this.add(menuPanel);
    }

    private JButton[] createButtons(Image[] set, ActionListener listener) {
        JButton[] result = new JButton[set.length];
        for (int i = 0; i < set.length; i++) {
            ImageIcon icon = new ImageIcon(set[i]);

            result[i] = new JButton(icon);
            result[i].setBorder(BorderFactory.createEmptyBorder());
            result[i].setContentAreaFilled(false);
            result[i].addActionListener(listener);
            result[i].setBorder(borderGrey);
        }
        return result;
    }

    ActionListener elementSelection = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            // this is horrid but oh well
            elementButtons[currentElementSelection].setBorder(borderGrey);
            int selection = 0;

            for (int i = 0; i < elementButtons.length; i++) {
                if (source.equals(elementButtons[i])) {
                    selection = i;
                    break;
                }
            }

            Tile currentTile = canvas.getSelectedTile();
            if (currentTile.isSpecial()) {
                currentShapeSelection = 0;
                shapeSelection.actionPerformed(new ActionEvent(elementButtons[0], 0, null));
            }
            currentTile.setSpecial(false); //  ensure we're not drawing a special!
            currentElementSelection = selection;
            elementButtons[currentElementSelection].setBorder(borderBlack);
            currentTile.setBackground(selection); // change background tile
            canvas.setSelectedTile(currentTile);
            System.out.println("new: " + currentTile);
        }
    };

    ActionListener element2Selection = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            // this is horrid but oh well
            element2Buttons[currentElement2Selection].setBorder(borderGrey);
            int selection = 0;

            for (int i = 0; i < element2Buttons.length; i++) {
                if (source.equals(element2Buttons[i])) {
                    selection = i;
                    break;
                }
            }

            Tile currentTile = canvas.getSelectedTile();
            if (currentTile.isSpecial()) {
                currentShapeSelection = 0;
                shapeSelection.actionPerformed(new ActionEvent(elementButtons[0], 0, null));
            }
            currentTile.setSpecial(false); //  ensure we're not drawing a special!
            currentElement2Selection = selection;
            element2Buttons[currentElement2Selection].setBorder(borderBlack);
            currentTile.setForeground(selection); // change foreground tile
            canvas.setSelectedTile(currentTile);
            System.out.println("new: " + currentTile);
        }
    };


    ActionListener shapeSelection = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            // this is horrid but oh well

            shapeButtons[currentShapeSelection].setBorder(borderGrey);

            for (int i = 0; i < shapeButtons.length; i++) {
                if (source.equals(shapeButtons[i])) {
                    shapeButtons[i].setBorder(borderBlack);
                    currentShapeSelection = i;
                }
            }

            Tile currentTile = canvas.getSelectedTile();
            currentTile.setSpecial(false); //  ensure we're not drawing a special!
            currentTile.setShapeIndex(currentShapeSelection);
            canvas.setSelectedTile(currentTile);
            System.out.println("new: " + currentTile);

        }
    };

    ActionListener specialSelection = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            // this is horrid but oh well

            specialButtons[currentSpecialSelection].setBorder(borderGrey);

            for (int i = 0; i < specialButtons.length; i++) {
                if (source.equals(specialButtons[i])) {
                    specialButtons[i].setBorder(borderBlack);
                    currentSpecialSelection = i;
                }
            }

            Tile currentTile = canvas.getSelectedTile();
            currentTile.setSpecial(true); //  ensure we ARE drawing a special tile
            currentTile.setShapeIndex(currentSpecialSelection);
            canvas.setSelectedTile(currentTile);
            System.out.println("new: " + currentTile);


        }
    };

    ActionListener changeMap = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            try {
                JFileChooser chooser = new JFileChooser(".");
                chooser.showDialog(getThis(),"hurr");
                File f = chooser.getSelectedFile();
                //currentTrack = TrackStore.getTrack(f);//todo get rid of this (uses org.moparforia.server.track.TrackStore)!!
                currentTrack = TrackEditor.loadTrack(f.toPath());
                Map m = new MapDecompressor().decompress(currentTrack.getMap());
                canvas.setMap(m);
            } catch (Exception exp) {
                exp.printStackTrace();
                System.err.println("PROBLEMO WITH LOADING MAP");
            }

        }
    };

    public EditorPanel getThis() {
        return this;
    }

    ActionListener generateMap = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if(currentTrack == null) {
                String data = new MapCompressor().compress(canvas.getMap());
                String name = JOptionPane.showInputDialog(getThis(),"WUTS THE NAME OF THIS TRACK?");
                Track track = new Track(name, "fc", data, TrackCategory.UNKNOWN);
                currentTrack = track;
            }

            MapCompressor mc = new MapCompressor();

            String data = mc.compress(canvas.getMap());
            currentTrack.setMap(data);
            String save = currentTrack.toString().replace("\t", "\n");
            File file = new File("tracks/custom/" + currentTrack.getName() + ".track");

            try (PrintStream out = new PrintStream(new FileOutputStream(file, false))) {
                out.print(save);
            } catch (Exception ignored) {
            }

        }
    };


    public JButton getCurrentElementButton(boolean fireAction) {
        JButton button = elementButtons[currentElementSelection];
        if (fireAction)
            button.getActionListeners()[0].actionPerformed(new ActionEvent(button, 0, null));
        return button;
    }

    public JButton selectNextElementButton() {
        elementButtons[currentElementSelection].setBorder(borderGrey);
        currentElementSelection++;
        if (currentElementSelection >= elementButtons.length)
            currentElementSelection = 0;
        return getCurrentElementButton(true);
    }

    public JButton selectPrevElementButton() {
        elementButtons[currentElementSelection].setBorder(borderGrey);
        currentElementSelection--;
        if (currentElementSelection <= 0)
            currentElementSelection = elementButtons.length - 1;
        return getCurrentElementButton(true);
    }

    public JButton getCurrentElement2Button(boolean fireAction) {
        JButton button = element2Buttons[currentElement2Selection];
        if (fireAction)
            button.getActionListeners()[0].actionPerformed(new ActionEvent(button, 0, null));
        return button;
    }

    public JButton selectNextElement2Button() {
        element2Buttons[currentElement2Selection].setBorder(borderGrey);
        currentElement2Selection++;
        if (currentElement2Selection >= element2Buttons.length)
            currentElement2Selection = 0;
        return getCurrentElement2Button(true);
    }

    public JButton selectPrevElement2Button() {
        element2Buttons[currentElement2Selection].setBorder(borderGrey);
        currentElement2Selection--;
        if (currentElement2Selection <= 0)
            currentElement2Selection = element2Buttons.length - 1;
        return getCurrentElement2Button(true);
    }

}
