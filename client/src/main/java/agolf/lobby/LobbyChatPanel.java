package agolf.lobby;

import agolf.GameApplet;
import agolf.GameContainer;
import com.aapeli.multiuser.ChatLobby;
import com.aapeli.multiuser.Languages;
import com.aapeli.multiuser.MultiLanguageChatListener;
import com.aapeli.multiuser.User;

class LobbyChatPanel extends ChatLobby implements MultiLanguageChatListener {

    private static boolean aBoolean3712 = true;
    private static boolean aBoolean3713 = true;
    private static boolean aBoolean3714 = true;
    private GameContainer gameContainer;
    private int lobbyId;

    protected LobbyChatPanel(GameContainer gameContainer, int width, int height, int lobbyId) {
        super(
                gameContainer.params,
                gameContainer.textManager,
                gameContainer.imageManager,
                gameContainer.badWordFilter,
                width,
                height);
        this.setMessageMaximumLength(200);
        this.gameContainer = gameContainer;
        this.lobbyId = lobbyId;
        this.setBackground(GameApplet.colourGameBackground);
        this.setForeground(GameApplet.colourTextBlack);
        int elevation = gameContainer.gameApplet.getPlayerAccessLevel();
        this.enablePopUp(elevation >= 1, elevation >= 2);
        if (!gameContainer.gameApplet.isEmailVerified()) {
            this.disableChatInput(1);
        } else if (gameContainer.gameApplet.isGuestChatDisabled()) {
            this.disableChatInput(2);
        }

        this.addChatWithLanguage(Languages.getLanguageId(gameContainer.params.getLocale()));
        this.addChatListener(this);
    }

    public void localUserSay(String var1) {}

    public void localUserSay(int var1, String var2) {
        this.gameContainer.lobbyPanel.writeData("say\t" + var1 + "\t" + var2);
    }

    public void localUserSayPrivately(String var1, String var2) {
        this.gameContainer.lobbyPanel.writeData("sayp\t" + var1 + "\t" + var2);
    }

    public void localUserAdminCommand(String var1, String var2) {
        this.gameContainer.lobbyPanel.writeData("command\t" + var1 + "\t" + var2);
    }

    public void localUserAdminCommand(String var1, String var2, String var3) {
        this.gameContainer.lobbyPanel.writeData("command\t" + var1 + "\t" + var2 + "\t" + var3);
    }

