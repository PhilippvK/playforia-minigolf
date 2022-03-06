package org.moparforia.shared.networking;

import io.netty.channel.ChannelHandlerContext;

public class PacketReceivePair<T extends Packet> {
    private final PacketReceiveHandler<T> receiveHandler;
    private final Class<T> clazz;

    public PacketReceivePair(PacketReceiveHandler<T> handler, Class<T> clazz) {
        this.receiveHandler = handler;
        this.clazz = clazz;
    }

    public PacketReceiveHandler<T> getReceiveHandler() {
        return receiveHandler;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public boolean checkType(Packet packet) {
        return clazz.isInstance(packet);
    }

    public void invoke(Packet packet, ChannelHandlerContext ctx, PacketHandler handler) {
        receiveHandler.receive(clazz.cast(packet), ctx, handler);
    }
}
