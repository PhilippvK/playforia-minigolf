package org.moparforia.shared.game;

import java.util.Arrays;

public enum LobbyType {
    SINGLE(1),
    DUAL(2),
    MULTI(3);

    private final int type;

    private LobbyType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return Integer.toString(type);
    }

    public static LobbyType getLobby(int type) {
        for (LobbyType lt : values()) {
            if (lt.type == type) {
                return lt;
            }
        }
        return null;
    }
}
