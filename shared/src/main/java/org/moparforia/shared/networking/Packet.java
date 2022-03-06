package org.moparforia.shared.networking;

import org.moparforia.shared.networking.packets.headers.HeaderProvider;
import org.moparforia.shared.networking.packets.headers.PacketHeaders;

import java.io.Serializable;

public class Packet implements Serializable {
    private final String header;

    public Packet(String header) {
        this.header = header;
    }

    public Packet(HeaderProvider header) {
        this(header.getHeader());
    }

    public String getHeader() {
        return header;
    }
}
