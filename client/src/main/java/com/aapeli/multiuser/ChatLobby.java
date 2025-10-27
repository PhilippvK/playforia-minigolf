package com.aapeli.multiuser;

import com.aapeli.client.BadWordFilter;
import com.aapeli.client.ImageManager;
import com.aapeli.client.Parameters;
import com.aapeli.client.TextManager;
import com.aapeli.colorgui.Checkbox;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;

public class ChatLobby extends ChatBase {

    private Checkbox noJoinAndPartMessagesCheckbox;
    private Checkbox noGameMessagesChatbox;
    private boolean shouldMuteMessagesIfUserListSet;

    public ChatLobby(
            Parameters parameters,
            TextManager textManager,
            ImageManager imageManager,
            BadWordFilter badWordFilter,
            int width,
            int height) {
        this(parameters, textManager, imageManager, badWordFilter, false, false, width, height);
    }

    public ChatLobby(
            Parameters parameters,
            TextManager textManager,
            ImageManager imageManager,
            BadWordFilter badWordFilter,
            boolean useSmallFont,
            boolean hideRankingIcons,
            int width,
            int height) {
        super(parameters, textManager, imageManager, badWordFilter, useSmallFont, hideRankingIcons, width, height);
        this.createSettingsCheckBoxes();
        this.resizeLayout();
        this.shouldMuteMessagesIfUserListSet = true;
    }

    public void setBackground(Color color) {
        super.setBackground(color);
        if (this.noJoinAndPartMessagesCheckbox != null) {
            this.noJoinAndPartMessagesCheckbox.setBackground(color);
        }

        if (this.noGameMessagesChatbox != null) {
            this.noGameMessagesChatbox.setBackground(color);
        }
    }

    public void setForeground(Color color) {
        super.setForeground(color);
        if (this.noJoinAndPartMessagesCheckbox != null) {
            this.noJoinAndPartMessagesCheckbox.setForeground(color);
        }

        if (this.noGameMessagesChatbox != null) {
            this.noGameMessagesChatbox.setForeground(color);
        }
    }

    public void setBackgroundImage(Image image, int offsetX, int offsetY) {
        super.setBackgroundImage(image, offsetX, offsetY);
        Point noJoinMessagesLocation = this.noJoinAndPartMessagesCheckbox.getLocation();
        Point noGameMessagesLocation = this.noGameMessagesChatbox.getLocation();
        this.noJoinAndPartMessagesCheckbox.setBackgroundImage(
                image, offsetX + noJoinMessagesLocation.x, offsetY + noJoinMessagesLocation.y);
        this.noGameMessagesChatbox.setBackgroundImage(
                image, offsetX + noGameMessagesLocation.x, offsetY + noGameMessagesLocation.y);
    }

    public int setUserList(String[] list) {
        int users = super.setUserList(list);
        if (this.shouldMuteMessagesIfUserListSet) {
            if (users >= 30) {
                this.noJoinAndPartMessagesCheckbox.setState(true);
                if (users >= 40) {
                    this.noGameMessagesChatbox.setState(true);
                }
            }

            this.shouldMuteMessagesIfUserListSet = false;
        }

        return users;
    }

    public String userJoin(String userData) {
        return this.addToUserList(userData, false);
    }

    public String userJoin(String userData, boolean returnedFromGame) {
        User user = this.addToUserListNew(userData, false);
        if (!this.noJoinAndPartMessagesCheckbox.getState()) {
            this.addMessage(
                    user,
                    super.textManager.getText(
                            "Chat_Lobby_User" + (returnedFromGame ? "ReturnedFromGame" : "Joined"), user.getNick()));
        }

        return user.getNick();
    }

    public void userLeft(String nick) {
        super.userList.removeUser(nick);
    }

    public void userLeft(String nick, boolean disconnected) {
        User user = super.userList.removeAndReturnUser(nick);
        if (user != null && !this.noJoinAndPartMessagesCheckbox.getState()) {
            this.addMessage(
                    user,
                    super.textManager.getText("Chat_Lobby_UserLeft" + (disconnected ? "ConnectionProblem" : ""), nick));
        }
    }

    public void userLeftCreatedGame(String nick, String lobbyName) {
        User user = super.userList.removeAndReturnUser(nick);
        if (user != null && !this.noGameMessagesChatbox.getState()) {
            this.addMessage(user, super.textManager.getText("Chat_Lobby_UserCreatedGame", nick, lobbyName));
        }
    }

    public void userLeftJoinedGame(String nick, String lobbyName) {
        User user = super.userList.removeAndReturnUser(nick);
        if (user != null && !this.noGameMessagesChatbox.getState()) {
            this.addMessage(user, super.textManager.getText("Chat_Lobby_UserJoinedGame", nick, lobbyName));
        }
    }

    public void userLeftWatchingGame(String nick, String lobbyName) {
        User user = super.userList.removeAndReturnUser(nick);
        if (user != null && !this.noGameMessagesChatbox.getState()) {
            this.addMessage(user, super.textManager.getText("Chat_Lobby_UserWathicngGame", nick, lobbyName));
        }
    }

