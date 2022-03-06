package org.moparforia.shared.networking.packets;

import org.moparforia.shared.networking.Packet;
import org.moparforia.shared.networking.packets.headers.HeaderProvider;

public class PlayerPacket extends Packet {
    private final String player;

    public PlayerPacket(HeaderProvider header, String player) {
        super(header);
        this.player = player;
    }


    public String getPlayer() {
        return player;
    }
}
