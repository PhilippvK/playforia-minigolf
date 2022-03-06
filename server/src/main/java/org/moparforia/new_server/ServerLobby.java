package org.moparforia.new_server;

import org.moparforia.shared.game.Lobby;
import org.moparforia.shared.game.WeightEnd;
import org.moparforia.shared.tracks.TrackCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Intentionally didnt make this a subclass of Lobby as that is network transferred object
 */
public class ServerLobby {
    private final List<String> players = new ArrayList<>();
    private final boolean started = false;
    private final boolean hasPassword;
    private final String name;
    private final Integer maxPlayers;
    private final Integer numTracks;
    private final TrackCategory trackType;
    private final Integer maxStrokes;
    private final Integer timeLimit;
    private final boolean waterOnStart;
    private final boolean collision;
    private final boolean strokeScoring;
    private final WeightEnd scoringEnd;
    private String password;

    public ServerLobby(String name, Integer maxPlayers,
                       Integer numTracks, TrackCategory trackType, Integer maxStrokes, Integer timeLimit,
                       boolean waterOnStart, boolean collision, boolean strokeScoring, WeightEnd scoringEnd,
                       String password) {
        this.hasPassword = password != null;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.numTracks = numTracks;
        this.trackType = trackType;
        this.maxStrokes = maxStrokes;
        this.timeLimit = timeLimit;
        this.waterOnStart = waterOnStart;
        this.collision = collision;
        this.strokeScoring = strokeScoring;
        this.scoringEnd = scoringEnd;
        this.password = password;
    }

    public ServerLobby(Lobby lobby, String password) {
        this(lobby.getName(), lobby.getMaxPlayers(), lobby.getNumTracks(), lobby.getTrackType(),
                lobby.getMaxStrokes(), lobby.getTimeLimit(), lobby.waterOnStart(), lobby.hasCollision(),
                lobby.isStrokeScoring(), lobby.getScoringEnd(), password);
    }

    public Lobby toLobby() {
        return new Lobby(new ArrayList<>(), false, password != null, name, maxPlayers,
                numTracks, trackType, maxStrokes, timeLimit, waterOnStart, collision, strokeScoring, scoringEnd);
    }
}
