package agolf;

import com.aapeli.client.StringDraw;
import com.aapeli.colorgui.Button;
import com.aapeli.colorgui.Checkbox;
import com.aapeli.colorgui.Choicer;
import com.aapeli.tools.Tools;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LobbySelectPanel extends Panel implements ActionListener, MouseListener, ItemListener {

    private static final int[] lobbyJoinDelays = new int[] {1000};
    private static long[] lobbyJoinAvailableTimestamps;
    private static boolean playHidden;
    private GameContainer gameContainer;
    private int width;
    private int height;
    private int lobbyMaxPlayers;
    private Button buttonSingle;
    private Button buttonSingleQuick;
    private Button buttonDual;
    private Button buttonMulti;
    private Button buttonMultiQuick;
    private Button buttonQuit;
    private Checkbox checkboxPlayHidden;
    private Choicer choicerGraphics;
    private Choicer audioChoicer;
    private int[] lobbyNumPlayers;
    private LobbySelectNumberOfPlayersFetcher lobbySelectNumberOfPlayersFetcher;

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
        g.setFont(GolfGameFrame.fontSerif26b);
        if (!this.gameContainer.disableSinglePlayer) {
            StringDraw.drawString(
                    g, this.gameContainer.textManager.getText("LobbySelect_SinglePlayer"), this.width / 6, 70, 0);
        }

        StringDraw.drawString(
                g, this.gameContainer.textManager.getText("LobbySelect_DualPlayer"), this.width * 3 / 6, 70, 0);
        StringDraw.drawString(
                g, this.gameContainer.textManager.getText("LobbySelect_MultiPlayer"), this.width * 5 / 6, 70, 0);
        g.setFont(GolfGameFrame.fontDialog12);
        if (!this.gameContainer.disableSinglePlayer) {
            this.drawNumPlayers(g, this.width / 6, this.lobbyNumPlayers[0]);
        }

        this.drawNumPlayers(g, this.width * 3 / 6, this.lobbyNumPlayers[1]);
        this.drawNumPlayers(g, this.width * 5 / 6, this.lobbyNumPlayers[2]);
    }

    public void mouseEntered(MouseEvent evt) {}

    public void mouseExited(MouseEvent evt) {}

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

    public void mouseReleased(MouseEvent var1) {}

    public void mouseClicked(MouseEvent var1) {}

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
            this.gameContainer.golfGameFrame.setGameState(0);
            this.writeData("cspt\t10\t1\t0");
        } else if (evtSource == this.buttonMultiQuick) {
            this.gameContainer.golfGameFrame.setGameState(0);
            this.writeData("qmpt");
        } else {
            if (evtSource == this.buttonQuit) {
                this.gameContainer.golfGameFrame.quit(null);
            }
        }
    }

    public void itemStateChanged(ItemEvent evt) {
        Object source = evt.getSource();
        if (source == this.choicerGraphics) {
            this.gameContainer.graphicsQualityIndex = this.choicerGraphics.getSelectedIndex();
        } else if (source == this.audioChoicer) {
            this.gameContainer.soundManager.audioChoicerIndex = this.audioChoicer.getSelectedIndex();
        }
    }

    public static String getLobbySelectMessage(int lobbyId) {
        if (lobbyJoinAvailableTimestamps == null) {
            lobbyJoinAvailableTimestamps = new long[lobbyJoinDelays.length];

            for (int i = 0; i < lobbyJoinDelays.length; ++i) {
                lobbyJoinAvailableTimestamps[i] = 0L;
            }
        }

        long now = System.currentTimeMillis();
        long sleepTime = 0L;

        for (int i = 0; i < lobbyJoinDelays.length; ++i) {
            long delayNeededForTimestamp = lobbyJoinAvailableTimestamps[i] + (long) lobbyJoinDelays[i] - now;
            if (delayNeededForTimestamp > sleepTime) {
                sleepTime = delayNeededForTimestamp;
            }
        }

        for (int i = lobbyJoinDelays.length - 1; i >= 1; --i) {
            lobbyJoinAvailableTimestamps[i] = lobbyJoinAvailableTimestamps[i - 1];
        }

        lobbyJoinAvailableTimestamps[0] = now + sleepTime;
        Tools.sleep(sleepTime);
        return "select\t" + (lobbyId <= 2 ? String.valueOf(lobbyId) : "x") + (lobbyId == 1 && playHidden ? "h" : "");
    }

    protected boolean selectLobby(int lobbyId, boolean playHidden) {
        if (this.gameContainer.disableSinglePlayer && lobbyId == 1) {
            return false;
        } else {
            this.checkboxPlayHidden.setState(playHidden);
            LobbySelectPanel.playHidden = playHidden;
            return this.selectLobby(lobbyId);
        }
    }

    protected void resetNumberOfPlayersFetcher() {
        this.destroyNumberOfPlayersFetcher();
        this.lobbySelectNumberOfPlayersFetcher = new LobbySelectNumberOfPlayersFetcher(this);
    }

    protected void destroyNumberOfPlayersFetcher() {
        if (this.lobbySelectNumberOfPlayersFetcher != null) {
            this.lobbySelectNumberOfPlayersFetcher.stop();
            this.lobbySelectNumberOfPlayersFetcher = null;
        }
    }

    protected void handlePacket(String[] args) {
        if (args[1].equals("nop")) {
            for (int i = 0; i < 3; ++i) {
                this.lobbyNumPlayers[i] = Integer.parseInt(args[2 + i]);
            }

            this.repaint();
        }
    }

    protected void writeData(String var1) {
        this.gameContainer.connection.writeData("lobbyselect\t" + var1);
    }

    private void create() {
        this.setLayout(null);
        this.checkboxPlayHidden = new Checkbox(this.gameContainer.textManager.getText("LobbySelect_PlayHidden"));
        this.checkboxPlayHidden.setBounds(this.width / 6 - 75 - 10, this.height - 124, 220, 18);
        this.checkboxPlayHidden.setBackground(GolfGameFrame.colourGameBackground);
        if (!this.gameContainer.disableSinglePlayer) {
            this.add(this.checkboxPlayHidden);
            this.buttonSingle = new Button(this.gameContainer.textManager.getText("LobbySelect_SinglePlayer"));
            this.buttonSingle.setBounds(this.width / 6 - 75, this.height - 150, 150, 25);
            this.buttonSingle.addActionListener(this);
            this.add(this.buttonSingle);
            this.buttonSingleQuick = new Button(this.gameContainer.textManager.getText("LobbySelect_QuickStart"));
            this.buttonSingleQuick.setBackground(GolfGameFrame.colourButtonBlue);
            this.buttonSingleQuick.setBounds(this.width / 6 - 50, this.height - 95, 100, 20);
            this.buttonSingleQuick.addActionListener(this);
            this.add(this.buttonSingleQuick);
        }

        // this.buttonDual = new
        // Button(this.gameContainer.textManager.getGame("LobbySelect_DualPlayer"));
        this.buttonDual = new Button("Coming soon...");
        this.buttonDual.setBounds(this.width * 3 / 6 - 75, this.height - 150, 150, 25);
        // this.buttonDual.addActionListener(this);
        this.add(this.buttonDual);

        this.buttonMulti = new Button(this.gameContainer.textManager.getText("LobbySelect_MultiPlayer"));
        this.buttonMulti.setBounds(this.width * 5 / 6 - 75, this.height - 150, 150, 25);
        this.buttonMulti.addActionListener(this);
        this.add(this.buttonMulti);

        this.buttonMultiQuick = new Button(this.gameContainer.textManager.getText("LobbySelect_QuickStart"));
        this.buttonMultiQuick.setBackground(GolfGameFrame.colourButtonBlue);
        this.buttonMultiQuick.setBounds(this.width * 5 / 6 - 50, this.height - 95, 100, 20);
        this.buttonMultiQuick.addActionListener(this);
        this.add(this.buttonMultiQuick);

        String graphicsOptionText = this.gameContainer.textManager.getText("LobbySelect_Gfx");
        this.choicerGraphics = new Choicer();
        this.choicerGraphics.addItem(
                graphicsOptionText + " " + this.gameContainer.textManager.getText("LobbySelect_Gfx0"));
        this.choicerGraphics.addItem(
                graphicsOptionText + " " + this.gameContainer.textManager.getText("LobbySelect_Gfx1"));
        this.choicerGraphics.addItem(
                graphicsOptionText + " " + this.gameContainer.textManager.getText("LobbySelect_Gfx2"));
        this.choicerGraphics.addItem(
                graphicsOptionText + " " + this.gameContainer.textManager.getText("LobbySelect_Gfx3"));
        this.choicerGraphics.select(this.gameContainer.graphicsQualityIndex);
        this.choicerGraphics.setBounds(this.width / 3 - 100, this.height - 10 - 50, 200, 20);
        this.choicerGraphics.addItemListener(this);
        this.add(this.choicerGraphics);

        String audioOptionsText = "Audio:";
        this.audioChoicer = new Choicer();
        this.audioChoicer.addItem(audioOptionsText + " On");
        this.audioChoicer.addItem(audioOptionsText + " Off");
        this.audioChoicer.select(this.gameContainer.soundManager.audioChoicerIndex);
        this.audioChoicer.setBounds(this.width / 3 - 100, this.height - 10 - 20, 200, 20);
        this.audioChoicer.addItemListener(this);
        this.add(this.audioChoicer);

        this.buttonQuit = new Button(this.gameContainer.textManager.getText("LobbySelect_Quit"));
        this.buttonQuit.setBackground(GolfGameFrame.colourButtonRed);
        this.buttonQuit.setBounds(this.width * 2 / 3 - 50, this.height - 10 - 20, 100, 20);
        this.buttonQuit.addActionListener(this);
        this.add(this.buttonQuit);
    }

    private void drawNumPlayers(Graphics g, int x, int numPlayers) {
        if (numPlayers != -1) {
            String text = null;
            if (numPlayers == 0) {
                text = this.gameContainer.textManager.getText("LobbySelect_Players0");
            } else if (numPlayers == 1) {
                text = this.gameContainer.textManager.getText("LobbySelect_Players1");
            } else if (numPlayers >= 2) {
                text = this.gameContainer.textManager.getText("LobbySelect_PlayersX", numPlayers);
            }

            StringDraw.drawString(g, text, x, 110, 0);
            if (numPlayers >= this.lobbyMaxPlayers && this.gameContainer.golfGameFrame.getPlayerAccessLevel() == 0) {
                StringDraw.drawString(g, this.gameContainer.textManager.getText("LobbySelect_Full"), x, 130, 0);
            }
        }
    }

    private boolean selectLobby(int lobbyId) {
        if (this.lobbyNumPlayers[lobbyId - 1] >= this.lobbyMaxPlayers
                && this.gameContainer.golfGameFrame.getPlayerAccessLevel() == 0) {
            return false;
        } else {
            this.gameContainer.golfGameFrame.setGameState(0);
            this.writeData(getLobbySelectMessage(lobbyId));
            return true;
        }
    }

    protected void requestNumberOfPlayers() {
        this.writeData("rnop");
    }
}
