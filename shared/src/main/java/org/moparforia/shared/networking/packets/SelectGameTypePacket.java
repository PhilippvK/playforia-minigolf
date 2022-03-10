package org.moparforia.shared.networking.packets;

import org.moparforia.shared.game.LobbyType;
import org.moparforia.shared.networking.Packet;
import org.moparforia.shared.networking.packets.headers.PacketHeaders;

public class SelectGameTypePacket extends Packet {
    private final LobbyType type;

    public SelectGameTypePacket(LobbyType type) {
        super(PacketHeaders.SELECT_GAME_TYPE);
        this.type = type;
    }

    public LobbyType getType() {
        return type;
    }
}
