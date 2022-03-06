package org.moparforia.shared.networking.packets;

import org.moparforia.shared.networking.Packet;
import org.moparforia.shared.networking.packets.headers.PacketHeaders;

public class LobbyPlayersPacket extends Packet {
    private final int multi;
    private final int dual;
    private final int single;

    public LobbyPlayersPacket(PacketHeaders header, int single, int dual, int multi) {
        super(header);
        this.single = single;
        this.dual = dual;
        this.multi = multi;
    }

    public int getMulti() {
        return multi;
    }

    public int getDual() {
        return dual;
    }

    public int getSingle() {
        return single;
    }
}
