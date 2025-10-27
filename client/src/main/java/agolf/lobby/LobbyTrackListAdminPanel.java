package agolf.lobby;

import agolf.GameContainer;
import agolf.GolfGameFrame;
import com.aapeli.client.StringDraw;
import com.aapeli.colorgui.Button;
import com.aapeli.colorgui.MultiColumnListColumn;
import com.aapeli.colorgui.MultiColumnListItem;
import com.aapeli.colorgui.MultiColumnSelectableList;
import com.aapeli.colorgui.SortOrder;
import java.awt.Checkbox;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

class LobbyTrackListAdminPanel extends Panel implements ActionListener, ItemListener {

    private static boolean refreshTrackList = true;
    private GameContainer gameContainer;
    private int width;
    private int height;
    private MultiColumnSelectableList<String> trackList;
    private Button buttonRefresh;
    private Button buttonUnselect;
    private Button buttonPlay;
    private Checkbox[] checkboxTracks;
    private Checkbox checkboxRepeat;
    private Checkbox checkboxRandom;
    private Checkbox checkboxSafeMode;
    private Button buttonQuit;

    protected LobbyTrackListAdminPanel(GameContainer gameContainer, int width, int height, boolean enableSafeMode) {
        this.gameContainer = gameContainer;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.create(enableSafeMode);
    }

    public void addNotify() {
        super.addNotify();
        this.repaint();
    }

    public void paint(Graphics var1) {
        this.update(var1);
    }

    public void update(Graphics var1) {
        if (this.trackList != null) {
            var1.setColor(GolfGameFrame.colourGameBackground);
            var1.fillRect(0, 0, this.width, this.height);
            var1.setFont(GolfGameFrame.fontDialog12);
            var1.setColor(GolfGameFrame.colourTextBlack);
            StringDraw.drawString(
                    var1, "Tracks: " + this.trackList.getItemCount(), this.width / 2 - 100 - 10 + 60, 405, 0);
            StringDraw.drawString(
                    var1, "Selected: " + this.trackList.getSelectedItemCount(), this.width / 2 - 100 - 10 + 60, 425, 0);
        }
    }

    public void actionPerformed(ActionEvent var1) {
        Object var2 = var1.getSource();
        if (var2 == this.buttonRefresh) {
            this.refreshTrackList();
        } else if (var2 == this.buttonUnselect) {
            this.trackList.unselectAll();
            this.repaint();
        } else if (var2 != this.buttonPlay) {
            if (var2 == this.buttonQuit) {
                this.gameContainer.lobbyPanel.quitLobby();
            }

        } else {
            MultiColumnListItem<String>[] selectedItems = this.trackList.getSelectedItems();
            if (selectedItems != null) {
                int selectedItemsLen = selectedItems.length;
                if (selectedItemsLen >= 1) {
                    String[] tracksData = new String[selectedItemsLen];

                    for (int index = 0; index < selectedItemsLen; ++index) {
                        tracksData[index] = selectedItems[index].getData();
                    }

                    this.gameContainer.safeMode = this.checkboxSafeMode.getState();
                    if (this.checkboxRandom.getState()) {
                        tracksData = this.randomize(tracksData);
                    }

                    if (this.checkboxRepeat.getState()) {
                        tracksData = this.repeat(tracksData);
                        selectedItemsLen = tracksData.length;
                    }

                    if (selectedItemsLen > 20) {
                        selectedItemsLen = 20;
                    }

                    String data = "ctt\t" + selectedItemsLen;

                    for (int index = 0; index < selectedItemsLen; ++index) {
                        data = data + "\t" + tracksData[index];
                    }

                    this.gameContainer.lobbyPanel.writeData(data);
                }
            }
        }
    }

    public void itemStateChanged(ItemEvent var1) {
        this.repaint();
    }

