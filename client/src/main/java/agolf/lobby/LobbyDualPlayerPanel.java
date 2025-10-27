package agolf.lobby;

import agolf.GameContainer;
import agolf.GolfGameFrame;
import com.aapeli.client.IPanel;
import com.aapeli.client.StringDraw;
import com.aapeli.colorgui.Button;
import com.aapeli.colorgui.Checkbox;
import com.aapeli.colorgui.Choicer;
import com.aapeli.multiuser.ChallengeFloodProtection;
import com.aapeli.multiuser.NoChallCheckbox;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class LobbyDualPlayerPanel extends IPanel implements ItemListener, ActionListener {

    private GameContainer gameContainer;
    private int width;
    private int height;
    private Choicer choicerTracksNum;
    private Choicer choicerMaxStrokes;
    private Choicer choicerTimeLimit;
    private Choicer choicerTrackTypes;
    private Choicer choicerWaterEvent;
    private Choicer choicerCollision;
    private Choicer choicerScoring;
    private Choicer choicerScoringEnd;
    private Button buttonChallenge;
    private Button buttonCancel;
    private Button buttonAccept;
    private Button buttonRefuse;
    private Checkbox checkboxBeep;
    private NoChallCheckbox checkboxNoChallenges;
    private Object synchronizedObject;
    private int currentState;
    private String opponentName;
    private int tracksNum;
    private int maxStrokes;
    private int timeLimit;
    private int trackType;
    private int waterEvent;
    private int collision;
    private int scoring;
    private int scoringEnd;
    private String extraText;
    private ChallengeFloodProtection challengeFloodProtection;
    private Image image;
    private Graphics graphics;

    // private Choicer choicerTrackCategory;
    // private int trackCategory;
    // private boolean isUsingCustomServer;

    protected LobbyDualPlayerPanel(GameContainer gameContainer, int width, int height) {
        // isUsingCustomServer = Launcher.isUsingCustomServer();
        this.gameContainer = gameContainer;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.synchronizedObject = new Object();
        this.setBackground(gameContainer.imageManager.getImage("bg-lobby-dual"));
        this.create();
        this.challengeFloodProtection = new ChallengeFloodProtection();
    }

    public void update(Graphics g) {
        if (this.image == null) {
            this.image = this.createImage(this.width, this.height);
            this.graphics = this.image.getGraphics();
        }

        this.graphics.setColor(GolfGameFrame.colourGameBackground);
        this.graphics.fillRect(0, 0, this.width, this.height);
        this.drawBackground(this.graphics);
        Color colourTextOutline = new Color(102, 204, 255);
        this.graphics.setColor(GolfGameFrame.colourTextBlack);
        this.graphics.setFont(GolfGameFrame.fontSerif26b);
        StringDraw.drawString(
                this.graphics, this.gameContainer.textManager.getText("LobbySelect_DualPlayer"), this.width / 2, 32, 0);
        this.graphics.setFont(GolfGameFrame.fontSerif20);
        StringDraw.drawOutlinedString(
                this.graphics,
                colourTextOutline,
                this.gameContainer.textManager.getText("LobbyReal_ChallengeTitle"),
                this.width / 2 - 185,
                40,
                1);
        byte yPos = 73;
        byte yPos2 = 23;
        this.graphics.setFont(GolfGameFrame.fontDialog12);
        StringDraw.drawOutlinedString(
                this.graphics,
                colourTextOutline,
                this.gameContainer.textManager.getText("LobbyReal_TrackCount"),
                this.width / 2 - 185,
                yPos,
                1);
        /*if(isUsingCustomServer) {
            StringDraw.drawOutlinedString(this.graphics, colourTextOutline, "Track category:", this.width / 2 - 185, yPos + yPos2, 1);
        }*/
        StringDraw.drawOutlinedString(
                this.graphics,
                colourTextOutline,
                this.gameContainer.textManager.getText("LobbyReal_TrackTypes"),
                this.width / 2 - 185,
                yPos + yPos2 * (/*isUsingCustomServer ? 2 :*/ 1),
                1);
        StringDraw.drawOutlinedString(
                this.graphics,
                colourTextOutline,
                this.gameContainer.textManager.getText("LobbyReal_MaxStrokes"),
                this.width / 2 - 185,
                yPos + yPos2 * (/*isUsingCustomServer ? 3 :*/ 2),
                1);
        StringDraw.drawOutlinedString(
                this.graphics,
                colourTextOutline,
                this.gameContainer.textManager.getText("LobbyReal_TimeLimit"),
                this.width / 2 - 185,
                yPos + yPos2 * (/*isUsingCustomServer ? 4 :*/ 3),
                1);
        StringDraw.drawOutlinedString(
                this.graphics,
                colourTextOutline,
                this.gameContainer.textManager.getText("LobbyReal_WaterEvent"),
                this.width / 2 - 185,
                yPos + yPos2 * (/*isUsingCustomServer ? 5 :*/ 4),
                1);
        StringDraw.drawOutlinedString(
                this.graphics,
                colourTextOutline,
                this.gameContainer.textManager.getText("LobbyReal_Collision"),
                this.width / 2 - 185,
                yPos + yPos2 * (/*isUsingCustomServer ? 6 :*/ 5),
                1);
        StringDraw.drawOutlinedString(
                this.graphics,
                colourTextOutline,
                this.gameContainer.textManager.getText("LobbyReal_Scoring"),
                this.width / 2 - 185,
                yPos + yPos2 * (/*isUsingCustomServer ? 7 :*/ 6),
                1);
        StringDraw.drawOutlinedString(
                this.graphics,
                colourTextOutline,
                this.gameContainer.textManager.getText("LobbyReal_ScoringEnd"),
                this.width / 2 - 185,
                yPos + yPos2 * (/*isUsingCustomServer ? 8 :*/ 7),
                1);
        synchronized (synchronizedObject) {
            if (this.currentState > 0) {
                int xPos = this.width * 3 / 4 - 15;
                this.graphics.setFont(GolfGameFrame.fontDialog14b);
                if (this.currentState == 1) {
                    StringDraw.drawOutlinedString(
                            this.graphics,
                            colourTextOutline,
                            this.gameContainer.textManager.getText("LobbyReal_ChallengingTitle", this.opponentName),
                            xPos,
                            55,
                            0);
                }

                if (this.currentState == 2) {
                    StringDraw.drawOutlinedString(
                            this.graphics,
                            colourTextOutline,
                            this.gameContainer.textManager.getText("LobbyReal_ChallengedTitle", this.opponentName),
                            xPos,
                            55,
                            0);
                }

                this.graphics.setFont(GolfGameFrame.fontDialog12);
                yPos = 83;
                yPos2 = 17;
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.gameContainer.textManager.getText("LobbyReal_TrackCount"),
                        xPos - 5,
                        yPos,
                        1);
                StringDraw.drawOutlinedString(
                        this.graphics, colourTextOutline, String.valueOf(this.tracksNum), xPos, yPos, -1);
                /*if(isUsingCustomServer) {
                    StringDraw.drawOutlinedString(this.graphics, colourTextOutline, "Track category:", xPos - 5, yPos + yPos2, 1);
                    StringDraw.drawOutlinedString(this.graphics, colourTextOutline, this.gameContainer.textManager.getGame("LobbyReal_TrackTypes" + this.trackCategory), xPos, yPos + yPos2, -1);
                }*/
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.gameContainer.textManager.getText("LobbyReal_TrackTypes"),
                        xPos - 5,
                        yPos + yPos2 * (/*isUsingCustomServer ? 2 :*/ 1),
                        1);
                // todo check if challenger is an admin -> and shit (for the custom tracks only
                // thing)
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.gameContainer.textManager.getText("LobbyReal_TrackTypes" + this.trackType),
                        xPos,
                        yPos + yPos2 * (/*isUsingCustomServer ? 2 :*/ 1),
                        -1);
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.gameContainer.textManager.getText("LobbyReal_MaxStrokes"),
                        xPos - 5,
                        yPos + yPos2 * (/*isUsingCustomServer ? 3 :*/ 2),
                        1);
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.maxStrokes == 0
                                ? this.gameContainer.textManager.getText("LobbyReal_MaxStrokesUnlimited")
                                : String.valueOf(this.maxStrokes),
                        xPos,
                        yPos + yPos2 * (/*isUsingCustomServer ? 3 :*/ 2),
                        -1);
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.gameContainer.textManager.getText("LobbyReal_TimeLimit"),
                        xPos - 5,
                        yPos + yPos2 * (/*isUsingCustomServer ? 4 :*/ 3),
                        1);
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.gameContainer.lobbyPanel.getTime(this.timeLimit),
                        xPos,
                        yPos + yPos2 * (/*isUsingCustomServer ? 4 :*/ 3),
                        -1);
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.gameContainer.textManager.getText("LobbyReal_WaterEvent"),
                        xPos - 5,
                        yPos + yPos2 * (/*isUsingCustomServer ? 5 :*/ 4),
                        1);
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.gameContainer.textManager.getText("LobbyReal_WaterEvent" + (this.waterEvent + 1)),
                        xPos,
                        yPos + yPos2 * (/*isUsingCustomServer ? 5 :*/ 4),
                        -1);
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.gameContainer.textManager.getText("LobbyReal_Collision"),
                        xPos - 5,
                        yPos + yPos2 * (/*isUsingCustomServer ? 6 :*/ 5),
                        1);
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.gameContainer.textManager.getText("LobbyReal_Collision" + (this.collision + 1)),
                        xPos,
                        yPos + yPos2 * (/*isUsingCustomServer ? 6 :*/ 5),
                        -1);
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.gameContainer.textManager.getText("LobbyReal_Scoring"),
                        xPos - 5,
                        yPos + yPos2 * (/*isUsingCustomServer ? 7 :*/ 6),
                        1);
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.gameContainer.textManager.getText("LobbyReal_Scoring" + (this.scoring + 1)),
                        xPos,
                        yPos + yPos2 * (/*isUsingCustomServer ? 7 :*/ 6),
                        -1);
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.gameContainer.textManager.getText("LobbyReal_ScoringEnd"),
                        xPos - 5,
                        yPos + yPos2 * (/*isUsingCustomServer ? 8 :*/ 7),
                        1);
                StringDraw.drawOutlinedString(
                        this.graphics,
                        colourTextOutline,
                        this.gameContainer.textManager.getText("LobbyReal_ScoringEnd" + this.scoringEnd),
                        xPos,
                        yPos + yPos2 * (/*isUsingCustomServer ? 8 :*/ 7),
                        -1);
            }

            if (this.extraText != null) {
                StringDraw.drawOutlinedString(
                        this.graphics, colourTextOutline, this.extraText, this.width * 3 / 4, this.height - 120, 0);
            }
        }

        g.drawImage(this.image, 0, 0, this);
    }

    public void itemStateChanged(ItemEvent evt) {
        Object evtSource = evt.getSource();
        if (evtSource == this.choicerTrackTypes) {
            int trackType = this.choicerTrackTypes.getSelectedIndex();
            if (trackType == 4) {
                this.choicerMaxStrokes.select(1);
            }

            if (trackType == 5) {
                this.choicerMaxStrokes.select(2);
            }

            if (trackType == 6) {
                this.choicerMaxStrokes.select(4);
            }

            if (trackType < 4 || trackType > 6) {
                this.choicerMaxStrokes.select(3);
            }

        } else {
            if (evtSource == this.checkboxNoChallenges) {
                boolean noChallenges = this.checkboxNoChallenges.getState();
                if (noChallenges) {
                    this.refuse();
                }

                this.gameContainer.lobbyPanel.writeData("nc\t" + (noChallenges ? 't' : 'f'));
                this.gameContainer.lobbyPanel.getUser(null, noChallenges);
            }
        }
    }

    public void actionPerformed(ActionEvent evt) {
        if (this.gameContainer.golfGameFrame.syncIsValidSite.get()) {
            Object evtSource = evt.getSource();
            if (evtSource == this.buttonChallenge) {
                synchronized (synchronizedObject) {
                    if (this.currentState == 0) {
                        String challengeTarget = this.gameContainer.lobbyPanel.getSelectedNickForChallenge();
                        if (challengeTarget == null) {
                            return;
                        }

                        if (this.gameContainer.lobbyPanel.isNotAcceptingChallenges(challengeTarget)) {
                            this.extraText = this.gameContainer.textManager.getText("LobbyReal_NoChallenges");
                            this.repaint();
                            return;
                        }

                        if (!this.challengeFloodProtection.isOkToChallenge(challengeTarget)) {
                            this.gameContainer.lobbyPanel.addMessage(
                                    this.gameContainer.textManager.getText("Chat_MessageChallengeFlood"));
                            return;
                        }

                        this.opponentName = challengeTarget;
                        this.tracksNum = this.choicerTracksNum.getSelectedIndex() + 1;
                        this.trackType = this.choicerTrackTypes.getSelectedIndex();
                        this.maxStrokes = (this.choicerMaxStrokes.getSelectedIndex() + 1) * 5;
                        this.timeLimit = LobbyPanel.gameTimeLimits[this.choicerTimeLimit.getSelectedIndex()];
                        this.waterEvent = this.choicerWaterEvent.getSelectedIndex();
                        this.collision = this.choicerCollision.getSelectedIndex();
                        this.scoring = this.choicerScoring.getSelectedIndex();
                        this.scoringEnd = this.choicerScoringEnd.getSelectedIndex();
                        /*if(isUsingCustomServer) {
                            this.trackCategory = this.choicerTrackCategory.getSelectedIndex();
                        }*/
                        this.setState(1);
                        this.gameContainer.lobbyPanel.writeData("challenge\t"
                                + this.opponentName
                                + "\t"
                                + this.tracksNum
                                + "\t"
                                + this.trackType
                                + "\t"
                                + this.maxStrokes
                                + "\t"
                                + this.timeLimit
                                + "\t"
                                + this.waterEvent
                                + "\t"
                                + this.collision
                                + "\t"
                                + this.scoring
                                + "\t"
                                + this.scoringEnd /*+ (isUsingCustomServer ? ("\t" + this.trackCategory) : "")*/);
                        return;
                    }
                }

            } else if (evtSource == this.buttonCancel) {
                synchronized (synchronizedObject) {
                    if (this.currentState == 1) {
                        this.setState(0);
                        this.gameContainer.lobbyPanel.writeData("cancel\t" + this.opponentName);
                        return;
                    }
                }

            } else if (evtSource == this.buttonAccept) {
                this.accept();
            } else {
                if (evtSource == this.buttonRefuse) {
                    this.refuse();
                }
            }
        }
    }

    protected boolean handlePacket(String[] args) {
        switch (args[1]) {
            case "challenge" -> {
                synchronized (synchronizedObject) {
                    if (this.currentState == 1) {
                        this.gameContainer.lobbyPanel.writeData("cfail\t" + args[2] + "\tcother");
                        return true;
                    }
                    if (currentState == 2) {
                        this.gameContainer.lobbyPanel.writeData("cfail\t" + args[2] + "\tnochall");
                        return true;
                    }
                    if (checkboxNoChallenges.getState() || gameContainer.lobbyPanel.isUserIgnored(args[2])) {
                        this.gameContainer.lobbyPanel.writeData("cfail\t" + args[2] + "\tcbyother");
                        return true;
                    }
                    this.opponentName = args[2];
                    this.tracksNum = Integer.parseInt(args[3]);
                    this.trackType = Integer.parseInt(args[4]);
                    this.maxStrokes = Integer.parseInt(args[5]);
                    this.timeLimit = Integer.parseInt(args[6]);
                    this.waterEvent = Integer.parseInt(args[7]);
                    this.collision = Integer.parseInt(args[8]);
                    this.scoring = Integer.parseInt(args[9]);
                    this.scoringEnd = Integer.parseInt(args[10]);
                    /*if(isUsingCustomServer) {
                        this.trackCategory = Integer.parseInt(args[11]);
                    }*/
                    this.setState(2);
                }

                if (this.checkboxBeep.getState()) {
                    this.gameContainer.soundManager.playChallenge();
                }

                return true;
            }
            case "cancel" -> {
                synchronized (synchronizedObject) {
                    if (this.currentState == 2) {
                        this.setState(0);
                        return true;
                    }

                    return true;
                }
            }
            case "cfail" -> {
                synchronized (synchronizedObject) {
                    if (this.currentState != 1) {
                        return true;
                    }

                    this.setState(0);
                }

                if (args[2].equals("nouser")) {
                    this.extraText = this.gameContainer.textManager.getText("LobbyReal_NoChallengedUser");
                }

                if (args[2].equals("nochall")) {
                    this.extraText = this.gameContainer.textManager.getText("LobbyReal_NoChallenges");
                }

                if (args[2].equals("cother")) {
                    this.extraText = this.gameContainer.textManager.getText("LobbyReal_ChallengingOther");
                }

                if (args[2].equals("cbyother")) {
                    this.extraText = this.gameContainer.textManager.getText("LobbyReal_ChallengedByOther");
                }

                if (args[2].equals("refuse")) {
                    this.extraText = this.gameContainer.textManager.getText("LobbyReal_ChallengeRefused");
                }

                this.repaint();
                return true;
            }
            case "afail" -> {
                synchronized (synchronizedObject) {
                    if (this.currentState != -1) {
                        return true;
                    }

                    this.setState(0);
                }

                this.extraText = this.gameContainer.textManager.getText("LobbyReal_ChallengedLeft");
                this.repaint();
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    protected void setState(int state) {
        this.extraText = null;
        if (state == this.currentState) {
            this.repaint();
        } else {
            this.currentState = state;
            this.setVisible(false);
            this.remove(this.buttonChallenge);
            this.remove(this.buttonCancel);
            this.remove(this.buttonAccept);
            this.remove(this.buttonRefuse);
            this.remove(this.checkboxBeep);
            this.remove(this.checkboxNoChallenges);
            if (state == 0) {
                this.add(this.buttonChallenge);
                this.add(this.checkboxBeep);
                this.add(this.checkboxNoChallenges);
            }

            if (state == 1) {
                this.add(this.buttonCancel);
            }

            if (state == 2) {
                this.add(this.buttonAccept);
                this.add(this.buttonRefuse);
                this.add(this.checkboxNoChallenges);
            }

            this.setVisible(true);
            this.repaint();
        }
    }

    protected void allowChallenges() {
        this.checkboxNoChallenges.setState(false);
    }

    private void create() {
        this.setLayout(null);
        /*if(isUsingCustomServer) {
            choicerTrackCategory = this.gameContainer.lobbyPanel.addChoicerTrackCategory(this, this.width / 2 - 170, 81, 150, 20);
        }*/
        this.choicerTracksNum =
                this.gameContainer.lobbyPanel.addChoicerNumTracks(this, this.width / 2 - 170, 58, 50, 20);
        this.choicerTrackTypes = this.gameContainer.lobbyPanel.addChoicerTrackTypes(
                this, this.width / 2 - 170, /*isUsingCustomServer ? 104 :*/ 81, 150, 20);
        this.choicerTrackTypes.addItemListener(this);
        this.choicerMaxStrokes = this.gameContainer.lobbyPanel.addChoicerMaxStrokes(
                this, this.width / 2 - 170, /*isUsingCustomServer ? 127 :*/ 104, 100, 20);
        this.choicerTimeLimit = this.gameContainer.lobbyPanel.addChoicerTimeLimit(
                this, this.width / 2 - 170, /*isUsingCustomServer ? 150 :*/ 127, 100, 20);
        this.choicerWaterEvent = this.gameContainer.lobbyPanel.addChoicerWaterEvent(
                this, this.width / 2 - 170, /*isUsingCustomServer ? 173 :*/ 150, 150, 20);
        this.choicerCollision = this.gameContainer.lobbyPanel.addChoicerCollision(
                this, this.width / 2 - 170, /*isUsingCustomServer ? 196 :*/ 173, 100, 20);
        this.choicerScoring = this.gameContainer.lobbyPanel.addChoicerScoring(
                this, this.width / 2 - 170, /*isUsingCustomServer ? 219 :*/ 196, 150, 20);
        this.choicerScoringEnd = this.gameContainer.lobbyPanel.addChoicerScoringEnd(
                this, this.width / 2 - 170, /*isUsingCustomServer ? 242 :*/ 219, 100, 20);
        this.buttonChallenge = new Button(this.gameContainer.textManager.getText("LobbyReal_Challenge"));
        this.buttonChallenge.setBackground(GolfGameFrame.colourButtonGreen);
        this.buttonChallenge.setBounds(
                this.width / 2 - 170, this.height - 15 - 25 + (/*isUsingCustomServer ? 10 :*/ 0), 100, 25);
        this.buttonChallenge.addActionListener(this);
        this.add(this.buttonChallenge);
        this.buttonCancel = new Button(this.gameContainer.textManager.getText("LobbyReal_Cancel"));
        this.buttonCancel.setBackground(GolfGameFrame.colourButtonYellow);
        this.buttonCancel.setBounds(this.width * 3 / 4 - 50, this.height - 40 - 25, 100, 25);
        this.buttonCancel.addActionListener(this);
        this.buttonAccept = new Button(this.gameContainer.textManager.getText("LobbyReal_Accept"));
        this.buttonAccept.setBackground(GolfGameFrame.colourButtonGreen);
        this.buttonAccept.setBounds(this.width * 3 / 4 - 100 - 10, this.height - 40 - 25, 100, 25);
        this.buttonAccept.addActionListener(this);
        this.buttonRefuse = new Button(this.gameContainer.textManager.getText("LobbyReal_Refuse"));
        this.buttonRefuse.setBackground(GolfGameFrame.colourButtonRed);
        this.buttonRefuse.setBounds(this.width * 3 / 4 + 10, this.height - 40 - 25, 100, 25);
        this.buttonRefuse.addActionListener(this);
        int var1 = this.width * 3 / 4 - this.width / 8 - 10;
        this.checkboxBeep = new Checkbox(this.gameContainer.textManager.getText("LobbyReal_BeepOnChallenge"), true);
        this.checkboxBeep.setBounds(var1, this.height - 10 - 18 - 5 - 18, this.width / 4 + 20, 18);
        this.add(this.checkboxBeep);
        this.checkboxNoChallenges =
                new NoChallCheckbox(this.gameContainer.textManager.getText("LobbyReal_NoChallengesToMe"));
        this.checkboxNoChallenges.setBounds(var1, this.height - 10 - 18, this.width / 4 + 20, 18);
        this.checkboxNoChallenges.addItemListener(this);
        this.add(this.checkboxNoChallenges);
    }

    private void accept() {
        synchronized (synchronizedObject) {
            if (this.currentState == 2) {
                this.setState(-1);
                this.gameContainer.lobbyPanel.writeData("accept\t" + this.opponentName);
                return;
            }
        }
    }

    private void refuse() {
        synchronized (synchronizedObject) {
            if (this.currentState == 2) {
                this.setState(0);
                this.gameContainer.lobbyPanel.writeData("cfail\t" + this.opponentName + "\trefuse");
                return;
            }
        }
    }
}
