package org.moparforia.server.game;

import org.moparforia.shared.game.LobbyType;

/**
 * Playforia
 * 24.6.2013
 */
public class LobbyRef {

    private GameType gameType;
    private LobbyType lobbyType;
    private Lobby lobby;

    public LobbyRef(GameType gameType, LobbyType lobbyType, Lobby lobby) {
        this.gameType = gameType;
        this.lobbyType = lobbyType;
        this.lobby = lobby;
    }

    public GameType getGameType() {
        return gameType;
    }

    public LobbyType getLobbyType() {
        return lobbyType;
    }

    public Lobby getLobby() {
        return lobby;
    }
}
