package com.aapeli.multiuser;

import com.aapeli.client.ImageManager;
import com.aapeli.colorgui.ColorList;
import com.aapeli.colorgui.ColorListItem;
import java.awt.Image;

public final class User {

    private String nick;
    private boolean isLocal;
    private boolean isRegistered;
    private boolean isVip;
    private boolean isSheriff;
    private int rating;
    private int overrideColour;
    private int language;
    private Image languageFlag;
    private boolean isGettingPrivateMessages;
    private boolean isIgnore;
    private boolean isNotAcceptingChallenges;
    private ColorListItem colorListItem;
    private String profilePage;
    public static boolean aBoolean1684;

    public User(String nick, boolean isLocal, boolean isRegistered, boolean isVip, boolean isSheriff) {
        this(nick, isLocal, isRegistered, isVip, isSheriff, -1);
    }

    public User(String nick, boolean isLocal, boolean isRegistered, boolean isVip, boolean isSheriff, int rating) {
        if (!isRegistered) {
            isSheriff = false;
            isVip = false;
            rating = -1;
        }

        this.nick = nick;
        this.isLocal = isLocal;
        this.isRegistered = isRegistered;
        this.isVip = isVip;
        this.isSheriff = isSheriff;
        this.rating = rating;
        this.isGettingPrivateMessages = false;
        this.isIgnore = false;
        this.isNotAcceptingChallenges = false;
        this.overrideColour = -1;
        this.language = 0;
    }

    public String getNick() {
        return this.nick;
    }

    public boolean isLocal() {
        return this.isLocal;
    }

    public boolean isRegistered() {
        return this.isRegistered;
    }

    public boolean isVip() {
        return this.isVip;
    }

    public boolean isSheriff() {
        return this.isSheriff;
    }

    public int getRating() {
        return this.rating;
    }

    public boolean isGettingPrivateMessages() {
        return this.isGettingPrivateMessages;
    }

    public boolean isIgnore() {
        return this.isIgnore;
    }

    public void setOverrideColor(int overrideColor) {
        this.overrideColour = overrideColor;
    }

    public boolean isNotAcceptingChallenges() {
        return this.isNotAcceptingChallenges;
    }

    public void setAfterNickText(String suffix) {
        String newText = this.nick;
        if (suffix != null) {
            newText = newText + " " + suffix;
        }

        this.colorListItem.setText(newText);
    }

    public void setAfterNickIcon(Image icon) {
        this.colorListItem.setIconAfterText(icon);
        ColorList colorList = this.colorListItem.getColorListReference();
        if (colorList != null) {
            colorList.repaint();
        }
    }

    public Image getAfterNickIcon() {
        return this.colorListItem.getIconAfterText();
    }

    public int getLanguage() {
        return this.language;
    }

    public Image getLanguageFlag() {
        return this.languageFlag;
    }

    protected void setIsNotAcceptingChallenges(boolean isNotAcceptingChallenges) {
        this.isNotAcceptingChallenges = isNotAcceptingChallenges;
    }

    protected void setLanguage(int language) {
        this.language = language;
    }

    protected void setLanguageFlag(Image languageFlag) {
        this.languageFlag = languageFlag;
    }

    protected void setGettingPrivateMessages(boolean gettingPrivateMessages) {
        this.isGettingPrivateMessages = gettingPrivateMessages;
    }

    protected void setIgnore(boolean ignore) {
        this.isIgnore = ignore;
    }

    protected int getColor(boolean sheriffMarkEnabled) {
        if (this.overrideColour >= 0) {
            return this.overrideColour;
        } else {
            int color;
            if (this.isLocal) {
                color = !this.isVip ? ColorListItem.COLOR_BLUE : ColorListItem.COLOR_CYAN;
            } else {
                boolean sheriff = this.isSheriff && sheriffMarkEnabled;
                if (!sheriff && !this.isVip) {
                    color = ColorListItem.COLOR_BLACK;
                } else {
                    color = sheriff ? ColorListItem.COLOR_YELLOW : ColorListItem.COLOR_GREEN;
                }
            }

            if (this.isGettingPrivateMessages) {
                color = ColorListItem.COLOR_MAGENTA;
            }

            if (this.isIgnore) {
                color = ColorListItem.COLOR_RED;
            }

            return color;
        }
    }

    protected void setColorListItem(ColorListItem colorListItem) {
        this.colorListItem = colorListItem;
    }

    protected ColorListItem getColorListItem() {
        return this.colorListItem;
    }

    protected void loadAvatar(String imageAlias, ImageManager imageManager, ColorList playersList) {
        if (!imageManager.isGameImageDefined(imageAlias)) {
            imageManager.defineGameImage(imageAlias, imageAlias);
        }

        this.colorListItem.setIcon(imageManager.getGameImage(imageAlias));
        playersList.repaint();
    }

    protected void setProfilePage(String profilePage) {
        this.profilePage = profilePage;
    }

    protected String getProfilePage() {
        return this.profilePage;
    }
}
