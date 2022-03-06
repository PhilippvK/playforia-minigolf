package org.moparforia.shared.game;

import org.moparforia.shared.tracks.TrackCategory;

public enum WeightEnd {
    NO(0),
    LITTLE(1),
    PLENTY(2);

    private int id;
    WeightEnd(int i) {
        this.id = i;
    }

    public int getId() {
        return id;
    }

    public static WeightEnd getByTypeId(int id) {
        for (WeightEnd type : WeightEnd.values())
            if (type.getId() == id)
                return type;

        return null;
    }
}
