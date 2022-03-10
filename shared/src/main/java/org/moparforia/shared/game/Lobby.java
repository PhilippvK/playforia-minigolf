package org.moparforia.shared.game;

import org.moparforia.shared.tracks.TrackCategory;

import java.io.Serializable;
import java.util.List;

public class Lobby implements Serializable {
    private final List<String> players;
    private final boolean started;
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

    public Lobby(List<String> players, boolean started, boolean hasPassword, String name, Integer maxPlayers,
                 Integer numTracks, TrackCategory trackType, Integer maxStrokes, Integer timeLimit,
                 boolean waterOnStart, boolean collision, boolean strokeScoring, WeightEnd scoringEnd) {
        this.players = players;
        this.started = started;
        this.hasPassword = hasPassword;
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
    }

    public List<String> getPlayers() {
        return players;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean hasPassword() {
        return hasPassword;
    }

    public String getName() {
        return name;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public Integer getNumTracks() {
        return numTracks;
    }

    public TrackCategory getTrackType() {
        return trackType;
    }

    public Integer getMaxStrokes() {
        return maxStrokes;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public boolean waterOnStart() {
        return waterOnStart;
    }

    public boolean hasCollision() {
        return collision;
    }

    public WeightEnd getScoringEnd() {
        return scoringEnd;
    }

    public boolean isStrokeScoring() {
        return strokeScoring;
    }

}