    protected boolean handlePacket(String[] args) {
        int len;
        String[][] trackList;
        int var4;
        if (args[1].equals("tracklistadmin")) {
            len = args.length - 2;
            trackList = new String[len / 4][4];

            for (var4 = 0; var4 < len; ++var4) {
                trackList[var4 / 4][var4 % 4] = args[var4 + 2];
            }

            this.createTrackList(trackList, true);
            this.repaint();
            return true;
        } else if (args[1].equals("tracklist")) {
            len = args.length - 2;
            trackList = new String[len / 2][2];

            for (var4 = 0; var4 < len; ++var4) {
                trackList[var4 / 2][var4 % 2] = args[var4 + 2];
            }

            this.createTrackList(trackList, false);
            this.repaint();
            return true;
        }
        return false;
    }

    protected void setRefreshTrackList() {
        if (refreshTrackList) {
            this.refreshTrackList();
            refreshTrackList = false;
        }
    }

    private void create(boolean enableSafeMode) {
        this.buttonRefresh = new Button("Refresh list");
        this.buttonRefresh.setBounds(this.width / 2 - 125 - 100 - 40, 355, 120, 25);
        this.buttonRefresh.setBackground(GolfGameFrame.colourButtonBlue);
        this.buttonRefresh.addActionListener(this);
        this.add(this.buttonRefresh);
        String[] checkboxTitles = new String[] {"Private", "Pending", "Public", "Trackset"};
        boolean[] checkboxValues =
                new boolean[] {!enableSafeMode, enableSafeMode ? true : true, !enableSafeMode, !enableSafeMode};
        this.checkboxTracks = new Checkbox[4];

        for (int index = 0; index < 4; ++index) {
            this.checkboxTracks[index] = new Checkbox(checkboxTitles[index], checkboxValues[index]);
            this.checkboxTracks[index].setBounds(this.width / 2 - 125 - 100 - 40 + 10, 385 + index * 20, 100, 20);
            this.checkboxTracks[index].setBackground(GolfGameFrame.colourGameBackground);
            this.checkboxTracks[index].setForeground(GolfGameFrame.colourTextBlack);
            this.add(this.checkboxTracks[index]);
        }

        this.buttonUnselect = new Button("Unselect all");
        this.buttonUnselect.setBounds(this.width / 2 - 100 - 10, 355, 120, 25);
        this.buttonUnselect.setBackground(GolfGameFrame.colourButtonYellow);
        this.buttonUnselect.addActionListener(this);
        this.add(this.buttonUnselect);
        this.buttonPlay = new Button("Play track(s)");
        this.buttonPlay.setBounds(this.width / 2 + 125 - 100 + 20, 355, 120, 25);
        this.buttonPlay.setBackground(GolfGameFrame.colourButtonGreen);
        this.buttonPlay.addActionListener(this);
        this.add(this.buttonPlay);
        this.checkboxRepeat = new Checkbox("Repeat track(s)", true);
        this.checkboxRepeat.setBounds(this.width / 2 + 125 - 100 + 20 + 10, 385, 100, 20);
        this.checkboxRepeat.setBackground(GolfGameFrame.colourGameBackground);
        this.checkboxRepeat.setForeground(GolfGameFrame.colourTextBlack);
        this.add(this.checkboxRepeat);
        this.checkboxRandom = new Checkbox("Random order", false);
        this.checkboxRandom.setBounds(this.width / 2 + 125 - 100 + 20 + 10, 405, 100, 20);
        this.checkboxRandom.setBackground(GolfGameFrame.colourGameBackground);
        this.checkboxRandom.setForeground(GolfGameFrame.colourTextBlack);
        this.add(this.checkboxRandom);
        this.checkboxSafeMode = new Checkbox("Safe mode", true);
        if (enableSafeMode) {
            this.checkboxSafeMode.setBounds(this.width / 2 + 125 - 100 + 20 + 10, 425, 100, 20);
            this.checkboxSafeMode.setBackground(GolfGameFrame.colourGameBackground);
            this.checkboxSafeMode.setForeground(GolfGameFrame.colourTextBlack);
            this.add(this.checkboxSafeMode);
        }

        this.buttonQuit = new Button("Quit");
        this.buttonQuit.setBackground(GolfGameFrame.colourButtonRed);
        this.buttonQuit.setBounds(this.width - 100, this.height - 20, 100, 20);
        this.buttonQuit.addActionListener(this);
        this.add(this.buttonQuit);
    }

