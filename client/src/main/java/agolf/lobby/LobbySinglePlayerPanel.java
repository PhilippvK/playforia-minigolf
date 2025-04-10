package agolf.lobby;

import agolf.GameContainer;
import agolf.GolfGameFrame;
import com.aapeli.client.StringDraw;
import com.aapeli.colorgui.Button;
import com.aapeli.colorgui.Choicer;
import com.aapeli.colorgui.MultiColumnListItem;
import com.aapeli.colorgui.MultiColumnListListener;
import com.aapeli.colorgui.MultiColumnSelectableList;
import com.aapeli.colorgui.SortOrder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;
import org.moparforia.client.Launcher;
import org.moparforia.shared.tracks.TrackSet;
import org.moparforia.shared.tracks.TrackSetDifficulty;

class LobbySinglePlayerPanel extends Panel implements ItemListener, ActionListener, MultiColumnListListener<TrackSet> {

    private GameContainer gameContainer;
    private int width;
    private int height;
    private Choicer choicerTracksNum;
    private Choicer choicerTrackTypes;
    private Choicer choicerWaterEvent;
    private MultiColumnSelectableList<TrackSet> trackSetList;
    private Button buttonStartTraining;
    private Button buttonStartChampionship;
    private TrackSet selectedTrackSet;
    private boolean requestTrackSetList;
    private Image image;
    private Graphics graphics;
    // private Choicer choicerTrackCategory;
    private boolean isUsingCustomServer;

    protected LobbySinglePlayerPanel(GameContainer var1, int var2, int var3) {
        isUsingCustomServer = Launcher.isUsingCustomServer();
        this.gameContainer = var1;
        this.width = var2;
        this.height = var3;
        this.setSize(var2, var3);
        this.create();
        this.requestTrackSetList = true;
    }

    public void addNotify() {
        super.addNotify();
        this.repaint();
    }

    public void paint(Graphics var1) {
        this.update(var1);
    }

    public void update(Graphics g) {
        if (this.image == null) {
            this.image = this.createImage(this.width, this.height);
            this.graphics = this.image.getGraphics();
        }

        this.graphics.setColor(GolfGameFrame.colourGameBackground);
        this.graphics.fillRect(0, 0, this.width, this.height);
        this.graphics.drawImage(this.gameContainer.imageManager.getImage("bg-lobby-single"), 0, 0, this);
        Color trainingOutlineColour = new Color(14, 219, 14);
        Color championshipOutlineColour = new Color(0, 205, 0);
        this.graphics.setColor(GolfGameFrame.colourTextBlack);
        this.graphics.setFont(GolfGameFrame.fontSerif26b);
        StringDraw.drawString(
                this.graphics,
                this.gameContainer.textManager.getText("LobbySelect_SinglePlayer"),
                this.width / 2,
                37,
                0);
        byte yPos = -45;
        this.graphics.setFont(GolfGameFrame.fontSerif20);
        StringDraw.drawOutlinedString(
                this.graphics,
                trainingOutlineColour,
                this.gameContainer.textManager.getText("LobbyReal_TrainingTitle"),
                this.width / 4,
                170 + yPos,
                0);
        StringDraw.drawOutlinedString(
                this.graphics,
                championshipOutlineColour,
                this.gameContainer.textManager.getText("LobbyReal_ChampionshipTitle"),
                this.width * 3 / 4,
                195 + yPos - 15 - 15,
                0);
        this.graphics.setFont(GolfGameFrame.fontDialog12);
        yPos = -45;
        /*if(isUsingCustomServer) {
            StringDraw.drawOutlinedString(this.graphics, trainingOutlineColour, "Track category:", this.width / 2 - 190, 235 + yPos, 1);
        }*/
        StringDraw.drawOutlinedString(
                this.graphics,
                trainingOutlineColour,
                this.gameContainer.textManager.getText("LobbyReal_TrackCount"),
                this.width / 2 - 190,
                205 + yPos,
                1);
        StringDraw.drawOutlinedString(
                this.graphics,
                trainingOutlineColour,
                this.gameContainer.textManager.getText("LobbyReal_TrackTypes"),
                this.width / 2 - 190,
                (/*isUsingCustomServer ? 265 :*/ 235) + yPos,
                1);
        StringDraw.drawOutlinedString(
                this.graphics,
                trainingOutlineColour,
                this.gameContainer.textManager.getText("LobbyReal_WaterEvent"),
                this.width / 2 - 190,
                (/*isUsingCustomServer ? 295 :*/ 265) + yPos,
                1);
        if (this.choicerWaterEvent.getSelectedIndex() == 1) {
            this.graphics.setColor(GolfGameFrame.colourTextRed);
            StringDraw.drawOutlinedString(
                    this.graphics,
                    trainingOutlineColour,
                    this.gameContainer.textManager.getText("LobbyReal_WaterEventWarning"),
                    this.width / 2 - 190 + 20,
                    (/*isUsingCustomServer ? 325 :*/ 295) + yPos,
                    0);
            this.graphics.setColor(GolfGameFrame.colourTextBlack);
        }

        if (this.selectedTrackSet != null) {
            this.graphics.setFont(GolfGameFrame.fontDialog11);
            TrackSet trackSet = this.selectedTrackSet;
            List<String> trackData = List.of(
                    trackSet.getAllTimeBestPlayer(),
                    String.valueOf(trackSet.getAllTimeBestStrokes()),
                    trackSet.getMonthBestPlayer(),
                    String.valueOf(trackSet.getMonthBestStrokes()),
                    trackSet.getWeekBestPlayer(),
                    String.valueOf(trackSet.getWeekBestStrokes()),
                    trackSet.getDayBestPlayer(),
                    String.valueOf(trackSet.getDayBestStrokes()));
            String[] trackInfoTitles = new String[] {
                "LobbyReal_TS_AllTimeBest", "LobbyReal_TS_MonthBest", "LobbyReal_TS_WeekBest", "LobbyReal_TS_DayBest"
            };
            int[] trackInfoYPos = new int[] {370, 385, 400, 415};

            for (int index = 0; index < 4; ++index) {
                String strokes = trackData.get(index * 2);
                String recordHolder = trackData.get(index * 2 + 1);
                StringDraw.drawOutlinedString(
                        this.graphics,
                        championshipOutlineColour,
                        this.gameContainer.textManager.getText(trackInfoTitles[index]),
                        this.width - 240,
                        trackInfoYPos[index] + yPos,
                        1);
                if (!recordHolder.equals("0")) {
                    StringDraw.drawOutlinedString(
                            this.graphics,
                            championshipOutlineColour,
                            recordHolder + " (" + strokes + ")",
                            this.width - 235,
                            trackInfoYPos[index] + yPos,
                            -1);
                } else {
                    StringDraw.drawOutlinedString(
                            this.graphics,
                            championshipOutlineColour,
                            "-",
                            this.width - 235,
                            trackInfoYPos[index] + yPos,
                            -1);
                }
            }
        }

        g.drawImage(this.image, 0, 0, this);
    }

