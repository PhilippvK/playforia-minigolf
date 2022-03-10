package org.moparforia.server.game.gametypes.golf;

import org.moparforia.server.game.Lobby;
import org.moparforia.shared.game.LobbyType;
import org.moparforia.server.game.Player;
import org.moparforia.server.game.gametypes.GolfGame;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketType;
import org.moparforia.shared.Tools;
import org.moparforia.shared.tracks.Track;
import org.moparforia.shared.tracks.TrackCategory;

import java.util.List;

/**
 * multiplayerzzzz
 */
public class MultiGame extends GolfGame {


    public MultiGame(Player p, int gameId, String name, String password, int numberOfTracks,
                     int perms, int tracksType, int maxStrokes, int strokeTimeout,
                     int waterEvent, int collision, int trackScoring, int trackScoringEnd,
                     int numPlayers) {

        super(gameId, LobbyType.MULTI, name, password, password.equals("-") || password.equals("") ? false : true,
                numberOfTracks, perms, tracksType, maxStrokes, strokeTimeout,
                waterEvent, collision, trackScoring, trackScoringEnd, numPlayers);

        addPlayer(p, password);
        p.getLobby().writeAll(new Packet(PacketType.DATA, Tools.tabularize("lobby", "gamelist", "add", getGameString())));
        p.getLobby().addGame(this);
    }


    public boolean addPlayer(Player player, String pass) {
        Lobby lobby = player.getLobby();

        if (passworded && (!pass.equals(this.password))) {
            lobby.addPlayer(player, Lobby.JOIN_TYPE_FROMGAME); // LOL YOU GOT THE PASSWORD WRONG BACK TO THE LOBBY U GO
            return false;

        } else { // correct password or no password

            writeAll(new Packet(PacketType.DATA, Tools.tabularize("game", "join", playerCount(), player.getNick(), player.getClan()))); // important this happens before players added.
            super.addPlayer(player);

            if (playerCount() > 1) { // if this is not the first player, update list.
                lobby.writeAll(new Packet(PacketType.DATA, Tools.tabularize("lobby", "gamelist", "change", getGameString())));
            }

            if (numPlayers == playerCount()) { // if game filled up, start!!
                isPublic = false;
                lobby.writeAll(new Packet(PacketType.DATA, Tools.tabularize("lobby", "gamelist", "remove", getGameId())));
                startGame();
            }
            return true;
        }
    }

    public boolean removePlayer(Player player) {
        int id = getPlayerId(player);
        super.removePlayer(player);
        if (playerCount() > 0) {
            if (!isPublic) { // if the game is being played, just pick the first player to shoot.
                writeAll(new Packet(PacketType.DATA, Tools.tabularize("game", "startturn", playersNumber.get(0))));
            }

        } else { // if game is empty, remove from list init!1!1!
            player.getLobby().writeAll(new Packet(PacketType.DATA, Tools.tabularize("lobby", "gamelist", "remove", getGameId())));
        }
        return true;
    }

    @Override
    public List<Track> initTracks() {
        return manager.getRandomTracks(numberOfTracks, TrackCategory.getByTypeId(tracksType));
    }
}