    private void refreshTrackList() {
        String params = "";

        for (int index = 0; index < 4; ++index) {
            params = params + (this.checkboxTracks[index].getState() ? 't' : 'f');
        }

        if (!params.equals("ffff")) {
            /*handlePacket(new String[]{
                    "", "tracklistadmin",
                    "0", "fat cookies", "pudding", "10",
                    "1", "exemplar", "trout sandwich", "5",
                    "2", "someone else", "public map", "0",
                    "3", "yeeep", "yuuuup", "8"
            });*/
            this.gameContainer.lobbyPanel.writeData("tracklist\t" + params);
        }
    }

    private void createTrackList(String[][] tracksInfo, boolean isAdmin) {
        if (this.trackList == null) {
            this.setVisible(false);
            if (isAdmin) {
                List<MultiColumnListColumn> columns = List.of(
                        new MultiColumnListColumn("Status", SortOrder.ORDER_ABC),
                        new MultiColumnListColumn("Author", SortOrder.ORDER_ABC),
                        new MultiColumnListColumn("Track", SortOrder.ORDER_ABC),
                        new MultiColumnListColumn("Rating", SortOrder.ORDER_321_ALL));
                this.trackList = new MultiColumnSelectableList<>(columns, 2, 450, 250);
            } else {
                List<MultiColumnListColumn> columns = List.of(
                        new MultiColumnListColumn("Status", SortOrder.ORDER_ABC),
                        new MultiColumnListColumn("Track", SortOrder.ORDER_ABC));
                this.trackList = new MultiColumnSelectableList<>(columns, 1, 450, 250);
            }

            this.trackList.setLocation(this.width / 2 - 125 - 100 - 50, 100);
            this.trackList.setSelectable(2);
            this.trackList.addItemListener(this);
            this.add(this.trackList);
            this.setVisible(true);
        }

        this.trackList.removeAllItems();
        int tracksInfoLen = tracksInfo.length;
        byte colour = -1;

        for (int index = 0; index < tracksInfoLen; ++index) {
            int trackStatus = Integer.parseInt(tracksInfo[index][0]);
            if (trackStatus == 0) {
                colour = MultiColumnListItem.COLOR_RED;
                tracksInfo[index][0] = "Private";
            }

            if (trackStatus == 1) {
                colour = MultiColumnListItem.COLOR_YELLOW;
                tracksInfo[index][0] = "Pending";
            }

            if (trackStatus == 2) {
                colour = MultiColumnListItem.COLOR_GREEN;
                tracksInfo[index][0] = "Public";
            }

            if (trackStatus == 3) {
                colour = MultiColumnListItem.COLOR_BLUE;
                tracksInfo[index][0] = "TrackSet";
            }

            // Status", "Author", "Track", "Rating

            MultiColumnListItem listItem;
            if (isAdmin) {
                listItem = new MultiColumnListItem(
                        colour, tracksInfo[index], tracksInfo[index][1] + ":" + tracksInfo[index][2]);
            } else {
                listItem = new MultiColumnListItem(colour, tracksInfo[index], tracksInfo[index][1]);
            }

            this.trackList.addItem(listItem);
        }
    }

    private String[] randomize(String[] array) {
        int arrayLen = array.length;
        String[] randomizedArray = new String[arrayLen];

        for (int index = 0; index < arrayLen; ++index) {
            int randomIndex;
            do {
                randomIndex = (int) (Math.random() * (double) arrayLen);
            } while (array[randomIndex] == null);

            randomizedArray[index] = array[randomIndex];
            array[randomIndex] = null;
        }

        return randomizedArray;
    }

    private String[] repeat(String[] array) {
        int arrayLen = array.length;
        if (arrayLen == 20) {
            return array;
        } else {
            String[] repeatedArray = new String[20];
            int index = 0;

            for (int repeatIndex = 0; repeatIndex < 20; ++repeatIndex) {
                repeatedArray[repeatIndex] = array[index];
                ++index;
                if (index == arrayLen) {
                    index = 0;
                }
            }

            return repeatedArray;
        }
    }
}
