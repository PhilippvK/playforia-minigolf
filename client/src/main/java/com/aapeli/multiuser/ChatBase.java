package com.aapeli.multiuser;

import com.aapeli.client.BadWordFilter;
import com.aapeli.client.IPanel;
import com.aapeli.client.ImageManager;
import com.aapeli.client.InputTextField;
import com.aapeli.client.InputTextFieldListener;
import com.aapeli.client.Parameters;
import com.aapeli.client.TextManager;
import com.aapeli.client.UrlLabel;
import com.aapeli.colorgui.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;
import org.moparforia.shared.Language;

public abstract class ChatBase extends IPanel
        implements ComponentListener, UserListHandler, ActionListener, InputTextFieldListener {

    public static final int CIDR_NONE = 0;
    public static final int CIDR_UNREG = 1;
    public static final int CIDR_UNCONF = 2;
    private static final Color sayButtonColor = new Color(144, 144, 224);
    private static boolean shouldDisplayChatInputHelp = true;
    public Parameters parameters;
    public TextManager textManager;
    public ImageManager imageManager;
    private BadWordFilter badWordFilter;
    private FloodProtection floodProtection;
    public int width;
    public int height;
    private Image backgroundImage;
    private int backgroundImageOffsetX;
    private int backgroundImageOffsetY;
    private int lastX;
    private int lastY;
    private String localUserNick;
    private int chatDisabledStatus;
    public UserList userList;
    public ChatTextArea chatTextArea;
    public MultiLanguageChatContainer multiLanguageChatContainer;
    public InputTextField inputTextField;
    public Component sayButton;
    public UrlLabel signupMessage;
    private String messageRecipient;
    private List<ChatListener> chatListeners;
    private Object lock;

    public ChatBase(
            Parameters parameters,
            TextManager textManager,
            ImageManager imageManager,
            BadWordFilter badWordFilter,
            boolean useSmallFont,
            boolean hideRankingIcons,
            int width,
            int height) {
        this(
                parameters,
                textManager,
                imageManager,
                badWordFilter,
                true,
                true,
                useSmallFont,
                hideRankingIcons,
                false,
                width,
                height);
    }

    public ChatBase(
            Parameters parameters,
            TextManager textManager,
            ImageManager imageManager,
            BadWordFilter badWordFilter,
            boolean addSendPrivatelyCheckbox,
            boolean addIgnoreUserCheckbox,
            boolean useSmallFont,
            boolean hideRankingIcons,
            boolean shouldNotWriteWelcomeMessage,
            int width,
            int height) {
        this.parameters = parameters;
        this.textManager = textManager;
        this.imageManager = imageManager;
        this.badWordFilter = badWordFilter;
        this.lock = new Object();
        this.floodProtection = new FloodProtection();
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.localUserNick = null;
        this.chatDisabledStatus = 0;
        this.init(
                addSendPrivatelyCheckbox,
                addIgnoreUserCheckbox,
                useSmallFont,
                hideRankingIcons,
                shouldNotWriteWelcomeMessage);
        this.addComponentListener(this);
        this.chatListeners = new ArrayList<>();
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
                    this.backgroundImage, this.backgroundImageOffsetX + newX, this.backgroundImageOffsetY + newY);
        }
    }

    public void componentResized(ComponentEvent event) {
        Dimension size = this.getSize();
        this.width = size.width;
        this.height = size.height;
        this.resizeLayout();
    }

    public void openPlayerCard(String playerNick) {
        this.parameters.showPlayerCard(playerNick);
    }

    public void adminCommand(String command, String parameter1) {
        ChatListener[] listeners = this.getChatListenersCopy();

        for (ChatListener chatListener : listeners) {
            chatListener.localUserAdminCommand(command, parameter1);
        }
    }

    public void adminCommand(String command, String parameter1, String parameter2) {
        ChatListener[] var4 = this.getChatListenersCopy();

        for (ChatListener chatListener : var4) {
            chatListener.localUserAdminCommand(command, parameter1, parameter2);
        }
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.sayButton) {
            this.sendMessage();
        }
    }

    public void startedTyping() {
        this.messageRecipient = this.getMessageRecipient();
    }

    public void clearedField() {
        this.messageRecipient = null;
    }

    public void enterPressed() {
        this.sendMessage();
    }

    public static UrlLabel setInputByCIDR(
            int chatDisabledStatus,
            Container container,
            InputTextField inputTextField,
            Component sayButton,
            UrlLabel signupMessage,
            TextManager textManager,
            Parameters parameters) {
        return getSignupMessage(
                chatDisabledStatus,
                container,
                inputTextField,
                sayButton,
                signupMessage,
                textManager.getText("Chat_NoGuestChatAndRegNote"),
                textManager.getText("Chat_NoUnconfirmedChatNote"),
                parameters);
    }

    public void setBackground(Color color) {
        if (this.userList != null) {
            this.userList.setBackground(color);
        }

        if (this.signupMessage != null) {
            this.signupMessage.setBackground(color);
        }

        this.repaint();
    }

    public void setForeground(Color color) {
        if (this.userList != null) {
            this.userList.setForeground(color);
        }

        if (this.signupMessage != null) {
            this.signupMessage.setForeground(color);
        }
    }

    public void setBackgroundImage(Image image, int offsetX, int offsetY) {
        this.backgroundImage = image;
        this.backgroundImageOffsetX = offsetX;
        this.backgroundImageOffsetY = offsetY;
        Point location = this.getLocation();
        this.lastX = location.x;
        this.lastY = location.y;
        Point userListLocation = this.userList.getLocation();
        this.userList.setBackgroundImage(image, offsetX + userListLocation.x, offsetY + userListLocation.y);
        this.repaint();
    }

    public synchronized void addChatListener(ChatListener listener) {
        this.chatListeners.add(listener);
    }

    public synchronized void removeChatListener(ChatListener listener) {
        this.chatListeners.remove(listener);
    }

    public void setMessageMaximumLength(int limit) {
        this.inputTextField.setTextMaximumLength(limit);
    }

    public void clearOutput() {
        synchronized (this.lock) {
            if (this.multiLanguageChatContainer == null) {
                this.chatTextArea.clear();
            } else {
                this.multiLanguageChatContainer.clear();
            }
        }
    }

    public void enablePopUp(boolean isModerator, boolean isAdmin) {
        this.userList.enableRightClickMenu(isModerator, isAdmin);
    }

    public void addPlainMessage(String message) {
        this.chatTextArea.addPlainMessage(message);
    }

    public void addMessage(String message) {
        this.chatTextArea.addMessage(message);
    }

    public void addHighlightMessage(String message) {
        this.chatTextArea.addHighlightMessage(message);
    }

    public void addErrorMessage(String message) {
        this.chatTextArea.addErrorMessage(message);
    }

    public void addLine() {
        this.chatTextArea.addText();
    }

    public int setUserList(String[] list) {
        return this.setUserList(list, -1);
    }

    public int setUserList(String[] list, int localUserIndex) {
        this.userList.removeAllUsers();
        int l = list.length;

        for (int i = 0; i < l; ++i) {
            this.addToUserList(list[i], localUserIndex == i);
        }

        return l;
    }

    public String localUserJoin(String userData) {
        this.addToUserList(userData, true);
        return this.localUserNick;
    }

    public void userSay(String user, String message) {
        if (!this.isUserIgnored(user)) {
            this.chatTextArea.addSay(user, message);
        }
    }

    public void userSay(Language language, String user, String message) {
        if (!this.isUserIgnored(user)) {
            this.multiLanguageChatContainer.addMessage(language, user, message);
        }
    }

    public void userSayPrivately(String from, String message) {
        if (!this.isUserIgnored(from)) {
            this.chatTextArea.addSayPrivately(from, this.localUserNick, message);
        }
    }

    public void sheriffSay(String text) {
        synchronized (this.lock) {
            if (this.multiLanguageChatContainer == null) {
                this.chatTextArea.addSheriffSay(text);
            } else {
                this.multiLanguageChatContainer.addSheriffSay(text);
            }
        }
    }

    public void serverSay(String message) {
        if (message.startsWith("L10N:")) {
            String localizationArgument1 = null;
            int i = message.indexOf(';');
            String localizedMessage;
            String localizationKey;
            if (i == -1) {
                localizationKey = message.substring(5);
                localizedMessage = this.textManager.getText(localizationKey);
            } else {
                localizationKey = message.substring(5, i);
                int j = message.indexOf(';', i + 1);
                if (j == -1) {
                    localizationArgument1 = message.substring(i + 1);
                    localizedMessage = this.textManager.getText(localizationKey, localizationArgument1);
                } else {
                    localizationArgument1 = message.substring(i + 1, j);
                    String localizationArgument2 = message.substring(j + 1);
                    localizedMessage =
                            this.textManager.getText(localizationKey, localizationArgument1, localizationArgument2);
                }
            }

            if (localizedMessage.length() > 1) {
                if ((localizationKey.equals("ServerSay_SheriffGaveWarning")
                                || localizationKey.equals("ServerSay_SheriffMutedUser")
                                || localizationKey.equals("ServerSay_SheriffUnMutedUser"))
                        && localizationArgument1 != null
                        && !this.userList.isUser(localizationArgument1)) {
                    return;
                }

                synchronized (this.lock) {
                    if (this.multiLanguageChatContainer == null) {
                        this.chatTextArea.addLocalizedServerSay(localizedMessage);
                    } else {
                        this.multiLanguageChatContainer.addLocalizedServerSay(localizedMessage);
                    }
                }
            }

        } else {
            synchronized (this.lock) {
                if (this.multiLanguageChatContainer == null) {
                    this.chatTextArea.addServerSay(message);
                } else {
                    this.multiLanguageChatContainer.addServerSay(message);
                }
            }
        }
    }

    public void broadcastMessage(String message) {
        synchronized (this.lock) {
            if (this.multiLanguageChatContainer == null) {
                this.chatTextArea.addBroadcastMessage(message);
            } else {
                this.multiLanguageChatContainer.addBroadcastMessage(message);
            }
        }
    }

    public boolean isUserInLobby(String nick) {
        return this.isUserInChat(nick);
    }

    public boolean isUserInChat(String nick) {
        return this.userList.getUser(nick) != null;
    }

    public boolean isUserIgnored(String user) {
        User userItem = this.userList.getUser(user);
        return userItem == null ? true : userItem.isIgnore();
    }

    public UserList getUserList() {
        return this.userList;
    }

    public void addChatWithLanguage(Language language) {
        synchronized (this.lock) {
            if (this.multiLanguageChatContainer == null) {
                Point location = this.chatTextArea.getLocation();
                this.remove(this.chatTextArea);
                this.multiLanguageChatContainer = new MultiLanguageChatContainer(this, this.chatTextArea, language);
                this.multiLanguageChatContainer.setLocation(location.x, location.y);
                this.add(this.multiLanguageChatContainer);
            }
        }
    }

    public void disableChatInput(int status) {
        this.chatDisabledStatus = status;
        this.paintSignupMessage();
    }

    public boolean isTyping() {
        return this.inputTextField.isTyping();
    }

    public abstract void resizeLayout();

    public String addToUserList(String userData, boolean isLocal) {
        return this.addToUserListNew(userData, isLocal).getNick();
    }

    public User addToUserListNew(String userData, boolean isLocal) {
        User user = this.userList.addUser(userData, isLocal);
        if (isLocal) {
            this.localUserNick = user.getNick();
        }

        synchronized (this.lock) {
            if (this.multiLanguageChatContainer != null) {
                this.multiLanguageChatContainer.createLanguageChatArea(user.getLanguage());
            }

            return user;
        }
    }

    public String getRegistrationNeededText() {
        return this.textManager.getText("Chat_NoGuestChatAndRegNote");
    }

    public String getConfirmationNeededText() {
        return this.textManager.getText("Chat_NoUnconfirmedChatNote");
    }

    public void setChatTextArea(ChatTextArea chatTextArea) {
        this.chatTextArea = chatTextArea;
    }

    protected void addMessage(User user, String text) {
        synchronized (this.lock) {
            if (this.multiLanguageChatContainer == null) {
                this.chatTextArea.addMessage(text);
            } else {
                this.multiLanguageChatContainer.addMessage(user.getLanguage(), text);
            }
        }
    }

    protected void addMessage(User user1, User user2, String text) {
        synchronized (this.lock) {
            if (this.multiLanguageChatContainer == null) {
                this.chatTextArea.addMessage(text);
            } else {
                Language language1 = user1.getLanguage();
                Language language2 = user2.getLanguage();
                this.multiLanguageChatContainer.addMessage(language1, text);
                if (language2 != language1) {
                    this.multiLanguageChatContainer.addMessage(language2, text);
                }
            }
        }
    }

    private static UrlLabel getSignupMessage(
            int chatDisabledStatus,
            Container container,
            InputTextField inputTextField,
            Component sayButton,
            UrlLabel signupMessage,
            String registrationNeededText,
            String confirmationNeededText,
            Parameters parameters) {
        if (chatDisabledStatus == 0) {
            if (signupMessage != null) {
                signupMessage.setVisible(false);
            }

            inputTextField.setVisible(true);
            sayButton.setVisible(true);
            return signupMessage;
        } else {
            if (signupMessage == null) {
                signupMessage = new UrlLabel();
                Point inputFieldLocation = inputTextField.getLocation();
                Point sayButtonLocation = sayButton.getLocation();
                Dimension sayButtonSize = sayButton.getSize();
                signupMessage.setBounds(
                        inputFieldLocation.x,
                        inputFieldLocation.y,
                        sayButtonLocation.x + sayButtonSize.width - inputFieldLocation.x,
                        sayButtonLocation.y + sayButtonSize.height - inputFieldLocation.y);
                signupMessage.setBackground(container.getBackground());
                signupMessage.setForeground(container.getForeground());
                container.add(signupMessage);
            }

            inputTextField.setVisible(false);
            sayButton.setVisible(false);
            if (chatDisabledStatus == 1) {
                signupMessage.setText(registrationNeededText, parameters.getRegisterPage());
                signupMessage.setTarget(0);
            } else if (chatDisabledStatus == 2) {
                signupMessage.setText(confirmationNeededText, null);
            } else {
                signupMessage.setText(null, null);
            }

            signupMessage.setVisible(true);
            return signupMessage;
        }
    }

    private void init(
            boolean addSendPrivatelyCheckbox,
            boolean addIgnoreUserCheckbox,
            boolean useSmallFont,
            boolean hideRankingIcons,
            boolean shouldNotWriteWelcomeMessage) {
        this.setLayout(null);
        this.chatTextArea = new ChatTextArea(
                this.textManager,
                this.badWordFilter,
                200,
                100,
                useSmallFont ? ChatTextArea.SMALL_FONT : ChatTextArea.DEFAULT_FONT);
        if (shouldDisplayChatInputHelp && !shouldNotWriteWelcomeMessage) {
            this.chatTextArea.addWelcomeMessage(this.textManager.getText("Chat_Welcome"));
        }

        this.add(this.chatTextArea);
        if (shouldDisplayChatInputHelp) {
            this.inputTextField = new InputTextField(this.textManager.getText("Chat_InputHelp"), 200, true);
            shouldDisplayChatInputHelp = false;
        } else {
            this.inputTextField = new InputTextField(200, true);
        }

        this.inputTextField.addInputTextFieldListener(this);
        this.add(this.inputTextField);
        Button sayButton = new Button(this.textManager.getText("Chat_SayButton"));
        sayButton.setBackground(sayButtonColor);
        sayButton.addActionListener(this);
        this.add(sayButton);
        this.sayButton = sayButton;
        this.userList = new UserList(
                this,
                this.textManager,
                this.imageManager,
                !hideRankingIcons,
                addSendPrivatelyCheckbox,
                addIgnoreUserCheckbox);
        this.userList.setChatReference(this);
        this.add(this.userList);
        this.signupMessage = new UrlLabel();
        this.add(this.signupMessage);
        this.paintSignupMessage();
    }

    private void sendMessage() {
        if (this.localUserNick != null) {
            String message = this.inputTextField.getText().trim();
            if (message.length() != 0) {
                if (!this.floodProtection.isOkToSay(message)) {
                    this.chatTextArea.addFloodMessage();
                } else {
                    String messageRecipient = this.getMessageRecipient();
                    String previousMessageRecipient = this.messageRecipient;
                    this.messageRecipient = null;
                    if (messageRecipient == null
                            && previousMessageRecipient != null
                            && !this.isUserInChat(previousMessageRecipient)) {
                        this.chatTextArea.addPrivateMessageUserLeftMessage(previousMessageRecipient);
                    } else {
                        message = this.inputTextField.getInputText();
                        if (message.length() > 0) {
                            ChatListener[] listeners = this.getChatListenersCopy();
                            if (messageRecipient != null) {
                                for (ChatListener chatListener : listeners) {
                                    chatListener.localUserSayPrivately(messageRecipient, message);
                                }

                                this.chatTextArea.addOwnSayPrivately(this.localUserNick, messageRecipient, message);
                                return;
                            }

                            synchronized (this.lock) {
                                if (this.multiLanguageChatContainer == null) {
                                    for (ChatListener listener : listeners) {
                                        listener.localUserSay(message);
                                    }
                                } else {
                                    Language language = this.multiLanguageChatContainer.getLanguage();

                                    for (ChatListener chatListener : listeners) {
                                        ((MultiLanguageChatListener) chatListener).localUserSay(language, message);
                                    }
                                }
                            }

                            this.chatTextArea.addOwnSay(this.localUserNick, message);
                        }
                    }
                }
            }
        }
    }

    private String getMessageRecipient() {
        User user = this.userList.getSelectedUser();
        return user != null && user.isGettingPrivateMessages() ? user.getNick() : null;
    }

    private ChatListener[] getChatListenersCopy() {
        int chatListenersCount = this.chatListeners.size();
        ChatListener[] chatListeners = new ChatListener[chatListenersCount];

        for (int i = 0; i < chatListenersCount; ++i) {
            chatListeners[i] = this.chatListeners.get(i);
        }

        return chatListeners;
    }

    private void paintSignupMessage() {
        this.signupMessage = getSignupMessage(
                this.chatDisabledStatus,
                this,
                this.inputTextField,
                this.sayButton,
                this.signupMessage,
                this.getRegistrationNeededText(),
                this.getConfirmationNeededText(),
                this.parameters);
    }
}
