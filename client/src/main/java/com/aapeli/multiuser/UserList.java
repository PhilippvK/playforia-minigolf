package com.aapeli.multiuser;

import com.aapeli.client.IPanel;
import com.aapeli.client.ImageManager;
import com.aapeli.client.TextManager;
import com.aapeli.colorgui.Checkbox;
import com.aapeli.colorgui.ColorList;
import com.aapeli.colorgui.ColorListItem;
import com.aapeli.colorgui.ColorListItemGroup;
import com.aapeli.colorgui.TextArea;
import com.aapeli.tools.Tools;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.SwingUtilities;
import org.moparforia.shared.Language;

public class UserList extends IPanel implements ComponentListener, ItemListener, ActionListener {

    public static final int SORT_NICKNAME_ABC = 1;
    public static final int SORT_NICKNAME_CBA = 2;
    public static final int SORT_RANKING_123 = 3;
    public static final int SORT_RANKING_321 = 4;
    private static final Color backgroundColor = Color.white;
    private static final Color foregroundColor = Color.black;
    private static final Font sortingButtonsFont = new Font("Dialog", Font.PLAIN, 9);
    private static final Color columnHeaderDefaultColor = new Color(224, 224, 224);
    private static final Color columnHeaderSortedColor = new Color(208, 208, 255);
    private UserListHandler userListHandler;
    private TextManager textManager;
    private ImageManager imageManager;
    private int width;
    private int height;
    private Image[] rankingIcons;
    private boolean rankingsShown;
    private ColorList playersList;
    private Checkbox sendPrivatelyCheckbox;
    private Checkbox ignoreUserCheckbox;
    private RoundedUpperCornersButton sortByRankingButton;
    private RoundedUpperCornersButton sortByNicknameButton;
    private Image backgroundImage;
    private Image playersListBackgroundImage;
    private int backgroundImageOffsetX;
    private int backgroundImageOffsetY;
    private int lastX;
    private int lastY;
    private boolean rightClickMenuEnabled;
    private int sheriffStatus;
    private int adminStatus;
    private PopupMenu rightClickMenu;
    private MenuItem openProfileMenuItem;
    private MenuItem sendPrivatelyMenuItem;
    private MenuItem ignoreUserMenuItem;
    private MenuItem adminRemoveUserMenuItem;
    private MenuItem sheriffSendMessageMenuItem;
    private MenuItem sheriffMute5minutesMenuItem;
    private MenuItem sheriffMute15minutesMenuItem;
    private MenuItem sheriffMute1hourMenuItem;
    private MenuItem sheriffMute6hoursMenuItem;
    private MenuItem sheriffMute1dayMenuItem;
    private MenuItem clearEveryUserChatMenuItem;
    private MenuItem sheriffCopyChatMenuItem;
    private MenuItem adminGetUserInfoMenuItem;
    private MenuItem adminUnmuteUserMenuItem;
    private MenuItem adminBroadcastMessageMenuItem;
    private User selectedUser;
    private StaffActionFrame staffActionFrame;
    private List<String> privateMessageUsers;
    private List<String> ignoredUsers;
    private boolean sheriffMarkEnabled;
    private boolean dimmerNicksEnabled;
    private TextArea chatOutput;
    private ChatBase chat;
    private Languages languages;
    private Hashtable<Language, ColorListItemGroup> languageGroups;

    public UserList(
            UserListHandler handler,
            TextManager textManager,
            ImageManager imageManager,
            boolean showRankingIcons,
            boolean addSendPrivately,
            boolean addIgnoreUser) {
        this.userListHandler = handler;
        this.textManager = textManager;
        this.imageManager = imageManager;
        this.width = 100;
        this.height = 200;
        this.setSize(100, 200);
        this.rankingsShown = showRankingIcons;
        this.init(addSendPrivately, addIgnoreUser);
        this.setBackground(backgroundColor);
        this.setForeground(foregroundColor);
        if (showRankingIcons) {
            Image rankingIcons = imageManager.getImage("ranking-icons");
            this.rankingIcons = imageManager.separateImages(rankingIcons, 14);
        }

        this.rightClickMenuEnabled = false;
        this.sheriffStatus = 0;
        this.adminStatus = 0;
        this.privateMessageUsers = new ArrayList<>();
        this.ignoredUsers = new ArrayList<>();
        this.sheriffMarkEnabled = true;
        this.dimmerNicksEnabled = true;
        this.languages = new Languages(imageManager);
        this.languageGroups = new Hashtable<>();
        this.addComponentListener(this);
    }

