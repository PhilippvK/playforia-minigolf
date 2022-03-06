package agolf.lobby;

import agolf.GameApplet;
import agolf.GameContainer;
import agolf.LobbySelectPanel;
import com.aapeli.colorgui.ColorButton;

import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class LobbyControlPanel extends Panel implements ActionListener {

    private GameContainer gameContainer;
    private int height;
    private int width;
    private ColorButton buttonBack;
    private ColorButton buttonSingle;
    //private ColorButton buttonDual;
    private ColorButton buttonMulti;
    private ColorButton buttonQuit;


    protected LobbyControlPanel(GameContainer gameContainer, int width, int height) {
        this.gameContainer = gameContainer;
        this.height = width;
        this.width = height;
        this.setSize(width, height);
        this.create();
    }

    public void addNotify() {
        super.addNotify();
        this.repaint();
    }

    public void paint(Graphics var1) {
        this.update(var1);
    }

    public void update(Graphics var1) {
        var1.setColor(GameApplet.colourGameBackground);
        var1.fillRect(0, 0, this.height, this.width);
    }

    public void actionPerformed(ActionEvent evt) {
        Object evtSource = evt.getSource();
        if (evtSource == this.buttonBack) {
            this.gameContainer.gameApplet.setGameState(0);
            this.gameContainer.lobbyPanel.writeData("back");
        } else if (evtSource == this.buttonQuit) {
            this.gameContainer.lobbyPanel.quitLobby();
        } else {
            byte lobbyId = 0;
            if (evtSource == this.buttonSingle) {
                lobbyId = 1;
            /*
            } else if (evtSource == this.buttonDual) {
                lobbyId = 2;
            */
            } else if (evtSource == this.buttonMulti) {
                lobbyId = 3;
            }

            if (lobbyId > 0) {
                this.gameContainer.gameApplet.setGameState(0);
//                this.gameContainer.lobbyPanel.writeData(LobbySelectPanel.method442(lobbyId));
            }

        }
    }

    protected void setState(int state) {
        this.setVisible(false);
        if (!this.gameContainer.disableSinglePlayer) {
            this.remove(this.buttonSingle);
        }

//        this.remove(this.buttonDual);
        this.remove(this.buttonMulti);
        if (!this.gameContainer.disableSinglePlayer && state != 1) {
            this.add(this.buttonSingle);
        }

        /*
        if (state != 2) {
            this.add(this.buttonDual);
        }
        */

        if (state != 3) {
            this.add(this.buttonMulti);
        }

        this.setVisible(true);
    }

    private void create() {
        this.setLayout((LayoutManager) null);
        this.buttonBack = new ColorButton(this.gameContainer.textManager.getGame("LobbyControl_Main"));
        this.buttonBack.setBackground(GameApplet.colourButtonYellow);
        this.buttonBack.setBounds(0, 0, this.height, 20);
        this.buttonBack.addActionListener(this);
        this.add(this.buttonBack);
        if (!this.gameContainer.disableSinglePlayer) {
            this.buttonSingle = new ColorButton(this.gameContainer.textManager.getGame("LobbyControl_Single"));
            this.buttonSingle.setBounds(0, 27, this.height, 20);
            this.buttonSingle.addActionListener(this);
        }

        /*
        this.buttonDual = new ColorButton(this.gameContainer.textManager.getGame("LobbyControl_Dual"));
        this.buttonDual.setBounds(0, 52, this.height, 20);
        this.buttonDual.addActionListener(this);
        */

        this.buttonMulti = new ColorButton(this.gameContainer.textManager.getGame("LobbyControl_Multi"));
        this.buttonMulti.setBounds(0, 77, this.height, 20);
        this.buttonMulti.addActionListener(this);
        this.buttonQuit = new ColorButton(this.gameContainer.textManager.getGame("LobbyControl_Quit"));
        this.buttonQuit.setBackground(GameApplet.colourButtonRed);
        this.buttonQuit.setBounds(0, this.width - 20, this.height, 20);
        this.buttonQuit.addActionListener(this);
        this.add(this.buttonQuit);
    }
}