    public void itemStateChanged(ItemEvent evt) {
        if (evt.getSource() == this.trackSetList) {
            TrackSet selectedTrackSet = this.getSelectedTrackSet();
            if (selectedTrackSet != null) {
                this.selectedTrackSet = selectedTrackSet;
            }
        }

        this.repaint();
    }

    public void actionPerformed(ActionEvent evt) {
        if (this.gameContainer.golfGameFrame.syncIsValidSite.get()) {
            Object eventSource = evt.getSource();
            if (eventSource == this.buttonStartTraining) {
                this.gameContainer.golfGameFrame.setGameState(0);
                this.gameContainer.lobbyPanel.writeData(
                        "cspt\t"
                                + (this.choicerTracksNum.getSelectedIndex() + 1)
                                + "\t"
                                + this.choicerTrackTypes.getSelectedIndex()
                                + "\t"
                                + this.choicerWaterEvent
                                        .getSelectedIndex() /*+ (isUsingCustomServer ? ("\t" + this.choicerTrackCategory.getSelectedIndex()) : "")*/);
            } else {
                if (eventSource == this.buttonStartChampionship) {
                    TrackSet trackSet = this.getSelectedTrackSet();
                    if (trackSet != null) {
                        startChampionship(trackSet.getId());
                    }
                }
            }
        }
    }

    protected boolean handlePacket(String[] args) {
        if (args[1].equals("tracksetlist")) {
            this.trackSetList.removeAllItems();
            int numTrackSets = (args.length - 2) / 11;
            int itemColor = -1;

            for (int i = 0; i < numTrackSets; i++) {
                String[] trackSetData = Arrays.copyOfRange(args, 2 + i * 11, 2 + (i + 1) * 11);
                TrackSet trackSet = TrackSet.deserialize(i, trackSetData);
                String[] columns = new String[] {
                    isUsingCustomServer
                            ? trackSet.getName()
                            : this.gameContainer.textManager.getText("LobbyReal_TS_" + trackSet.getName()),
                    this.gameContainer.textManager.getText(
                            "LobbyReal_TS_Level" + trackSet.getDifficulty().getId()),
                    String.valueOf(trackSet.getTracks().size())
                };
                itemColor = this.getTrackSetDifficultyColor(trackSet.getDifficulty());
                boolean isSelected = i == numTrackSets - 1;
                if (isSelected) {
                    this.selectedTrackSet = trackSet;
                }

                MultiColumnListItem<TrackSet> item =
                        new MultiColumnListItem<>(itemColor, false, columns, trackSet, isSelected);
                this.trackSetList.addItem(item);
            }

            this.requestTrackSetList = false;
            this.repaint();
            return true;
        } else {
            return false;
        }
    }

