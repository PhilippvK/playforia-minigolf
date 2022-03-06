package agolf;

import com.aapeli.client.StringDraw;
import com.aapeli.colorgui.Choicer;
import com.aapeli.colorgui.ColorButton;
import com.aapeli.colorgui.ColorCheckbox;
import com.aapeli.tools.Tools;
import org.moparforia.shared.game.LobbyType;
import org.moparforia.shared.networking.packets.SelectGameTypePacket;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LobbySelectPanel extends Panel implements ActionListener, MouseListener, ItemListener {

    private static final int[] anIntArray526 = new int[]{3000, 7000, 15000};
    private static final int anInt527 = anIntArray526.length;
    private static long[] aLongArray528;
    private static boolean playHidden;
    private GameContainer gameContainer;
    private int width;
    private int height;
    private int lobbyMaxPlayers;
    private ColorButton buttonSingle;
    private ColorButton buttonSingleQuick;
    private ColorButton buttonDual;
    private ColorButton buttonMulti;
    private ColorButton buttonMultiQuick;
    private ColorButton buttonQuit;
    private ColorCheckbox checkboxPlayHidden;
    private Choicer choicerGraphics;
    private int[] lobbyNumPlayers;
    private static final String[] aStringArray544 = new String[22];


    protected LobbySelectPanel(GameContainer gameContainer, int width, int height) {
        this.gameContainer = gameContainer;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.lobbyMaxPlayers = 200;
        this.lobbyNumPlayers = new int[3];
        this.lobbyNumPlayers[0] = this.lobbyNumPlayers[1] = this.lobbyNumPlayers[2] = -1;
        this.create();
        this.addMouseListener(this);
    }

    public void addNotify() {
        super.addNotify();
        this.repaint();
    }

    public void paint(Graphics g) {
        this.update(g);
    }

    public void update(Graphics g) {
        g.drawImage(this.gameContainer.imageManager.getImage("bg-lobbyselect"), 0, 0, this);
        g.setColor(Color.black);
        g.setFont(GameApplet.fontSerif26b);
        if (!this.gameContainer.disableSinglePlayer) {
            StringDraw.drawString(g, this.gameContainer.textManager.getGame("LobbySelect_SinglePlayer"), this.width / 6, 70, 0);
        }

        StringDraw.drawString(g, this.gameContainer.textManager.getGame("LobbySelect_DualPlayer"), this.width * 3 / 6, 70, 0);
        StringDraw.drawString(g, this.gameContainer.textManager.getGame("LobbySelect_MultiPlayer"), this.width * 5 / 6, 70, 0);
        g.setFont(GameApplet.fontDialog12);
        if (!this.gameContainer.disableSinglePlayer) {
            this.drawNumPlayers(g, this.width / 6, this.lobbyNumPlayers[0]);
        }

        this.drawNumPlayers(g, this.width * 3 / 6, this.lobbyNumPlayers[1]);
        this.drawNumPlayers(g, this.width * 5 / 6, this.lobbyNumPlayers[2]);
    }

    public void mouseEntered(MouseEvent evt) {
    }

    public void mouseExited(MouseEvent evt) {
    }

    public void mousePressed(MouseEvent evt) {
        if (evt.getY() < 390) {
            int mouseX = evt.getX();
            if (!this.gameContainer.disableSinglePlayer && mouseX < this.width / 3) {
                this.selectLobby(1);
                return;
            }
            /*
            if (mouseX >= this.width / 3 && mouseX < this.width * 2 / 3) {
                this.selectLobby(2);
                return;
            }
            */

            if (mouseX >= this.width * 2 / 3) {
                this.selectLobby(3);
            }
        }

    }

    public void mouseReleased(MouseEvent var1) {
    }

    public void mouseClicked(MouseEvent var1) {
    }

    public void actionPerformed(ActionEvent evt) {
        playHidden = this.checkboxPlayHidden.getState();
        Object evtSource = evt.getSource();
        if (!this.gameContainer.disableSinglePlayer && evtSource == this.buttonSingle) {
            this.selectLobby(1);

        /*}
         else if (evtSource == this.buttonDual) {
            this.selectLobby(2);
        */
        } else if (evtSource == this.buttonMulti) {
            this.selectLobby(3);
        } else if (evtSource == this.buttonSingleQuick) {
            this.gameContainer.gameApplet.setGameState(0);
            this.writeData("cspt\t10\t1\t0");
        } else if (evtSource == this.buttonMultiQuick) {
            this.gameContainer.gameApplet.setGameState(0);
            this.writeData("qmpt");
        } else {
            if (evtSource == this.buttonQuit) {
                this.gameContainer.gameApplet.quit((String) null);
            }

        }
    }

    public void itemStateChanged(ItemEvent evt) {
        this.gameContainer.graphicsQualityIndex = this.choicerGraphics.getSelectedIndex();
    }

    protected boolean method443(int var1, boolean var2) {
        if (this.gameContainer.disableSinglePlayer && var1 == 1) {
            return false;
        } else {
            this.checkboxPlayHidden.setState(var2);
            playHidden = var2;
            return this.selectLobby(var1);
        }
    }

    public void setNumberOfPlayers(int single, int dual, int multi) {
        lobbyNumPlayers[0] = single;
        lobbyNumPlayers[1] = dual;
        lobbyNumPlayers[2] = multi;
        this.repaint();
    }

    protected void writeData(String var1) {
//        this.gameContainer.connection.writeData("lobbyselect\t" + var1);
    }

    private void create() {
        this.setLayout((LayoutManager) null);
        this.checkboxPlayHidden = new ColorCheckbox(this.gameContainer.textManager.getGame("LobbySelect_PlayHidden"));
        this.checkboxPlayHidden.setBounds(this.width / 6 - 75 - 10, this.height - 124, 220, 18);
        this.checkboxPlayHidden.setBackground(GameApplet.colourGameBackground);
        if (!this.gameContainer.disableSinglePlayer) {
            this.add(this.checkboxPlayHidden);
            this.buttonSingle = new ColorButton(this.gameContainer.textManager.getGame("LobbySelect_SinglePlayer"));
            this.buttonSingle.setBounds(this.width / 6 - 75, this.height - 150, 150, 25);
            this.buttonSingle.addActionListener(this);
            this.add(this.buttonSingle);
            this.buttonSingleQuick = new ColorButton(this.gameContainer.textManager.getGame("LobbySelect_QuickStart"));
            this.buttonSingleQuick.setBackground(GameApplet.colourButtonBlue);
            this.buttonSingleQuick.setBounds(this.width / 6 - 50, this.height - 95, 100, 20);
            this.buttonSingleQuick.addActionListener(this);
            this.add(this.buttonSingleQuick);
        }



        //this.buttonDual = new ColorButton(this.gameContainer.textManager.getGame("LobbySelect_DualPlayer"));
        this.buttonDual = new ColorButton("Coming soon...");
        this.buttonDual.setBounds(this.width * 3 / 6 - 75, this.height - 150, 150, 25);
        //this.buttonDual.addActionListener(this);
        this.add(this.buttonDual);


        this.buttonMulti = new ColorButton(this.gameContainer.textManager.getGame("LobbySelect_MultiPlayer"));
        this.buttonMulti.setBounds(this.width * 5 / 6 - 75, this.height - 150, 150, 25);
        this.buttonMulti.addActionListener(this);
        this.add(this.buttonMulti);
        this.buttonMultiQuick = new ColorButton(this.gameContainer.textManager.getGame("LobbySelect_QuickStart"));
        this.buttonMultiQuick.setBackground(GameApplet.colourButtonBlue);
        this.buttonMultiQuick.setBounds(this.width * 5 / 6 - 50, this.height - 95, 100, 20);
        this.buttonMultiQuick.addActionListener(this);
        this.add(this.buttonMultiQuick);
        String text = this.gameContainer.textManager.getGame("LobbySelect_Gfx");
        this.choicerGraphics = new Choicer();
        this.choicerGraphics.addItem(text + " " + this.gameContainer.textManager.getGame("LobbySelect_Gfx0"));
        this.choicerGraphics.addItem(text + " " + this.gameContainer.textManager.getGame("LobbySelect_Gfx1"));
        this.choicerGraphics.addItem(text + " " + this.gameContainer.textManager.getGame("LobbySelect_Gfx2"));
        this.choicerGraphics.addItem(text + " " + this.gameContainer.textManager.getGame("LobbySelect_Gfx3"));
        this.choicerGraphics.select(this.gameContainer.graphicsQualityIndex);
        this.choicerGraphics.setBounds(this.width / 3 - 100, this.height - 10 - 20, 200, 20);
        this.choicerGraphics.addItemListener(this);
        this.add(this.choicerGraphics);
        this.buttonQuit = new ColorButton(this.gameContainer.textManager.getGame("LobbySelect_Quit"));
        this.buttonQuit.setBackground(GameApplet.colourButtonRed);
        this.buttonQuit.setBounds(this.width * 2 / 3 - 50, this.height - 10 - 20, 100, 20);
        this.buttonQuit.addActionListener(this);
        this.add(this.buttonQuit);
    }

    private void drawNumPlayers(Graphics g, int x, int numPlayers) {
        if (numPlayers != -1) {
            String text = null;
            if (numPlayers == 0) {
                text = this.gameContainer.textManager.getGame("LobbySelect_Players0");
            } else if (numPlayers == 1) {
                text = this.gameContainer.textManager.getGame("LobbySelect_Players1");
            } else if (numPlayers >= 2) {
                text = this.gameContainer.textManager.getGame("LobbySelect_PlayersX", numPlayers);
            }

            StringDraw.drawString(g, text, x, 110, 0);
            if (numPlayers >= this.lobbyMaxPlayers && this.gameContainer.gameApplet.getPlayerAccessLevel() == 0) {
                StringDraw.drawString(g, this.gameContainer.textManager.getGame("LobbySelect_Full"), x, 130, 0);
            }

        }
    }

    private boolean selectLobby(int lobby) {
        this.gameContainer.gameApplet.setGameState(0);
        this.gameContainer.gameApplet.connection.send(new SelectGameTypePacket(LobbyType.getLobby(lobby)));
        return true;
    }
}
