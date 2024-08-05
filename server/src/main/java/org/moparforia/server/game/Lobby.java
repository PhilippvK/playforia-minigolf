package org.moparforia.server.game;

import org.moparforia.server.game.gametypes.golf.MultiGame;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketType;
import org.moparforia.shared.Tools;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Playforia
 * 11.6.2013
 */
public class Lobby extends PlayerCollection {

    public static final int PART_REASON_STARTED_SP = 1; // todo: enum this ?
    public static final int PART_REASON_CREATED_MP = 2;
    public static final int PART_REASON_JOINED_MP = 3;
    public static final int PART_REASON_USERLEFT = 4;
    public static final int PART_REASON_CONN_PROBLEM = 5;
    public static final int PART_REASON_SWITCHEDLOBBY = 6;
    public static final int JOIN_TYPE_NORMAL = 0;
    public static final int JOIN_TYPE_FROMGAME = 1;

    private final LobbyType type;
    private HashMap<Integer, Game> games = new HashMap<Integer, Game>();

    public Lobby(LobbyType type) {
        this.type = type;
    }

    public LobbyType getLobbyType() {
        return type;
    }

    public boolean removePlayer(Player player, int partReason, String... gameName) {
        if (!super.removePlayer(player))
            return false;

        String cmd = Tools.tabularize("lobby", "part", player.getNick(), partReason);
        if (partReason == PART_REASON_JOINED_MP && gameName != null && gameName.length == 1)
            cmd += '\t' + gameName[0];

        writeAll(new Packet(PacketType.DATA, cmd));

        if (player.getChannel().isWritable() && partReason == PART_REASON_USERLEFT) {
            player.getChannel().writeAndFlush(new Packet(PacketType.DATA, "status\tlobbyselect\t300"));
        }
        return true;
    }

    public boolean addPlayer(Player player, int joinType) {
        if (player.getLobby() != null) {
            player.getLobby().removePlayer(player, PART_REASON_USERLEFT);
        }
        player.getChannel().writeAndFlush(new Packet(PacketType.DATA, Tools.tabularize("status", "lobby", type + (player.isChatHidden() ? "h" : ""))));
        String[] otherPlayers = new String[playerCount()];
        int pointer = 0;

        for (Player p : getPlayers()) {
            p.getChannel().writeAndFlush(new Packet(PacketType.DATA, Tools.tabularize(
                    "lobby", joinType == JOIN_TYPE_NORMAL ? "join" : "joinfromgame", player.toString()//todo not sure if should be getNick or getGameString
            )));
            otherPlayers[pointer++] = p.toString();
        }
        if (pointer != 0) {
            player.getChannel().writeAndFlush(new Packet(PacketType.DATA, Tools.tabularize("lobby", "users", otherPlayers)));
        } else {
            player.getChannel().writeAndFlush(new Packet(PacketType.DATA, Tools.tabularize("lobby", "users")));
        }

        player.getChannel().writeAndFlush(new Packet(PacketType.DATA, Tools.tabularize("lobby", "ownjoin", player.toString())));
        if (getLobbyType() == LobbyType.MULTI) {
            sendGameList(player);
        }
        player.setLobby(this);
        return super.addPlayer(player);
    }

    public void sendGameList(Player player) {
        ArrayList<Game> gamez = new ArrayList<Game>(games.values());
        StringBuffer buff = new StringBuffer();
        int length = 0;
        for (int i = 0; i < gamez.size(); i++) {
            MultiGame g = (MultiGame) gamez.get(i);
            if (g.isPublic()) {
                buff.append(g.getGameString() + "\t"); // DONT USE TOSTRING FAGGOT
                length++;
            }
        }
        player.getChannel().writeAndFlush(new Packet(PacketType.DATA, Tools.tabularize("lobby", "gamelist", "full", length, buff.toString())));
    }

    public Game getGame(int id) {
        return games.get(id);
    }

    public boolean gameExists(int id) {
        return games.containsKey(id);
    }

    public boolean addGame(Game g) {
        if (gameExists(g.getGameId())) {
            return false; // cant add a game that already exists bro!!1
        }
        games.put(g.getGameId(), g);
        return true;
    }

    public boolean removeGame(int gameId) {
        if (!games.containsKey(gameId)) {
            return false; // no such GAME EVER EXISTED IDIET
        }
        Game g = games.get(gameId);
        for (Player p : g.getPlayers()) {
            g.removePlayer(p);
        }
        games.remove(gameId);
        return true;
    }

    public boolean removeGame(Game g) {
        return removeGame(g.getGameId());
    }

    /**
     * counts all the players errywhar
     * @return number of players in the lobby and in all games created from this lobby.
     */
    public int totalPlayerCount() {
        int inGames = 0;
        for(Game g: games.values()) {
            inGames += g.playerCount();
        }
        return playerCount() + inGames;
    }


    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Lobby && ((Lobby) obj).getLobbyType() == this.getLobbyType();
    }
}