    protected void setRequestTrackSetList() {
        this.requestTrackSetList = true;
    }

    protected void requestTrackSetList() {
        if (this.requestTrackSetList) {
            this.gameContainer.lobbyPanel.writeData("tracksetlist");
        }
    }

    private void create() {
        this.setLayout(null);
        /*if(isUsingCustomServer) {
            this.choicerTrackCategory = this.gameContainer.lobbyPanel.addChoicerTrackCategory(this, this.width / 2 - 170, 175, 150, 20);
        }*/
        this.choicerTracksNum =
                this.gameContainer.lobbyPanel.addChoicerNumTracks(this, this.width / 2 - 170, 145, 50, 20);
        this.choicerTrackTypes = this.gameContainer.lobbyPanel.addChoicerTrackTypes(
                this, this.width / 2 - 170, /*isUsingCustomServer ? 205 :*/ 175, 150, 20);
        this.choicerWaterEvent = this.gameContainer.lobbyPanel.addChoicerWaterEvent(
                this, this.width / 2 - 170, /*isUsingCustomServer ? 235 :*/ 205, 150, 20);
        this.choicerWaterEvent.addItemListener(this);
        this.buttonStartTraining = new Button(this.gameContainer.textManager.getText("LobbyReal_Start"));
        this.buttonStartTraining.setBackground(GolfGameFrame.colourButtonGreen);
        this.buttonStartTraining.setBounds(this.width / 2 - 170, /*isUsingCustomServer ? 300 :*/ 270, 100, 25);
        this.buttonStartTraining.addActionListener(this);
        this.add(this.buttonStartTraining);
        String[] trackSetListTitles = new String[] {
            this.gameContainer.textManager.getText("LobbyReal_TS_TitleName"),
            this.gameContainer.textManager.getText("LobbyReal_TS_TitleDifficulty"),
            this.gameContainer.textManager.getText("LobbyReal_TS_TitleTracks")
        };
        SortOrder[] columnSortTypes =
                new SortOrder[] {SortOrder.ORDER_ABC, SortOrder.ORDER_ABC, SortOrder.ORDER_123_ALL};
        this.trackSetList = new MultiColumnSelectableList<>(trackSetListTitles, columnSortTypes, 1, 250, 130);
        this.trackSetList.setLocation(this.width - 290, 130);
        this.trackSetList.setBackgroundImage(
                this.gameContainer.imageManager.getImage("bg-lobby-single-fade"), this.width - 290, 130);
        this.trackSetList.setSelectable(MultiColumnSelectableList.SELECTABLE_ONE);
        this.trackSetList.addItemListener(this);
        trackSetList.setListListener(this);
        this.add(this.trackSetList);
        this.buttonStartChampionship = new Button(this.gameContainer.textManager.getText("LobbyReal_Start"));
        this.buttonStartChampionship.setBackground(GolfGameFrame.colourButtonGreen);
        this.buttonStartChampionship.setBounds(this.width - 290 + 75 - 20, 270, 100, 25);
        this.buttonStartChampionship.addActionListener(this);
        this.add(this.buttonStartChampionship);
    }

    private TrackSet getSelectedTrackSet() {
        MultiColumnListItem<TrackSet> item = this.trackSetList.getSelectedItem();
        return item == null ? null : item.getData();
    }

    @Override
    public void mouseDoubleClicked(MultiColumnListItem<TrackSet> clickedItem) {
        TrackSet itemData = clickedItem.getData();
        if (itemData != null) {
            startChampionship(itemData.getId());
        }
    }

    private void startChampionship(int index) {
        this.gameContainer.golfGameFrame.setGameState(0);
        this.gameContainer.lobbyPanel.writeData("cspc\t" + index);
    }

    private int getTrackSetDifficultyColor(TrackSetDifficulty difficulty) {
        return switch (difficulty) {
            case EASY -> MultiColumnListItem.COLOR_GREEN;
            case MEDIUM -> MultiColumnListItem.COLOR_YELLOW;
            case HARD -> MultiColumnListItem.COLOR_RED;
        };
    }
}
