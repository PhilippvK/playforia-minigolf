package org.moparforia.server.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        Channel channel = ctx.channel();
        Packet packet = new Packet(channel, in.readBytes(in.readableBytes()).toString(CharsetUtil.UTF_8));
        if (packet.getType() == PacketType.DATA) {
            long count = channel.attr(ClientState.CLIENT_STATE_ATTRIBUTE_KEY).get().getReceivedCount();
            if (count == packet.getCount()) {
                channel.attr(ClientState.CLIENT_STATE_ATTRIBUTE_KEY).get().setReceivedCount(count + 1);
            } else {
                channel.close();
            }
        }
        out.add(packet);
    }
}
