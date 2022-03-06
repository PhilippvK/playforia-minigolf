package org.moparforia.shared.networking.packets;

import org.moparforia.shared.networking.Packet;

public class SimplePacket extends Packet {
    private final String string;

    public SimplePacket(String header, String string) {
        super(header);
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