    protected boolean handlePacket(String[] args) {
        if (args[1].equals("numberofusers")) {
            if ((this.lobbyId != 1 || aBoolean3712)
                    && (this.lobbyId != 2 || aBoolean3713)
                    && (this.lobbyId != 3 || aBoolean3714)) {
                if (this.lobbyId == 1) {
                    aBoolean3712 = false; // ??
                }

                if (this.lobbyId == 2) {
                    aBoolean3713 = false; // ??
                }

                if (this.lobbyId == 3) {
                    aBoolean3714 = false; // ??
                }
            } else {
                super.chatTextArea.clear();
                super.userList.removeAllUsers();
            }

            int numSingleLobby = Integer.parseInt(args[2]);
            int numSingleGames = Integer.parseInt(args[3]);
            int numDualLobby = Integer.parseInt(args[4]);
            int numDualGames = Integer.parseInt(args[5]);
            int numMultiLobby = Integer.parseInt(args[6]);
            int numMultiGames = Integer.parseInt(args[7]);
            int singleLobbyUsers = numSingleLobby + numSingleGames;
            int dualLobbyUsers = numDualLobby + numDualGames;
            int multiLobbyUsers = numMultiLobby + numMultiGames;
            int lobbyUsers = -1;
            int ingameUsers = -1;
            if (this.lobbyId == 1) {
                lobbyUsers = numSingleLobby + numSingleGames;
                ingameUsers = numSingleGames;
            }

            if (this.lobbyId == 2) {
                lobbyUsers = numDualLobby + numDualGames;
                ingameUsers = numDualGames;
            }

            if (this.lobbyId == 3) {
                lobbyUsers = numMultiLobby + numMultiGames;
                ingameUsers = numMultiGames;
            }

            String message = null;
            if (lobbyUsers == 0 && ingameUsers == 0) {
                message = this.gameContainer.textManager.getGame("LobbyChat_UsersInThisLobby00");
            }

            if (lobbyUsers == 1 && ingameUsers == 0) {
                message = this.gameContainer.textManager.getGame("LobbyChat_UsersInThisLobby10");
            }

            if (lobbyUsers == 1 && ingameUsers == 1) {
                message = this.gameContainer.textManager.getGame("LobbyChat_UsersInThisLobby11");
            }

            if (lobbyUsers >= 2 && ingameUsers == 0) {
                message = this.gameContainer.textManager.getGame("LobbyChat_UsersInThisLobbyX0", lobbyUsers);
            }

            if (lobbyUsers >= 2 && ingameUsers == 1) {
                message = this.gameContainer.textManager.getGame("LobbyChat_UsersInThisLobbyX1", lobbyUsers);
            }

            if (lobbyUsers >= 2 && ingameUsers >= 2) {
                message =
                        this.gameContainer.textManager.getGame("LobbyChat_UsersInThisLobbyXX", lobbyUsers, ingameUsers);
            }

            super.chatTextArea.addPlainMessage(message);
            message = null;
            if (this.lobbyId == 1 && (dualLobbyUsers >= 1 || multiLobbyUsers >= 1)) {
                message = "(";
                if (dualLobbyUsers == 1) {
                    message = message + this.gameContainer.textManager.getGame("LobbyChat_UsersInDualPlayerLobby1");
                }

                if (dualLobbyUsers >= 2) {
                    message = message
                            + this.gameContainer.textManager.getGame(
                                    "LobbyChat_UsersInDualPlayerLobbyX", dualLobbyUsers);
                }

                if (dualLobbyUsers >= 1 && multiLobbyUsers >= 1) {
                    message = message + ", ";
                }

                if (multiLobbyUsers == 1) {
                    message = message + this.gameContainer.textManager.getGame("LobbyChat_UsersInMultiPlayerLobby1");
                }

                if (multiLobbyUsers >= 2) {
                    message = message
                            + this.gameContainer.textManager.getGame(
                                    "LobbyChat_UsersInMultiPlayerLobbyX", multiLobbyUsers);
                }

                message = message + ")";
            }

            if (this.lobbyId == 2 && (singleLobbyUsers >= 1 || multiLobbyUsers >= 1)) {
                message = "(";
                if (singleLobbyUsers == 1) {
                    message = message + this.gameContainer.textManager.getGame("LobbyChat_UsersInSinglePlayerLobby1");
                }

                if (singleLobbyUsers >= 2) {
                    message = message
                            + this.gameContainer.textManager.getGame(
                                    "LobbyChat_UsersInSinglePlayerLobbyX", singleLobbyUsers);
                }

                if (singleLobbyUsers >= 1 && multiLobbyUsers >= 1) {
                    message = message + ", ";
                }

                if (multiLobbyUsers == 1) {
                    message = message + this.gameContainer.textManager.getGame("LobbyChat_UsersInMultiPlayerLobby1");
                }

                if (multiLobbyUsers >= 2) {
                    message = message
                            + this.gameContainer.textManager.getGame(
                                    "LobbyChat_UsersInMultiPlayerLobbyX", multiLobbyUsers);
                }

                message = message + ")";
            }

            if (this.lobbyId == 3 && (singleLobbyUsers >= 1 || dualLobbyUsers >= 1)) {
                message = "(";
                if (singleLobbyUsers == 1) {
                    message = message + this.gameContainer.textManager.getGame("LobbyChat_UsersInSinglePlayerLobby1");
                }

                if (singleLobbyUsers >= 2) {
                    message = message
                            + this.gameContainer.textManager.getGame(
                                    "LobbyChat_UsersInSinglePlayerLobbyX", singleLobbyUsers);
                }

                if (singleLobbyUsers >= 1 && dualLobbyUsers >= 1) {
                    message = message + ", ";
                }

                if (dualLobbyUsers == 1) {
                    message = message + this.gameContainer.textManager.getGame("LobbyChat_UsersInDualPlayerLobby1");
                }

                if (dualLobbyUsers >= 2) {
                    message = message
                            + this.gameContainer.textManager.getGame(
                                    "LobbyChat_UsersInDualPlayerLobbyX", dualLobbyUsers);
                }

                message = message + ")";
            }

            if (message != null) {
                super.chatTextArea.addPlainMessage(message);
            }

            super.chatTextArea.addText();
            return true;
        } else if (args[1].equals("users")) {
            int i = args.length - 2;
            String[] users = new String[i];

            System.arraycopy(args, 2, users, 0, i);

            this.setUserList(users);
            return true;
        } else if (args[1].equals("ownjoin")) {
            this.localUserJoin(args[2]);
            return true;
        } else if (args[1].equals("join") || args[1].equals("joinfromgame")) {
            String userData = this.userJoin(args[2]);
            if (!this.isNoJoinPartMessages()) {
                super.chatTextArea.addJoinMessage(this.gameContainer.textManager.getGame(
                        "LobbyChat_User" + (args[1].equals("join") ? "Joined" : "ReturnedFromGame"), userData));
            }

            return true;
        } else if (args[1].equals("part")) {
            this.userLeft(args[2]);
            int reason = Integer.parseInt(args[3]);
            if (reason == 1 && this.lobbyId == 1) {
                if (!this.isNoGameMessages()) {
                    super.chatTextArea.addMessage(
                            this.gameContainer.textManager.getGame("LobbyChat_UserStartedSp", args[2]));
                }

                return true;
            } else if (reason == 2 || reason == 3) {
                if (!this.isNoGameMessages()) {
                    String[] reasons = new String[] {null, null, "CreatedMp", "JoinedMp"};
                    String var5;
                    if (args.length == 4) {
                        var5 = this.gameContainer.textManager.getGame("LobbyChat_User" + reasons[reason], args[2]);
                    } else {
                        var5 = this.gameContainer.textManager.getGame(
                                "LobbyChat_User" + reasons[reason], args[2], args[4]);
                    }

                    super.chatTextArea.addMessage(var5);
                }

                return true;
            } else if (reason >= 4) {
                if (!this.isNoJoinPartMessages()) {
                    super.chatTextArea.addPartMessage(this.gameContainer.textManager.getGame(
                            "LobbyChat_UserLeft" + (reason == 5 ? "ConnectionProblem" : ""), args[2]));
                }

                return true;
            } else {
                return true;
            }
        } else if (args[1].equals("gsn")) {
            if (!this.isNoGameMessages()) {
                super.chatTextArea.addMessage(
                        this.gameContainer.textManager.getGame("LobbyChat_UsersStartedDp", args[2], args[3]));
            }

            return true;
        } else if (args[1].equals("say")) {
            this.userSay(Integer.parseInt(args[2]), args[3], args[4]);
            return true;
        } else if (args[1].equals("sayp")) {
            this.userSayPrivately(args[2], args[3]);
            return true;
        } else if (args[1].equals("sheriffsay")) {
            this.sheriffSay(args[2]);
            return true;
        } else if (args[1].equals("serversay")) {
            this.serverSay(args[2]);
            return true;
        } else if (args[1].equals("nc")) {
            this.getUser(args[2], args[3].equals("t"));
            return true;
        }
        return false;
    }

    protected void getUser(String name, boolean var2) {
        User var3;
        if (name != null) {
            var3 = super.userList.getUser(name);
            if (var3 == null) {
                return;
            }
        } else {
            var3 = super.userList.getLocalUser();
        }

        super.userList.setNotAcceptingChallenges(var3, var2);
    }
}
