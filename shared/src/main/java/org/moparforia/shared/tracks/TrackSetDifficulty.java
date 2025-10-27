package org.moparforia.shared.tracks;

public enum TrackSetDifficulty {
    EASY(1),
    MEDIUM(2),
    HARD(3);

    private final int id;

    private TrackSetDifficulty(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TrackSetDifficulty fromId(int id) {
        for (TrackSetDifficulty difficulty : TrackSetDifficulty.values()) {
            if (difficulty.getId() == id) {
                return difficulty;
            }
        }
        throw new IllegalArgumentException("No TrackSetDifficulty with id  '" + id + "' found");
    }
}
