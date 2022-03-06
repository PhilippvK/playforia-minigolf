package org.moparforia.shared.networking.packets;

import org.moparforia.shared.game.Lobby;
import org.moparforia.shared.networking.Packet;
import org.moparforia.shared.networking.packets.headers.HeaderProvider;

public class LobbyPacket extends Packet {
    private final Lobby lobby;

    public LobbyPacket(HeaderProvider header, Lobby lobby) {
        super(header);
        this.lobby = lobby;
    }

    public Lobby getLobby() {
        return lobby;
    }
}
