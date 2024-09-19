package org.moparforia.server.game.gametypes.golf;

import org.moparforia.shared.tracks.Track;
import org.moparforia.server.game.Lobby;
import org.moparforia.server.game.LobbyType;
import org.moparforia.server.game.Player;
import org.moparforia.server.game.gametypes.GolfGame;
import org.moparforia.shared.tracks.filesystem.FileSystemTrackManager;

import java.util.List;

public class ChampionshipGame extends GolfGame {

    private int championshipId;

    public ChampionshipGame(Player p, int gameId, int championshipId) {
        super(gameId, LobbyType.SINGLE, "derp", null, false, 10,
                PERM_EVERYONE, 0, STROKES_UNLIMITED, STROKETIMEOUT_INFINITE,
                WATER_START, COLLSION_YES, SCORING_STROKE, SCORING_WEIGHT_END_NONE, 1);
        this.championshipId = championshipId;
        tracks = initTracks();

        Lobby lob = p.getLobby();
        if (addPlayer(p)) {
            lob.addGame(this);
            startGame();
        } else {
            //todo: if adding da player failed init!!
        }
    }

    public List<Track> initTracks() {
        List<Track> tracks = FileSystemTrackManager.getInstance().getTrackSet(championshipId);
        this.numberOfTracks = tracks.size(); // important we set this depending on set.
        return tracks;
    }
}
