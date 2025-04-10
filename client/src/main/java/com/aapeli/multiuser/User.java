package com.aapeli.multiuser;

import com.aapeli.client.ImageManager;
import com.aapeli.colorgui.GroupListItem;
import com.aapeli.colorgui.SelectableGroupList;
import java.awt.Image;
import org.moparforia.shared.Language;

public final class User {

    private String nick;
    private boolean isLocal;
    private boolean isRegistered;
    private boolean isVip;
    private boolean isSheriff;
    private int rating;
    private int overrideColour;
    private Language language;
    private Image languageFlag;
    private boolean isGettingPrivateMessages;
    private boolean isIgnore;
    private boolean isNotAcceptingChallenges;
    private GroupListItem groupListItem;
    private String profilePage;

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
        this.language = Language.UNKNOWN;
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

        this.groupListItem.setText(newText);
    }

    public void setAfterNickIcon(Image icon) {
        this.groupListItem.setIconAfterText(icon);
        SelectableGroupList selectableGroupList = this.groupListItem.getGroupList();
        if (selectableGroupList != null) {
            selectableGroupList.repaint();
        }
    }

    public Image getAfterNickIcon() {
        return this.groupListItem.getIconAfterText();
    }

    public Language getLanguage() {
        return this.language;
    }

    public Image getLanguageFlag() {
        return this.languageFlag;
    }

    protected void setIsNotAcceptingChallenges(boolean isNotAcceptingChallenges) {
        this.isNotAcceptingChallenges = isNotAcceptingChallenges;
    }

    protected void setLanguage(Language language) {
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
                color = !this.isVip ? GroupListItem.COLOR_BLUE : GroupListItem.COLOR_CYAN;
            } else {
                boolean sheriff = this.isSheriff && sheriffMarkEnabled;
                if (!sheriff && !this.isVip) {
                    color = GroupListItem.COLOR_BLACK;
                } else {
                    color = sheriff ? GroupListItem.COLOR_YELLOW : GroupListItem.COLOR_GREEN;
                }
            }

            if (this.isGettingPrivateMessages) {
                color = GroupListItem.COLOR_MAGENTA;
            }

            if (this.isIgnore) {
                color = GroupListItem.COLOR_RED;
            }

            return color;
        }
    }

    protected void setGroupListItem(GroupListItem groupListItem) {
        this.groupListItem = groupListItem;
    }

    protected GroupListItem getGroupListItem() {
        return this.groupListItem;
    }

    protected void loadAvatar(String imageAlias, ImageManager imageManager, SelectableGroupList playersList) {
        if (!imageManager.isImageDefined(imageAlias)) {
            imageManager.defineImage(imageAlias, imageAlias);
        }

        this.groupListItem.setIcon(imageManager.getImage(imageAlias));
        playersList.repaint();
    }

    protected void setProfilePage(String profilePage) {
        this.profilePage = profilePage;
    }

    protected String getProfilePage() {
        return this.profilePage;
    }
}
