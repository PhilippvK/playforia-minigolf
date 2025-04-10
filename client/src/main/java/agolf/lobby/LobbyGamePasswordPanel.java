package agolf.lobby;

import agolf.GameContainer;
import agolf.GolfGameFrame;
import com.aapeli.colorgui.Button;
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
    private int gameId;
    private TextField textField;
    private Button buttonOk;
    private Button buttonCancel;
    private boolean emptyField;

    protected LobbyGamePasswordPanel(
            GameContainer gameContainer, LobbyMultiPlayerPanel lobbyMultiPlayerPanel, int gameId) {
        this.gameContainer = gameContainer;
        this.lobbyMultiPlayerPanel = lobbyMultiPlayerPanel;
        this.gameId = gameId;
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

    public void keyReleased(KeyEvent var1) {}

    public void keyTyped(KeyEvent var1) {}

    public void actionPerformed(ActionEvent evt) {
        Object evtSource = evt.getSource();
        if (evtSource == this.buttonOk) {
            this.joinGame();
        }

        if (evtSource == this.buttonCancel) {
            this.lobbyMultiPlayerPanel.joinMultiPlayerGame(this.gameId, null);
        }
    }

    private void create() {
        this.textField = new TextField(this.gameContainer.textManager.getText("LobbyRealPassword_EnterPassword"));
        this.textField.setBounds(25, 9, 150, 20);
        this.textField.addKeyListener(this);
        this.add(this.textField);
        this.buttonCancel = new Button(this.gameContainer.textManager.getText("LobbyRealPassword_Cancel"));
        this.buttonCancel.setBounds(25, 31, 50, 20);
        this.buttonCancel.setBackground(GolfGameFrame.colourButtonRed);
        this.buttonCancel.addActionListener(this);
        this.add(this.buttonCancel);
        this.buttonOk = new Button(this.gameContainer.textManager.getText("LobbyRealPassword_Ok"));
        this.buttonOk.setBounds(125, 31, 50, 20);
        this.buttonOk.setBackground(GolfGameFrame.colourButtonGreen);
        this.buttonOk.addActionListener(this);
        this.add(this.buttonOk);
        this.textField.selectAll();
    }

    private void joinGame() {
        String password = this.textField.getText().trim();
        if (!this.emptyField && password.length() != 0) {
            this.lobbyMultiPlayerPanel.joinMultiPlayerGame(this.gameId, password);
        } else {
            this.lobbyMultiPlayerPanel.joinMultiPlayerGame(this.gameId, null);
        }
    }
}
