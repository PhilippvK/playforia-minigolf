package agolf.game;

import agolf.GameContainer;
import agolf.GolfGameFrame;
import com.aapeli.colorgui.Button;
import com.aapeli.colorgui.Choicer;
import java.awt.Checkbox;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class GameControlPanel extends Panel implements ActionListener, ItemListener {

    private GameContainer gameContainer;
    private PlayerInfoPanel playerInfoPanel;
    private int anInt342;
    private int anInt343;
    private int playerCount;
    private Button buttonSkip;
    private Button buttonNewGame;
    private Button buttonBack;
    private Choicer playerNamesDisplayModeChoicer;
    private Checkbox checkboxMaxFps;
    private boolean skipButtonVisible;

    protected GameControlPanel(GameContainer var1, PlayerInfoPanel var2, int var3, int var4) {
        this.gameContainer = var1;
        this.playerInfoPanel = var2;
        this.anInt342 = var3;
        this.anInt343 = var4;
        this.setSize(var3, var4);
        this.setLayout(null);
        this.skipButtonVisible = false;
    }

    public void addNotify() {
        super.addNotify();
        this.repaint();
    }

    public void paint(Graphics var1) {
        this.update(var1);
    }

    public void update(Graphics var1) {
        var1.setColor(GolfGameFrame.colourGameBackground);
        var1.fillRect(0, 0, this.anInt342, this.anInt343);
    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == this.buttonSkip) {
            if (this.gameContainer.gamePanel.skipButtonPressed(this.playerCount == 1)) {
                this.setVisible(false);
                this.remove(this.buttonSkip);
                this.setVisible(true);
                this.skipButtonVisible = false;
            }

        } else if (source == this.buttonNewGame) {
            this.buttonNewGame.removeActionListener(this);
            this.setVisible(false);
            this.remove(this.buttonNewGame);
            this.setVisible(true);
            this.playerInfoPanel.readyForNewGameLocal();
            this.gameContainer.gamePanel.requestNewGame();
        } else if (source == this.buttonBack) {
            this.buttonBack.removeActionListener(this);
            this.setVisible(false);
            this.remove(this.buttonBack);
            this.setVisible(true);
            this.gameContainer.gamePanel.leaveGame();
        }
    }

    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == this.playerNamesDisplayModeChoicer) {
            this.gameContainer.gamePanel.setPlayerNamesDisplayMode(
                    this.playerNamesDisplayModeChoicer.getSelectedIndex());
        }
    }

    protected void setPlayerCount(int var1) {
        this.playerCount = var1;
    }

    protected void displaySkipButton() {
        this.setVisible(false);
        this.remove(this.buttonSkip);
        this.skipButtonVisible = false;
        if (this.playerCount > 1 || this.gameContainer.synchronizedTrackTestMode.get()) {
            this.add(this.buttonSkip);
            this.skipButtonVisible = true;
        }

        this.setVisible(true);
    }

    protected void setState(int state) {
        this.setVisible(false);
        this.removeAll();
        this.skipButtonVisible = false;
        if (state == 2) {
            this.buttonNewGame = new Button(this.gameContainer.textManager.getText("GameControl_New"));
            this.buttonNewGame.setBounds(0, this.anInt343 - 55, this.anInt342, 25);
            this.buttonNewGame.setBackground(GolfGameFrame.colourButtonGreen);
            this.buttonNewGame.setForeground(GolfGameFrame.colourTextBlack);
            this.buttonNewGame.addActionListener(this);
            this.add(this.buttonNewGame);
        } else {
            this.buttonSkip = new Button(this.gameContainer.textManager.getText("GameControl_Skip"));
            this.buttonSkip.setBounds(0, this.anInt343 - 55, this.anInt342, 25);
            this.buttonSkip.setBackground(GolfGameFrame.colourButtonBlue);
            this.buttonSkip.setForeground(GolfGameFrame.colourTextBlack);
            this.buttonSkip.addActionListener(this);
            if (this.gameContainer.synchronizedTrackTestMode.get()) {
                this.checkboxMaxFps = new Checkbox("Max FPS", false);
                this.checkboxMaxFps.setBounds(0, this.anInt343 - 80, this.anInt342, 20);
                this.checkboxMaxFps.setBackground(GolfGameFrame.colourGameBackground);
                this.checkboxMaxFps.setForeground(GolfGameFrame.colourTextBlack);
                this.add(this.checkboxMaxFps);
            }

            if (this.playerCount > 1) {
                this.playerNamesDisplayModeChoicer = new Choicer();

                for (int option = 0; option < 4; ++option) {
                    this.playerNamesDisplayModeChoicer.addItem(
                            this.gameContainer.textManager.getText("GameControl_Names" + option));
                }

                this.playerNamesDisplayModeChoicer.select(this.playerCount <= 2 ? 0 : 3);
                this.playerNamesDisplayModeChoicer.setBounds(0, this.anInt343 - 80, this.anInt342, 20);
                this.playerNamesDisplayModeChoicer.addItemListener(this);
                this.add(this.playerNamesDisplayModeChoicer);
            }
        }

        this.buttonBack = new Button(this.gameContainer.textManager.getText("GameControl_Back"));
        this.buttonBack.setBounds(0, this.anInt343 - 25, this.anInt342, 25);
        this.buttonBack.setBackground(GolfGameFrame.colourButtonYellow);
        this.buttonBack.setForeground(GolfGameFrame.colourTextBlack);
        this.buttonBack.addActionListener(this);
        this.add(this.buttonBack);
        this.setVisible(true);
    }

    protected boolean maxFps() {
        return this.checkboxMaxFps.getState();
    }

    protected void method329() {
        this.buttonNewGame.removeActionListener(this);
        this.remove(this.buttonNewGame);
    }

    protected void refreshBackButton() {
        if (this.playerCount != 1) {
            this.setVisible(false);
            this.remove(this.buttonBack);
            this.add(this.buttonBack);
            this.setVisible(true);
        }
    }

    protected void hideSkipButton() {
        if (this.skipButtonVisible) {
            this.setVisible(false);
            this.remove(this.buttonSkip);
            this.setVisible(true);
            this.skipButtonVisible = false;
        }
    }

    protected void showSkipButton() {
        if (!this.skipButtonVisible) {
            this.setVisible(false);
            this.add(this.buttonSkip);
            this.setVisible(true);
            this.skipButtonVisible = true;
        }
    }
}
