package org.moparforia.shared.networking.packets;

import org.moparforia.shared.networking.Packet;
import org.moparforia.shared.networking.packets.headers.PacketHeaders;

public class InitialPacket extends Packet {
    private final String name;
    private final String version;
    private final boolean cheatsEnabled;

    public InitialPacket(String name, String version, boolean cheatsEnabled) {
        super(PacketHeaders.INITIAL_INFO);
        this.name = name;
        this.version = version;
        this.cheatsEnabled = cheatsEnabled;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public boolean isCheatsEnabled() {
        return cheatsEnabled;
    }
}
