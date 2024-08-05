package org.moparforia.server.game;

import io.netty.channel.Channel;
import org.moparforia.server.Server;
import org.moparforia.server.game.gametypes.golf.MultiGame;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketType;
import org.moparforia.shared.Tools;

import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 * Playforia
 * 11.6.2013
 */
public abstract class Game extends PlayerCollection {

    protected final int gameId;
    protected final LobbyType lobbyType;
    protected final String name;
    protected final String password;
    protected final boolean passworded;

    public int numberIndex = 0;
    public ArrayList<Integer> playersNumber = new ArrayList<Integer>();

    protected boolean isPublic = true;

    protected int wantsGameCount = 0;
    protected int confirmCount = 0;

    public static final int PERM_EVERYONE = 0;
    public static final int PERM_REGISTERED = 1;
    public static final int PERM_VIP = 2;

    public Game(int gameId, LobbyType lobbyId, String name, String password, boolean passworded) {
        this.gameId = gameId;
        this.lobbyType = lobbyId;
        this.name = name;
        this.password = password;
        this.passworded = passworded;
    }

    protected abstract void startGame();

    protected abstract void sendGameInfo(Player player);

    protected abstract String getGameString();

    protected abstract void reset();

    public abstract boolean handlePacket(Server server, Player player, Matcher message);

    public int getGameId() {
        return gameId;
    }

    public LobbyType getLobbyType() {
        return lobbyType;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean removePlayer(Player player) {
        if (!hasPlayer(player))
            return false;

        writeExcluding(player, new Packet(PacketType.DATA, Tools.tabularize("game", "part", playersNumber.get(getPlayers().indexOf(player)), 4)));
        playersNumber.remove((Integer) getPlayerId(player)); // be wary of this...
        super.removePlayer(player);

        return true;
    }

    public boolean addPlayer(Player player) {
        if (hasPlayer(player))
            return false;
        if (player.getLobby() != null) {
            int reason = Lobby.PART_REASON_STARTED_SP;
            // dont like this bit but important to tell the client how we're leaving lobby.
            if (this instanceof MultiGame) {
                if (playerCount() == 0) {
                    reason = Lobby.PART_REASON_CREATED_MP;
                } else {
                    reason = Lobby.PART_REASON_JOINED_MP;
                }
            }
            player.getLobby().removePlayer(player, reason);
        }
        sendJoinMessages(player);
        super.addPlayer(player);
        playersNumber.add(numberIndex);
        numberIndex++;
        player.setGame(this);
        return true;
    }

    protected void sendJoinMessages(Player player) {
        sendGameInfo(player);
        sendPlayerNames(player);
        writeExcluding(player, new Packet(PacketType.DATA, Tools.tabularize("game", "join", playerCount(), player.getNick(), player.getClan())));
        player.getChannel().writeAndFlush(new Packet(PacketType.DATA, Tools.tabularize("game", "owninfo", numberIndex, player.getNick(), player.getClan())));
    }

    protected void sendPlayerNames(Player player) {
        Channel c = player.getChannel();
        String playersData = Tools.tabularize("game", "players");
        for (Player p : getPlayers()) {
            if (!p.equals(player))
                playersData += Tools.tabularize("", getPlayerId(p), p.getNick(), p.getClan());
        }
        c.writeAndFlush(new Packet(PacketType.DATA, playersData));
    }


    protected int getFirstPlayer() {
        return playersNumber.get(0);
    }

    protected void endGame() {
        writeAll(new Packet(PacketType.DATA, Tools.tabularize("game", "end")));
    }

    public void wantsNewGame(Player p) {
        wantsGameCount++;
        writeExcluding(p, new Packet(PacketType.DATA, Tools.tabularize("game", "rfng", getPlayerId(p))));
        if (wantsGameCount >= getPlayers().size()) {
            wantsGameCount = 0;
            reset();
            startGame();
        }
    }

    public int getPlayerId(Player p) {
        return playersNumber.get(getPlayers().indexOf(p));
    }

    public Player playerForId(int id) {
        return getPlayers().get(id);
    }

    public boolean isEmpty() {
        return getPlayers().size() == 0;
    }

}
