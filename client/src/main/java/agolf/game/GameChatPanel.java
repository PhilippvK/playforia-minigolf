package agolf.game;

import agolf.GameApplet;
import agolf.GameContainer;
import com.aapeli.client.InputTextField;
import com.aapeli.client.UrlLabel;
import com.aapeli.colorgui.ColorButton;
import com.aapeli.multiuser.ChatBase;
import com.aapeli.multiuser.ChatTextArea;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class GameChatPanel extends Panel implements KeyListener, ActionListener {

    private static final int[] anIntArray330 = new int[]{3, 1, 4, 2, 3, 1, 4, 2, 3, 1};
    private GameContainer gameContainer;
    private int width;
    private int height;
    private ChatTextArea textAreaChat;
    private InputTextField textFieldMessage;
    private ColorButton buttonSay;
    private int gameExtra;
    private boolean created;


    protected GameChatPanel(GameContainer gameContainer, int width, int height, int gameExtra) {
        this.gameContainer = gameContainer;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.gameExtra = gameExtra;
        this.created = false;
    }

    public void addNotify() {
        super.addNotify();
        if (!this.created) {
            this.created = true;
            this.create();
        }

        this.repaint();
    }

    public void paint(Graphics g) {
        this.update(g);
    }

    public void update(Graphics g) {
        g.setColor(GameApplet.colourGameBackground);
        g.fillRect(0, 0, this.width, this.height);
    }

    public void keyPressed(KeyEvent evt) {
        if (evt.getSource() == this.textFieldMessage && evt.getKeyCode() == 10) {
            this.sendMessage();
        }

    }

    public void keyReleased(KeyEvent evt) {
    }

    public void keyTyped(KeyEvent evt) {
    }

    public void actionPerformed(ActionEvent evt) {
        if (this.gameExtra >= 2) {
            if (evt.getSource() == this.buttonSay) {
                this.sendMessage();
            }

        }
    }

    protected void setUserColour(String user, int var2) {
        this.textAreaChat.setUserColor(user, anIntArray330[var2]);
    }

    protected void removeUserColour(String var1) {
        this.textAreaChat.removeUserColor(var1);
    }

    protected void addMessage(String var1) {
        if (this.gameExtra >= 2) {
            this.textAreaChat.addMessage(var1);
        }
    }

    protected void addSay(int unused, String name, String message, boolean isLocalPlayer) {// /me action
        if (this.gameExtra >= 2) {
            if (isLocalPlayer) {
                this.textAreaChat.addOwnSay(name, message);
            } else {
                this.textAreaChat.addSay(name, message);
            }
        }
    }

    protected void printSpecialSettingstoTextArea(String comment, String settings, boolean[] elements) {
        if (this.gameExtra == 1) {
            this.textAreaChat.clear();
            if (comment != null) {
                this.textAreaChat.addMessage("Comment:");
                this.textAreaChat.addPlainMessage(comment);
            }

            if (settings != null) {
                this.textAreaChat.addMessage("Special settings:");
                if (settings.charAt(0) == 't') {
                    this.textAreaChat.addPlainMessage("- Mines visible");
                }

                if (settings.charAt(1) == 'f') {
                    this.textAreaChat.addPlainMessage("- Magnets invisible");
                }

                if (settings.charAt(2) == 'f') {
                    this.textAreaChat.addPlainMessage("- Teleport colors hidden");
                }

                if (settings.charAt(3) == 'f') {
                    this.textAreaChat.addPlainMessage("- Illusionwalls shadowless");
                }

                int var4 = settings.charAt(4) - '0';
                int var5 = settings.charAt(5) - '0';
                if (var4 > 1 || var5 < 4) {
                    this.textAreaChat.addPlainMessage("- Number of players: " + var4 + "-" + var5);
                }
            }

            if (elements[0] || elements[1]) {
                this.textAreaChat.addMessage("Special (invisible) elements:");
                if (elements[0]) {
                    this.textAreaChat.addPlainMessage("- Illusion walls");
                }

                if (elements[1]) {
                    this.textAreaChat.addPlainMessage("- Illusion holes");
                }
            }

        }
    }

    protected boolean haveFocus() {
        return this.textFieldMessage.haveFocus();
    }

    protected void addBroadcastMessage(String message) {
        this.textAreaChat.addBroadcastMessage(message);
    }

    private void create() {
        this.setLayout((LayoutManager) null);
        this.textAreaChat = new ChatTextArea(this.gameContainer.textManager, this.gameContainer.badWordFilter, this.width, this.height - 22, new Font("Dialog", 0, 11));
        this.textAreaChat.setLocation(0, 0);
        this.textFieldMessage = new InputTextField(this.gameContainer.textManager.getGame("GameChat_TypeHere"), 200, true);
        this.textFieldMessage.setBounds(0, this.height - 20, this.width - 70, 20);
        this.buttonSay = new ColorButton(this.gameContainer.textManager.getGame("GameChat_Say"));
        this.buttonSay.setBounds(this.width - 69, this.height - 20, 69, 20);
        this.buttonSay.setBackground(GameApplet.colourButtonBlue);
        if (this.gameExtra > 0) {
            this.add(this.textAreaChat);
            if (this.gameExtra == 2) {
                this.add(this.textFieldMessage);
                this.add(this.buttonSay);
                this.textFieldMessage.addKeyListener(this);
                this.buttonSay.addActionListener(this);
            }
        }

        this.method323();
    }

    private void method323() {
        if (this.gameExtra == 2) {
            byte var1 = ChatBase.CIDR_NONE;
            if (!this.gameContainer.gameApplet.isEmailVerified()) {
                var1 = ChatBase.CIDR_UNREG;
            } else if (this.gameContainer.gameApplet.isGuestChatDisabled()) {
                var1 = ChatBase.CIDR_UNCONF;
            }

            if (var1 != ChatBase.CIDR_NONE) {
                ChatBase.setInputByCIDR(var1, this, this.textFieldMessage, this.buttonSay, (UrlLabel) null, this.gameContainer.textManager, this.gameContainer.params);
            }
        }

    }

    private void sendMessage() {
        String message = this.textFieldMessage.getInputText();
        if (message.length() != 0) {
            this.gameContainer.gamePanel.sendChatMessage(message);
        }
    }
}
