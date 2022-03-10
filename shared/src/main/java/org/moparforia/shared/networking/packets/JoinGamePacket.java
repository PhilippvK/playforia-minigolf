package org.moparforia.shared.networking.packets;

import org.moparforia.shared.networking.Packet;
import org.moparforia.shared.networking.packets.headers.HeaderProvider;
import org.moparforia.shared.networking.packets.headers.PacketHeaders;

import java.util.Optional;

public class JoinGamePacket extends Packet {
    private final String password;
    private final String lobbyName;

    public JoinGamePacket(String lobbyName, String password) {
        super(PacketHeaders.JOIN_LOBBY);
        this.password = password;
        this.lobbyName = lobbyName;
    }

    public JoinGamePacket(String lobbyName) {
        this(lobbyName, null);
    }


    public Optional<String> getPassword() {
        return Optional.of(password);
    }

    public String getLobbyName() {
        return lobbyName;
    }
}
