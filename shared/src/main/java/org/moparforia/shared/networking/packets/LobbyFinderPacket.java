package org.moparforia.shared.networking.packets;

import org.moparforia.shared.game.Lobby;
import org.moparforia.shared.networking.Packet;
import org.moparforia.shared.networking.packets.headers.HeaderProvider;

import java.util.List;

public class LobbyFinderPacket extends Packet {
    private final List<Lobby> lobbies;
    private final List<String> users;

    public LobbyFinderPacket(HeaderProvider header, List<Lobby> lobbies, List<String> users) {
        super(header);
        this.lobbies = lobbies;
        this.users = users;
    }

    public List<Lobby> getLobbies() {
        return lobbies;
    }

    public List<String> getUsers() {
        return users;
    }
}