    public void usersLeftStartedGame(String nick1, String nick2, String lobbyName) {
        User user1 = super.userList.removeAndReturnUser(nick1);
        User user2 = super.userList.removeAndReturnUser(nick2);
        if (user1 != null && user2 != null && !this.noGameMessagesChatbox.getState()) {
            String text;
            if (lobbyName != null) {
                text = super.textManager.getText("Chat_Lobby_UsersStartedGame", nick1, nick2, lobbyName);
            } else {
                text = super.textManager.getText("Chat_Lobby_UsersStartedUnnamedGame", nick1, nick2);
            }

            this.addMessage(user1, user2, text);
        }
    }

    public void usersLeftStartedGame(String nick1, String nick2) {
        this.usersLeftStartedGame(nick1, nick2, null);
    }

    public User getSelectedUserForChallenge() {
        User selectedUser = super.userList.getSelectedUser();
        if (selectedUser == null) {
            super.chatTextArea.addMessage(super.textManager.getText("Chat_Lobby_CantChallengeNone"));
        } else {
            if (!selectedUser.isLocal()) {
                return selectedUser;
            }

            super.chatTextArea.addMessage(super.textManager.getText("Chat_Lobby_CantChallengeSelf"));
        }

        return null;
    }

    public String getSelectedNickForChallenge() {
        User user = this.getSelectedUserForChallenge();
        return user != null ? user.getNick() : null;
    }

    public boolean[] getCheckBoxStates() {
        return new boolean[] {this.noJoinAndPartMessagesCheckbox.getState(), this.noGameMessagesChatbox.getState()};
    }

    public void setCheckBoxStates(boolean noJoinAndParMessages, boolean noGameMessages) {
        this.noJoinAndPartMessagesCheckbox.setState(noJoinAndParMessages);
        this.noGameMessagesChatbox.setState(noGameMessages);
    }

    public boolean isNoJoinPartMessages() {
        return this.noJoinAndPartMessagesCheckbox.getState();
    }

    public boolean isNoGameMessages() {
        return this.noGameMessagesChatbox.getState();
    }

    public void resizeLayout() {
        int userListWidth = super.width / 5;
        if (userListWidth < 100) {
            userListWidth = 100;
        }

        if (userListWidth > 150) {
            userListWidth = 150;
        }

        double inputAndSettingsExtraRoom = ((double) super.height - 100.0D) / 100.0D;
        int inputFieldHeight = (int) (20.0D + inputAndSettingsExtraRoom * 5.0D);
        int checkboxSettingsHeight = (int) (15.0D + inputAndSettingsExtraRoom * 5.0D);
        if (inputFieldHeight < 20) {
            inputFieldHeight = 20;
        }

        if (inputFieldHeight > 25) {
            inputFieldHeight = 25;
        }

        if (checkboxSettingsHeight < 15) {
            checkboxSettingsHeight = 15;
        }

        if (checkboxSettingsHeight > 20) {
            checkboxSettingsHeight = 20;
        }

        int chatAreaWidth = super.width - 0 - 3 - userListWidth - 0;
        int chatAreaHeight = super.height - 0 - checkboxSettingsHeight - 3 - inputFieldHeight - 2 - 0;
        int sayButtonWidth = (int) (50.0D + ((double) chatAreaWidth - 200.0D) / 300.0D * 70.0D);
        if (sayButtonWidth < 50) {
            sayButtonWidth = 50;
        }

        if (sayButtonWidth > 100) {
            sayButtonWidth = 100;
        }

        int inputFieldWidth = chatAreaWidth - 1 - sayButtonWidth;
        int checkboxSettingsWidth = (chatAreaWidth - 2) / 2;
        super.userList.setBounds(0, 0, userListWidth, super.height);
        synchronized (this) {
            if (super.multiLanguageChatContainer == null) {
                super.chatTextArea.setBounds(0 + userListWidth + 3, 0, chatAreaWidth, chatAreaHeight);
            } else {
                super.multiLanguageChatContainer.setBounds(0 + userListWidth + 3, 0, chatAreaWidth, chatAreaHeight);
            }
        }

        int inputFieldX = 0 + userListWidth + 3;
        int inputFieldY = 0 + chatAreaHeight + 2;
        super.inputTextField.setBounds(inputFieldX, inputFieldY, inputFieldWidth, inputFieldHeight);
        int sayButtonX = 0 + userListWidth + 3 + inputFieldWidth + 1;
        super.sayButton.setBounds(sayButtonX, 0 + chatAreaHeight + 2, sayButtonWidth, inputFieldHeight);

        this.noJoinAndPartMessagesCheckbox.setBounds(
                0 + userListWidth + 3,
                super.height - 0 - checkboxSettingsHeight,
                checkboxSettingsWidth,
                checkboxSettingsHeight);
        this.noGameMessagesChatbox.setBounds(
                0 + userListWidth + 3 + checkboxSettingsWidth + 2,
                super.height - 0 - checkboxSettingsHeight,
                checkboxSettingsWidth,
                checkboxSettingsHeight);

        super.signupMessage.setBounds(
                inputFieldX, inputFieldY, sayButtonX - inputFieldX + sayButtonWidth, inputFieldHeight);
    }

    private void createSettingsCheckBoxes() {
        this.noJoinAndPartMessagesCheckbox = new Checkbox(super.textManager.getText("Chat_Lobby_NoJoinPartMessages"));
        this.add(this.noJoinAndPartMessagesCheckbox);
        this.noGameMessagesChatbox = new Checkbox(super.textManager.getText("Chat_Lobby_NoGameMessages"));
        this.add(this.noGameMessagesChatbox);
    }
}
