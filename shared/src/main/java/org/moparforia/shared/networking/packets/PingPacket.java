package org.moparforia.shared.networking.packets;

import org.moparforia.shared.networking.Packet;
import org.moparforia.shared.networking.packets.headers.PacketHeaders;

public class PingPacket extends Packet {
    public PingPacket() {
        super(PacketHeaders.PING);
    }
}
