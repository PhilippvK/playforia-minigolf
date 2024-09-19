package org.moparforia.server.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class PacketEncoder extends MessageToByteEncoder<Object> {

    private final boolean verbose;

    public PacketEncoder(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        Channel channel = ctx.channel();
        if (msg instanceof Packet packet) {
            String encoded;
            if (packet.getType() != PacketType.NONE) {
                encoded = packet.getType().toString().toLowerCase().charAt(0) + " ";
                if (packet.getType() == PacketType.DATA) {
                    long count = channel.attr(ClientState.CLIENT_STATE_ATTRIBUTE_KEY).get().getSentCount();
                    encoded += count + " ";
                    channel.attr(ClientState.CLIENT_STATE_ATTRIBUTE_KEY).get().setSentCount(count + 1);
                }
            } else {
                encoded = "";
            }
            if (this.verbose) {
                System.out.println(">> " + encoded);
            }
            encoded += packet.getMessage() + '\n';
            out.writeBytes(copiedBuffer(encoded, CharsetUtil.UTF_8));
        } else if (msg instanceof String m) {
            if (!m.endsWith("\n")) {
                m += "\n";
            }
            if (m.startsWith("d ")) {
                long count = channel.attr(ClientState.CLIENT_STATE_ATTRIBUTE_KEY).get().getSentCount();
                m = "d " + count + " " + m.substring(2);
                channel.attr(ClientState.CLIENT_STATE_ATTRIBUTE_KEY).get().setSentCount(count + 1);
            }
            if (this.verbose) {
                System.out.println(">>> " + m);
            }
            out.writeBytes(copiedBuffer(m, CharsetUtil.UTF_8));
        } else {
            out.writeBytes((ByteBuf) msg);
        }
    }
}