    public void addNotify() {
        super.addNotify();
        this.repaint();
    }

    public void paint(Graphics g) {
        this.update(g);
    }

    public void update(Graphics g) {
        if (this.backgroundImage != null) {
            g.drawImage(
                    this.backgroundImage,
                    0,
                    0,
                    this.width,
                    this.height,
                    this.backgroundImageOffsetX,
                    this.backgroundImageOffsetY,
                    this.backgroundImageOffsetX + this.width,
                    this.backgroundImageOffsetY + this.height,
                    this);
        } else {
            this.drawBackground(g);
        }
    }

    public void componentShown(ComponentEvent e) {}

    public void componentHidden(ComponentEvent e) {}

    public void componentMoved(ComponentEvent e) {
        if (this.backgroundImage != null) {
            Point location = this.getLocation();
            int newX = location.x - this.lastX;
            int newY = location.y - this.lastY;
            this.setBackgroundImage(
                    this.backgroundImage,
                    this.playersListBackgroundImage,
                    this.backgroundImageOffsetX + newX,
                    this.backgroundImageOffsetY + newY);
        }

        this.repaint();
    }

    public void componentResized(ComponentEvent e) {
        Dimension size = this.getSize();
        this.width = size.width;
        this.height = size.height;
        boolean sendPrivatelyCheckBoxExists = this.sendPrivatelyCheckbox != null;
        boolean ignoreUserCheckBoxExists = this.ignoreUserCheckbox != null;
        if (this.rankingsShown) {
            this.sortByRankingButton.setSize(17, 11);
            this.sortByNicknameButton.setSize(this.width - 17, 11);
        }

        int width = this.width;
        int height = this.height
                - (ignoreUserCheckBoxExists ? 18 : 0)
                - (sendPrivatelyCheckBoxExists ? 18 : 0)
                - (!ignoreUserCheckBoxExists && !sendPrivatelyCheckBoxExists ? 0 : 2)
                - (this.rankingsShown ? 11 : 0);
        this.playersList.setBounds(0, this.rankingsShown ? 11 : 0, width, height);
        if (sendPrivatelyCheckBoxExists) {
            this.sendPrivatelyCheckbox.setBounds(
                    0, this.height - 18 - (ignoreUserCheckBoxExists ? 18 : 0), this.width, 18);
        }

        if (ignoreUserCheckBoxExists) {
            this.ignoreUserCheckbox.setBounds(0, this.height - 18, this.width, 18);
        }

        this.componentMoved(e);
    }

