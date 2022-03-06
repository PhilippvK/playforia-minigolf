package agolf.lobby;

import agolf.GameApplet;
import agolf.GameContainer;
import com.aapeli.colorgui.ColorButton;
import org.moparforia.shared.game.Lobby;

import java.awt.Graphics;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class LobbyGamePasswordPanel extends Panel implements KeyListener, ActionListener {

    private GameContainer gameContainer;
    private LobbyMultiPlayerPanel lobbyMultiPlayerPanel;
    private Lobby lobby;
    private TextField textField;
    private ColorButton buttonOk;
    private ColorButton buttonCancel;
    private boolean emptyField;


    protected LobbyGamePasswordPanel(GameContainer gameContainer, LobbyMultiPlayerPanel lobbyMultiPlayerPanel, Lobby lobby) {
        this.gameContainer = gameContainer;
        this.lobbyMultiPlayerPanel = lobbyMultiPlayerPanel;
        this.lobby = lobby;
        this.setSize(200, 60);
        this.emptyField = true;
        this.create();
    }

    public void addNotify() {
        super.addNotify();
        this.textField.requestFocus();
        this.repaint();
    }

    public void paint(Graphics g) {
        this.update(g);
    }

    public synchronized void update(Graphics g) {
        g.drawImage(this.gameContainer.imageManager.getImage("bg-lobby-password"), 0, 0, this);
    }

    public void keyPressed(KeyEvent evt) {
        if (evt.getSource() == this.textField) {
            if (this.emptyField) {
                this.emptyField = false;
                this.textField.setText("");
                this.textField.setEchoChar('*');
            }

            if (evt.getKeyCode() == 10) {
                this.joinGame();
            }
        }

    }

    public void keyReleased(KeyEvent var1) {
    }

    public void keyTyped(KeyEvent var1) {
    }

    public void actionPerformed(ActionEvent evt) {
        Object evtSource = evt.getSource();
        if (evtSource == this.buttonOk) {
            this.joinGame();
        }

        if (evtSource == this.buttonCancel) {
            this.gameContainer.gameApplet.connection.joinLobby(this.lobby);
        }

    }

    private void create() {
        this.textField = new TextField(this.gameContainer.textManager.getGame("LobbyRealPassword_EnterPassword"));
        this.textField.setBounds(25, 9, 150, 20);
        this.textField.addKeyListener(this);
        this.add(this.textField);
        this.buttonCancel = new ColorButton(this.gameContainer.textManager.getGame("LobbyRealPassword_Cancel"));
        this.buttonCancel.setBounds(25, 31, 50, 20);
        this.buttonCancel.setBackground(GameApplet.colourButtonRed);
        this.buttonCancel.addActionListener(this);
        this.add(this.buttonCancel);
        this.buttonOk = new ColorButton(this.gameContainer.textManager.getGame("LobbyRealPassword_Ok"));
        this.buttonOk.setBounds(125, 31, 50, 20);
        this.buttonOk.setBackground(GameApplet.colourButtonGreen);
        this.buttonOk.addActionListener(this);
        this.add(this.buttonOk);
        this.textField.selectAll();
    }

    private void joinGame() {
        String password = this.textField.getText().trim();
        this.gameContainer.gameApplet.connection.joinLobby(this.lobby, password);
//        if (!this.emptyField && password.length() != 0) {
//            this.gameContainer.gameApplet.connection.joinLobby(this.lobby);
//        } else {
//            this.lobbyMultiPlayerPanel.joinMultiPlayerGame(this.gameId, (String) null);
//        }
    }
}
