package org.moparforia.server.game;

public enum LobbyType {
    SINGLE("1"),
    //DUAL("2"),
    MULTI("x");

    private final String type;

    private LobbyType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static LobbyType getLobby(String type) {
        for (LobbyType lt : values()) {
            if (lt.toString().equals(type)) {
                return lt;
            }
        }
        return null;
    }
}
