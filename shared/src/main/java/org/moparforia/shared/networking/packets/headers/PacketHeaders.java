package org.moparforia.shared.networking.packets.headers;

import org.moparforia.shared.networking.packets.headers.HeaderProvider;

public enum PacketHeaders implements HeaderProvider {
    INITIAL_INFO("initialInfo"),
    LOGIN_SUCCESSFUL("loginSuccess"),
    LOBBY_PLAYERS("lobbyPlayers"),
    SELECT_GAME_TYPE("selectGameType"),
    JOIN_LOBBY("joinLobby"),
    ADD_LOBBY("addLobby"),
    CREATE_LOBBY("createLobby"),
    REMOVE_LOBBY("removeLobby"),
    UPDATE_LOBBY("updateLobby"),
    LOBBIES("multiLobbies"),
    PING("ping");
    private final String text;

    /**
     * @param text
     */
    PacketHeaders(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String getHeader() {
        return text;
    }
}
