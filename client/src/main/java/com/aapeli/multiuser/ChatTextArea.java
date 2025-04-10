package com.aapeli.multiuser;

import com.aapeli.client.BadWordFilter;
import com.aapeli.client.TextManager;
import com.aapeli.colorgui.ColorListItem;
import com.aapeli.colorgui.TextArea;
import java.awt.Font;
import java.util.Hashtable;

public class ChatTextArea extends TextArea {

    public static final Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 12);
    public static final Font SMALL_FONT = new Font("Dialog", Font.PLAIN, 11);
    private TextManager textManager;
    private BadWordFilter badWordFilter;
    private Hashtable<String, Integer> userColors;

    public ChatTextArea(TextManager textManager, BadWordFilter badWordFilter, int width, int height, Font font) {
        super(width, height, font != null ? font : DEFAULT_FONT);
        this.textManager = textManager;
        this.badWordFilter = badWordFilter;
        this.userColors = new Hashtable<>();
    }

    public void addOwnSay(String user, String message) {
        this.addUserMessage(ColorListItem.COLOR_BLUE, user, message, true);
    }

    public void addOwnSayPrivately(String from, String to, String message) {
        this.addPrivateMessage(ColorListItem.COLOR_BLUE, from, to, message, true);
    }

    public void addSay(String user, String message) {
        this.addUserMessage(ColorListItem.COLOR_BLACK, user, message, false);
    }

    public void addSayPrivately(String from, String to, String message) {
        this.addPrivateMessage(ColorListItem.COLOR_MAGENTA, from, to, message, false);
    }

    public void addJoinMessage(String message) {
        this.addMessage(ColorListItem.COLOR_GREEN, message);
    }

    public void addPartMessage(String message) {
        this.addMessage(ColorListItem.COLOR_RED, message);
    }

    public void addStartedGameMessage(String message) {
        this.addMessage(ColorListItem.COLOR_GRAY, message);
    }

    public void addSheriffSay(String text) {
        this.addImportantLine(ColorListItem.COLOR_CYAN, this.textManager.getText("Chat_SheriffSay", text));
    }

    public void addServerSay(String text) {
        this.addText(ColorListItem.COLOR_CYAN, this.textManager.getText("Chat_ServerSay", text));
    }

    public void addLocalizedServerSay(String text) {
        this.addText(ColorListItem.COLOR_CYAN, text);
    }

    public void addBroadcastMessage(String text) {
        this.addImportantLine(ColorListItem.COLOR_CYAN, this.textManager.getText("Chat_ServerBroadcast", text));
    }

    public void addWelcomeMessage(String text) {
        if (text != null) {
            this.addText(6, text);
        }
    }

    public void addPlainMessage(String text) {
        this.addText(7, text);
    }

    public void addMessage(String text) {
        this.addMessage(ColorListItem.COLOR_GRAY, text);
    }

    public void addHighlightMessage(String var1) {
        this.addMessage(6, var1);
    }

    public void addErrorMessage(String var1) {
        this.addMessage(1, var1);
    }

    public void addFloodMessage() {
        this.addMessage(7, this.textManager.getText("Chat_MessageFlood"));
    }

    public void addPrivateMessageUserLeftMessage(String user) {
        this.addMessage(6, this.textManager.getText("Chat_MessagePrivateMessageUserLeft", user));
    }

    public void setUserColor(String user, int color) {
        this.userColors.put(user, color);
    }

    public void removeUserColor(String user) {
        this.userColors.remove(user);
    }

    public TextManager getTextManager() {
        return this.textManager;
    }

    public BadWordFilter getBadWordFilter() {
        return this.badWordFilter;
    }

    private void addMessage(int color, String text) {
        this.addText(this.normalizeColor(color), this.textManager.getText("Chat_Message", text));
    }

    private void addUserMessage(int fallbackColor, String user, String message, boolean isLocalMessage) {
        message = this.normalizeMessage(message, isLocalMessage);
        if (message.length() > 4 && message.toLowerCase().startsWith("/me ")) {
            this.addText(
                    this.getUserColor(user, fallbackColor),
                    this.textManager.getText("Chat_UserAction", user, message.substring(4)),
                    isLocalMessage);
        } else {
            this.addText(
                    this.getUserColor(user, fallbackColor),
                    this.textManager.getText("Chat_UserSay", user, message),
                    isLocalMessage);
        }
    }

    private void addPrivateMessage(int fallbackColor, String from, String to, String message, boolean isOwnMessage) {
        message = this.normalizeMessage(message, isOwnMessage);
        this.addText(
                this.getUserColor(from, fallbackColor),
                this.textManager.getText("Chat_UserSayPrivate", from, to, message),
                isOwnMessage);
    }

    private int normalizeColor(int color) {
        return this.userColors.size() == 0 ? color : ColorListItem.COLOR_GRAY;
    }

    private int getUserColor(String user, int fallback) {
        Integer color = this.userColors.get(user);
        return color == null ? fallback : color;
    }

    private String normalizeMessage(String message, boolean isOwnMessage) {
        message = this.normalize(message);
        message = this.filter(message, isOwnMessage);
        return message;
    }

    private String normalize(String message) {
        char[] chars = message.toCharArray();
        boolean modified = false;

        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] < 32
                    || chars[i] == 127
                    || chars[i] >= 128 && chars[i] <= 159
                    || chars[i] == 8232
                    || chars[i] == 8233
                    || chars[i] == '\ufff9'
                    || chars[i] == '\ufffa'
                    || chars[i] == '\ufffb'
                    || chars[i] == 8206
                    || chars[i] == 8207
                    || chars[i] == 8234
                    || chars[i] == 8238
                    || chars[i] == '\uf0da') {
                chars[i] = ' ';
                modified = true;
            }

            if (chars[i] == '\uf0da') {
                chars[i] = ' ';
                modified = true;
            }

            if (chars[i] == 'İ') {
                chars[i] = 'I';
                modified = true;
            }
        }

        if (modified) {
            message = new String(chars);
        }

        return message;
    }

    private String filter(String message, boolean isOwnMessage) {
        if (this.badWordFilter != null && !isOwnMessage) {
            message = this.badWordFilter.filter(message);
        }

        return message;
    }
}