    public void itemStateChanged(ItemEvent e) {
        Object source = e.getSource();
        if (source == this.sendPrivatelyMenuItem) {
            this.sendPrivatelyCheckbox.click();
        } else if (source == this.ignoreUserMenuItem) {
            this.ignoreUserCheckbox.click();
        } else {
            ColorListItem selectedPlayer = this.playersList.getSelectedItem();
            boolean playerDeselected = false;
            if (selectedPlayer == null) {
                this.resetCheckBoxes();
                playerDeselected = true;
                Object item = e.getItem();
                if (!(item instanceof ColorListItem)) {
                    return;
                }

                selectedPlayer = (ColorListItem) e.getItem();
            }

            User user = (User) selectedPlayer.getData();
            if (source == this.playersList) {
                int eventId = e.getID();
                if (eventId == ColorList.ID_DOUBLECLICKED) {
                    if (this.openProfilePage(user)) {
                        return;
                    }

                    this.userListHandler.openPlayerCard(user.getNick());
                } else if (eventId == ColorList.ID_RIGHTCLICKED && this.rightClickMenuEnabled) {
                    int[] mouseCoordinates = this.playersList.getLastClickedMouseXY();
                    this.showRightClickMenu(user, mouseCoordinates[0], mouseCoordinates[1]);
                }
            }

            if (!playerDeselected) {
                if (source == this.playersList) {
                    if (this.sendPrivatelyCheckbox != null) {
                        this.sendPrivatelyCheckbox.setState(user.isGettingPrivateMessages());
                    }

                    if (this.ignoreUserCheckbox != null) {
                        this.ignoreUserCheckbox.setState(user.isIgnore());
                    }

                } else {
                    if (source == this.sendPrivatelyCheckbox || source == this.ignoreUserCheckbox) {
                        if (user.isLocal()) {
                            this.resetCheckBoxes();
                        } else {
                            user.setGettingPrivateMessages(
                                    this.sendPrivatelyCheckbox != null ? this.sendPrivatelyCheckbox.getState() : false);
                            user.setIgnore(
                                    this.ignoreUserCheckbox != null ? this.ignoreUserCheckbox.getState() : false);
                            selectedPlayer.setColor(this.getUserColor(user));
                            this.playersList.repaint();
                        }
                    }
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == this.sortByRankingButton) {
            if (this.getSorting() == SORT_RANKING_321) {
                this.setSorting(SORT_RANKING_123);
            } else {
                this.setSorting(SORT_RANKING_321);
            }
        } else if (source == this.sortByNicknameButton) {
            if (this.getSorting() == SORT_NICKNAME_ABC) {
                this.setSorting(SORT_NICKNAME_CBA);
            } else {
                this.setSorting(SORT_NICKNAME_ABC);
            }
        } else if (source == this.openProfileMenuItem) {
            if (!this.openProfilePage(this.selectedUser)) {
                this.userListHandler.openPlayerCard(this.selectedUser.getNick());
            }
        } else if (source == this.adminRemoveUserMenuItem) {
            this.showStaffActionFrame(1, this.selectedUser.getNick());
        } else if (source == this.sheriffSendMessageMenuItem) {
            this.showStaffActionFrame(2, this.selectedUser.getNick());
        } else if (source != this.sheriffMute5minutesMenuItem
                && source != this.sheriffMute15minutesMenuItem
                && source != this.sheriffMute1hourMenuItem
                && source != this.sheriffMute6hoursMenuItem
                && source != this.sheriffMute1dayMenuItem) {
            if (source == this.clearEveryUserChatMenuItem) {
                this.showStaffActionFrame(4, null);
            } else if (source == this.sheriffCopyChatMenuItem) {
                CopyChatFrame copyChatFrame = new CopyChatFrame();
                copyChatFrame.create(
                        SwingUtilities.getWindowAncestor(this),
                        this.chat != null ? this.chat.chatTextArea : this.chatOutput);
            } else if (source == this.adminGetUserInfoMenuItem) {
                this.userListHandler.adminCommand("info", this.selectedUser.getNick());
            } else if (source == this.adminUnmuteUserMenuItem) {
                this.userListHandler.adminCommand("unmute", this.selectedUser.getNick());
            } else if (source == this.adminBroadcastMessageMenuItem) {
                this.showStaffActionFrame(5, null);
            }
        } else {
            short muteTime = 0;
            if (source == this.sheriffMute5minutesMenuItem) {
                muteTime = 5;
            } else if (source == this.sheriffMute15minutesMenuItem) {
                muteTime = 15;
            } else if (source == this.sheriffMute1hourMenuItem) {
                muteTime = 60;
            } else if (source == this.sheriffMute6hoursMenuItem) {
                muteTime = 360;
            } else if (source == this.sheriffMute1dayMenuItem) {
                muteTime = 1440;
            }

            this.userListHandler.adminCommand("mute", this.selectedUser.getNick(), "" + muteTime);
        }
    }

    public static String getNickFromUserInfo(String userData) {
        if (!isUserDataType3(userData)) {
            return getNickFromUserData(userData);
        } else {
            int i = userData.indexOf(':');
            int j = userData.indexOf('^');
            return Tools.changeFromSaveable(userData.substring(i + 1, j));
        }
    }

    public void disableSheriffMark() {
        this.sheriffMarkEnabled = false;
    }

    public void disableDimmerNicks() {
        this.dimmerNicksEnabled = false;
    }

    public void enableRightClickMenu() {
        this.enableRightClickMenu(false, false);
    }

    public void enableRightClickMenu(boolean isSheriff, boolean isAdmin) {
        this.sheriffStatus = isSheriff ? 2 : 0;
        this.adminStatus = isAdmin ? 1 : 0;
        this.rightClickMenuEnabled = true;
    }

    public void enablePopUpWithOnlyOldCommands(boolean isSheriff, boolean isAdmin) {
        this.sheriffStatus = isSheriff ? 1 : 0;
        this.adminStatus = isAdmin ? 1 : 0;
        this.rightClickMenuEnabled = true;
    }

    public void setCheckBoxesVisible(boolean checkBoxesVisible) {
        if (this.sendPrivatelyCheckbox != null) {
            this.sendPrivatelyCheckbox.setVisible(checkBoxesVisible);
        }

        if (this.ignoreUserCheckbox != null) {
            this.ignoreUserCheckbox.setVisible(checkBoxesVisible);
        }
    }

    public void setBackground(Color color) {
        super.setBackground(color);
        if (this.sendPrivatelyCheckbox != null) {
            this.sendPrivatelyCheckbox.setBackground(color);
        }

        if (this.ignoreUserCheckbox != null) {
            this.ignoreUserCheckbox.setBackground(color);
        }

        this.repaint();
    }

    public void setForeground(Color color) {
        super.setForeground(color);
        if (this.sendPrivatelyCheckbox != null) {
            this.sendPrivatelyCheckbox.setForeground(color);
        }

        if (this.ignoreUserCheckbox != null) {
            this.ignoreUserCheckbox.setForeground(color);
        }
    }

    public void setBackgroundImage(Image image, int backgroundImageOffsetX, int backgroundImageOffsetY) {
        this.setBackgroundImage(image, null, backgroundImageOffsetX, backgroundImageOffsetY);
    }

    public void setBackgroundImage(
            Image image, Image playersListBackground, int backgroundImageOffsetX, int backgroundImageOffsetY) {
        this.backgroundImage = image;
        this.playersListBackgroundImage = playersListBackground;
        this.backgroundImageOffsetX = backgroundImageOffsetX;
        this.backgroundImageOffsetY = backgroundImageOffsetY;
        Point currentLocation = this.getLocation();
        this.lastX = currentLocation.x;
        this.lastY = currentLocation.y;
        Point location;
        if (playersListBackground != null) {
            location = this.playersList.getLocation();
            this.playersList.setBackgroundImage(
                    playersListBackground, backgroundImageOffsetX + location.x, backgroundImageOffsetY + location.y);
        }

        if (this.sendPrivatelyCheckbox != null) {
            location = this.sendPrivatelyCheckbox.getLocation();
            this.sendPrivatelyCheckbox.setBackgroundImage(
                    image, backgroundImageOffsetX + location.x, backgroundImageOffsetY + location.y);
        }

        if (this.ignoreUserCheckbox != null) {
            location = this.ignoreUserCheckbox.getLocation();
            this.ignoreUserCheckbox.setBackgroundImage(
                    image, backgroundImageOffsetX + location.x, backgroundImageOffsetY + location.y);
        }

        this.repaint();
    }

    public void setListBackgroundImage(Image image, int backgroundImageOffsetX, int backgroundImageOffsetY) {
        Point location = this.playersList.getLocation();
        this.playersList.setBackgroundImage(
                image, backgroundImageOffsetX + location.x, backgroundImageOffsetY + location.y);
    }

    public User addUser(String userData, boolean isLocal) {
        return this.addUser(userData, isLocal, -1);
    }

    public User addUser(String userData, boolean userIsLocal, int color) {
        if (!isUserDataType3(userData)) {
            return this.addUser2(userData, userIsLocal, color);
        } else {
            // 3:im the man111^r^111^fi^-^-
            int colonIndex = userData.indexOf(':');
            userData = userData.substring(colonIndex + 1); // looks like the number is skipped
            StringTokenizer tokenizer = new StringTokenizer(userData, "^");
            String username = Tools.changeFromSaveable(tokenizer.nextToken());
            String elevation = tokenizer.nextToken();
            int rating = Integer.parseInt(tokenizer.nextToken());
            String languageString = tokenizer.nextToken();
            String profilePage = Tools.changeFromSaveable(tokenizer.nextToken());
            String avatarUrl = Tools.changeFromSaveable(tokenizer.nextToken());
            boolean isRegistered = elevation.indexOf('r') >= 0;
            boolean isVip = elevation.indexOf('v') >= 0;
            boolean isSheriff = elevation.indexOf('s') >= 0;
            boolean isNotAcceptingChallenges = elevation.indexOf('n') >= 0;
            User user = new User(username, userIsLocal, isRegistered, isVip, isSheriff, rating);
            user.setIsNotAcceptingChallenges(isNotAcceptingChallenges);

            Language language;
            if (!languageString.equals("-")) {
                language = Language.fromString(languageString);
            } else {
                language = Language.UNKNOWN;
            }
            user.setLanguage(language);
            user.setLanguageFlag(this.languages.getFlag(language));
            if (color >= 0) {
                user.setOverrideColor(color);
            }

            this.addUser(user);
            if (!profilePage.equals("-")) {
                user.setProfilePage(profilePage);
            }

            if (!avatarUrl.equals("-")) {
                user.loadAvatar(avatarUrl, this.imageManager, this.playersList);
            }

            return user;
        }
    }

    public void addUser(User user) {
        String nick = user.getNick();
        if (this.privateMessageUsers.contains(nick)) {
            user.setGettingPrivateMessages(true);
        }

        if (this.ignoredUsers.contains(nick)) {
            user.setIgnore(true);
        }

        String displayedNick = user.getNick();
        if (user.isSheriff() && this.sheriffMarkEnabled) {
            displayedNick = displayedNick + " " + this.textManager.getText("UserList_Sheriff");
        }

        ColorListItem colorListItem = new ColorListItem(
                this.getRankingIcon(user), this.getUserColor(user), user.isRegistered(), displayedNick, user, false);
        colorListItem.setValue(user.getRating());
        if (user.isSheriff()) {
            colorListItem.setSortOverride(true);
        }

        Language language = user.getLanguage();
        ColorListItemGroup group = this.languageGroups.get(language);
        if (group == null) {
            int sortValue = language.getId();
            if (language == Language.UNKNOWN) {
                sortValue = sortValue + 50;
            }

            String languageName = this.textManager.getText("Language_" + language);
            group = new ColorListItemGroup(languageName, this.languages.getFlag(language), sortValue);
            this.languageGroups.put(language, group);
        }

        if (user.isLocal()) {
            group.changeSortValue(-100);
            this.playersList.reSort();
        }

        colorListItem.setGroup(group);
        this.playersList.addItem(colorListItem);
        user.setColorListItem(colorListItem);
    }

    public User getSelectedUser() {
        ColorListItem item = this.playersList.getSelectedItem();
        return item == null ? null : (User) item.getData();
    }

    public User getUser(String nick) {
        ColorListItem[] allItems = this.playersList.getAllItems();
        if (allItems != null) {
            int itemsCount = allItems.length;
            if (itemsCount > 0) {
                for (ColorListItem colorListItem : allItems) {
                    User user = (User) colorListItem.getData();
                    if (user.getNick().equals(nick)) {
                        return user;
                    }
                }
            }
        }

        return null;
    }

    public boolean isUser(String nick) {
        return this.getUser(nick) != null;
    }

    public User getLocalUser() {
        ColorListItem[] allItems = this.playersList.getAllItems();
        if (allItems != null) {
            int itemsCount = allItems.length;
            if (itemsCount > 0) {
                for (ColorListItem colorListItem : allItems) {
                    User user = (User) colorListItem.getData();
                    if (user.isLocal()) {
                        return user;
                    }
                }
            }
        }

        return null;
    }

    public void removeUser(String nick) {
        ColorListItem[] allItems = this.playersList.getAllItems();
        if (allItems != null) {
            int itemsCount = allItems.length;
            if (itemsCount > 0) {
                for (ColorListItem colorListItem : allItems) {
                    User user = (User) colorListItem.getData();
                    if (user.getNick().equals(nick)) {
                        this.playersList.removeItem(colorListItem);
                        if (colorListItem.isSelected()) {
                            this.resetCheckBoxes();
                        }

                        this.removeUser(user);
                        return;
                    }
                }
            }
        }
    }

    public User removeAndReturnUser(String nick) {
        ColorListItem[] allItems = this.playersList.getAllItems();
        if (allItems != null) {
            int itemsCount = allItems.length;
            if (itemsCount > 0) {
                for (ColorListItem colorListItem : allItems) {
                    User user = (User) colorListItem.getData();
                    if (user.getNick().equals(nick)) {
                        this.playersList.removeItem(colorListItem);
                        if (colorListItem.isSelected()) {
                            this.resetCheckBoxes();
                        }

                        this.removeUser(user);
                        return user;
                    }
                }
            }
        }

        return null;
    }

    public void removeAllUsers() {
        ColorListItem[] users = this.playersList.getAllItems();
        if (users != null) {
            for (ColorListItem colorListItem : users) {
                this.removeUser((User) colorListItem.getData());
            }
        }

        this.playersList.removeAllItems();
        this.resetCheckBoxes();
    }

    public void setNotAcceptingChallenges(User user, boolean notAcceptingChallenges) {
        user.setIsNotAcceptingChallenges(notAcceptingChallenges);
        ColorListItem userListItem = user.getColorListItem();
        userListItem.setColor(this.getUserColor(user));
        this.playersList.repaint();
    }

    public void setSorting(int sorting) {
        this.playersList.setSorting(sorting);
        if (this.rankingsShown) {
            if (sorting != SORT_RANKING_123 && sorting != SORT_RANKING_321) {
                this.sortByRankingButton.setBackground(columnHeaderDefaultColor);
                this.sortByNicknameButton.setBackground(columnHeaderSortedColor);
            } else {
                this.sortByRankingButton.setBackground(columnHeaderSortedColor);
                this.sortByNicknameButton.setBackground(columnHeaderDefaultColor);
            }
        }
    }

    public int getSorting() {
        return this.playersList.getSorting();
    }

    public int getUserCount() {
        return this.playersList.getItemCount();
    }

    public void setChatOutputReference(TextArea chatOutput) {
        this.chatOutput = chatOutput;
    }

    public void setChatReference(ChatBase chat) {
        this.chat = chat;
    }

    private static boolean isUserDataType3(String userData) {
        return userData.startsWith("3:");
    }

    private void showRightClickMenu(User user, int x, int y) {
        this.selectedUser = user;
        if (this.rightClickMenu != null) {
            this.remove(this.rightClickMenu);
        }

        this.rightClickMenu = new PopupMenu();
        this.openProfileMenuItem =
                this.createButtonMenuItem(this.rightClickMenu, this.textManager.getText("UserList_OpenPlayerCard"));
        this.openProfileMenuItem.setEnabled(user.isRegistered() || user.getProfilePage() != null);
        if (this.sendPrivatelyCheckbox != null || this.ignoreUserCheckbox != null) {
            this.rightClickMenu.addSeparator();
        }

        if (this.sendPrivatelyCheckbox != null) {
            this.sendPrivatelyMenuItem = this.createCheckboxMenuItem(
                    this.rightClickMenu, this.sendPrivatelyCheckbox.getLabel(), user.isGettingPrivateMessages());
            this.sendPrivatelyMenuItem.setEnabled(!user.isLocal());
        }

        if (this.ignoreUserCheckbox != null) {
            this.ignoreUserMenuItem = this.createCheckboxMenuItem(
                    this.rightClickMenu, this.ignoreUserCheckbox.getLabel(), user.isIgnore());
            this.ignoreUserMenuItem.setEnabled(!user.isLocal());
        }

        Menu menu;
        if (this.sheriffStatus > 0) {
            this.rightClickMenu.addSeparator();
            menu = new Menu("Sheriff");
            this.sheriffSendMessageMenuItem = this.createButtonMenuItem(menu, "Send message...");
            if (this.sheriffStatus > 1) {
                Menu sheriffMuteUserMenu = new Menu("Mute user");
                this.sheriffMute5minutesMenuItem = this.createButtonMenuItem(sheriffMuteUserMenu, "5 minutes");
                this.sheriffMute15minutesMenuItem = this.createButtonMenuItem(sheriffMuteUserMenu, "15 minutes");
                this.sheriffMute1hourMenuItem = this.createButtonMenuItem(sheriffMuteUserMenu, "1 hour");
                this.sheriffMute6hoursMenuItem = this.createButtonMenuItem(sheriffMuteUserMenu, "6 hours");
                if (this.adminStatus > 0) {
                    this.sheriffMute1dayMenuItem = this.createButtonMenuItem(sheriffMuteUserMenu, "1 day (admin)");
                }

                menu.add(sheriffMuteUserMenu);
                if (this.chat != null || this.chatOutput != null) {
                    this.sheriffCopyChatMenuItem = this.createButtonMenuItem(menu, "Copy chat");
                }
            }

            this.rightClickMenu.add(menu);
        }

        if (this.adminStatus > 0) {
            menu = new Menu("Admin");
            this.adminGetUserInfoMenuItem = this.createButtonMenuItem(menu, "Get user info");
            this.adminUnmuteUserMenuItem = this.createButtonMenuItem(menu, "Unmute user");
            this.adminRemoveUserMenuItem = this.createButtonMenuItem(menu, "Remove user...");
            this.adminBroadcastMessageMenuItem = this.createButtonMenuItem(menu, "Broadcast message...");
            this.rightClickMenu.add(menu);
        }

        this.add(this.rightClickMenu);
        this.rightClickMenu.show(this.playersList, x, y);
    }

    private MenuItem createButtonMenuItem(Menu menu, String label) {
        MenuItem menuItem = new MenuItem(label);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        return menuItem;
    }

    private MenuItem createCheckboxMenuItem(Menu menu, String label, boolean checked) {
        CheckboxMenuItem menuItem = new CheckboxMenuItem(label, checked);
        menuItem.addItemListener(this);
        menu.add(menuItem);
        return menuItem;
    }

    private void showStaffActionFrame(int actionType, String targetNick) {
        if (this.staffActionFrame != null) {
            this.staffActionFrame.windowClosing(null);
        }

        this.staffActionFrame = new StaffActionFrame(this.textManager, this.userListHandler, actionType, targetNick);
        this.staffActionFrame.show(SwingUtilities.getWindowAncestor(this), this.adminStatus > 0);
    }

    private Color getUserColor(User user) {
        int colorIndex = user.getColor(this.sheriffMarkEnabled);
        Color color = ColorListItem.getColorById(colorIndex);
        if (this.dimmerNicksEnabled && user.isNotAcceptingChallenges()) {
            color = new Color((color.getRed() + 896) / 5, (color.getGreen() + 896) / 5, (color.getBlue() + 896) / 5);
        }

        return color;
    }

    private void init(boolean addSendPrivately, boolean addIgnoreUser) {
        this.setLayout(null);
        if (this.rankingsShown) {
            this.sortByRankingButton =
                    new RoundedUpperCornersButton(this.textManager.getText("UserList_SortByRanking"));
            this.sortByRankingButton.setBounds(0, 0, 17, 11);
            this.sortByRankingButton.setFont(sortingButtonsFont);
            this.sortByRankingButton.setBackground(columnHeaderDefaultColor);
            this.sortByRankingButton.addActionListener(this);
            this.add(this.sortByRankingButton);
            this.sortByNicknameButton = new RoundedUpperCornersButton(this.textManager.getText("UserList_SortByNick"));
            this.sortByNicknameButton.setBounds(17, 0, this.width - 17, 11);
            this.sortByNicknameButton.setFont(sortingButtonsFont);
            this.sortByNicknameButton.setBackground(columnHeaderSortedColor);
            this.sortByNicknameButton.addActionListener(this);
            this.add(this.sortByNicknameButton);
        }

        int width = this.width;
        int height = this.height
                - (addIgnoreUser ? 18 : 0)
                - (addSendPrivately ? 18 : 0)
                - (!addIgnoreUser && !addSendPrivately ? 0 : 2)
                - (this.rankingsShown ? 11 : 0);
        if (this.rankingsShown) {
            this.playersList = new ColorList(width, height, 11, 11);
        } else {
            this.playersList = new ColorList(width, height);
        }

        this.playersList.setSelectable(ColorList.SELECTABLE_ONE);
        this.playersList.setLocation(0, this.rankingsShown ? 11 : 0);
        this.playersList.addItemListener(this);
        this.add(this.playersList);
        this.setSorting(UserList.SORT_NICKNAME_ABC);
        if (addSendPrivately) {
            this.sendPrivatelyCheckbox = new Checkbox(this.textManager.getText("UserList_Privately"));
            this.sendPrivatelyCheckbox.setBounds(0, this.height - 18 - (addIgnoreUser ? 18 : 0), this.width, 18);
            this.sendPrivatelyCheckbox.addItemListener(this);
            this.add(this.sendPrivatelyCheckbox);
        }

        if (addIgnoreUser) {
            this.ignoreUserCheckbox = new Checkbox(this.textManager.getText("UserList_Ignore"));
            this.ignoreUserCheckbox.setBounds(0, this.height - 18, this.width, 18);
            this.ignoreUserCheckbox.addItemListener(this);
            this.add(this.ignoreUserCheckbox);
        }
    }

    private void resetCheckBoxes() {
        if (this.sendPrivatelyCheckbox != null) {
            this.sendPrivatelyCheckbox.setState(false);
        }

        if (this.ignoreUserCheckbox != null) {
            this.ignoreUserCheckbox.setState(false);
        }
    }

    private Image getRankingIcon(User user) {
        if (this.rankingIcons == null) {
            return null;
        } else if (!user.isRegistered()) {
            return this.rankingIcons[0];
        } else {
            int ranking = user.getRating();
            if (ranking < 0) {
                return null;
            } else if (ranking == 0) {
                return this.rankingIcons[1];
            } else if (ranking < 50) {
                return this.rankingIcons[2];
            } else {
                for (int i = 100; i <= 1000; i += 100) {
                    if (ranking < i) {
                        return this.rankingIcons[2 + i / 100];
                    }
                }

                return this.rankingIcons[13];
            }
        }
    }

    private synchronized void removeUser(User user) {
        String nick = user.getNick();
        if (user.isGettingPrivateMessages()) {
            this.privateMessageUsers.add(nick);
        } else {
            this.privateMessageUsers.remove(nick);
        }

        if (user.isIgnore()) {
            this.ignoredUsers.add(nick);
        } else {
            this.ignoredUsers.remove(nick);
        }
    }

    private boolean openProfilePage(User user) {
        String profilePage = user.getProfilePage();
        if (profilePage == null) {
            return false;
        } else {
            try {
                Desktop.getDesktop().browse(new URI(profilePage));
            } catch (Exception e) {
            }

            return true;
        }
    }

    private static String getNickFromUserData(String userData) {
        int i;
        int j;
        if (userData.startsWith("2:")) {
            i = userData.lastIndexOf('^');
            j = userData.lastIndexOf('^', i - 1);
            userData = userData.substring(2, j);
        }

        i = userData.indexOf(',');
        j = userData.lastIndexOf(',');
        if (i == j) {
            j = userData.length();
        }

        return userData.substring(i + 1, j);
    }

    private User addUser2(String userData, boolean isLocal, int color) {
        String urls = null;
        int j;
        int i;
        if (userData.startsWith("2:")) {
            j = userData.lastIndexOf('^');
            i = userData.lastIndexOf('^', j - 1);
            urls = userData.substring(i + 1);
            userData = userData.substring(2, i);
        }

        j = userData.indexOf(',');
        i = userData.lastIndexOf(',');
        String nick;
        int rating;
        if (j == i) {
            nick = userData.substring(j + 1);
            rating = -2;
        } else {
            nick = userData.substring(j + 1, i);
            rating = Integer.parseInt(userData.substring(i + 1));
        }

        String userParameters = userData.substring(0, j);
        boolean isRegistered = userParameters.indexOf('r') >= 0;
        boolean isVip = userParameters.indexOf('v') >= 0;
        boolean isSheriff = userParameters.indexOf('s') >= 0;
        boolean notAcceptingChallenges = userParameters.indexOf('n') >= 0;
        User user = new User(nick, isLocal, isRegistered, isVip, isSheriff, rating);
        user.setIsNotAcceptingChallenges(notAcceptingChallenges);
        if (color >= 0) {
            user.setOverrideColor(color);
        }

        this.addUser(user);
        if (urls != null) {
            j = urls.indexOf('^');
            String profilePage = urls.substring(0, j);
            String avatarUrl = urls.substring(j + 1);
            if (!profilePage.equals("-")) {
                user.setProfilePage(Tools.changeFromSaveable(profilePage));
            }

            if (!avatarUrl.equals("-")) {
                user.loadAvatar(Tools.changeFromSaveable(avatarUrl), this.imageManager, this.playersList);
            }
        }

        return user;
    }
}
