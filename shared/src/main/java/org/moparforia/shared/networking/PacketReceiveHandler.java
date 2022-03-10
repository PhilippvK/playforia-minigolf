package org.moparforia.shared.networking;

import io.netty.channel.ChannelHandlerContext;

public interface PacketReceiveHandler<T extends Packet> {
    void receive(T packet, ChannelHandlerContext ctx, PacketHandler handler);
}
