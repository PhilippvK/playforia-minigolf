package org.moparforia.shared.networking.packets;

import org.moparforia.shared.game.Lobby;
import org.moparforia.shared.networking.Packet;
import org.moparforia.shared.networking.packets.headers.PacketHeaders;

public class CreateLobbyPacket extends Packet {
    private final Lobby lobby;
    private final String password;

    public CreateLobbyPacket(Lobby lobby, String password) {
        super(PacketHeaders.CREATE_LOBBY);
        this.lobby = lobby;
        this.password = password;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public String getPassword() {
        return password;
    }
}
