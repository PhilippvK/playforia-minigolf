package org.moparforia.shared.networking;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.moparforia.shared.networking.packets.headers.PacketHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public abstract class PacketHandler extends ChannelDuplexHandler {

    protected static final Logger logger = Logger.getLogger(PacketHandler.class.getName());
    private final Map<String, PacketReceivePair<? extends Packet>> handlers = new HashMap<>();
    private final Map<Class<?>, Set<String>> handlerGroups = new HashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof Packet)) {
            logger.warning("Received message which isn't a packet: " + msg);
            return;
        }
        Packet packet = (Packet) msg;
        if (!handlers.containsKey(packet.getHeader())) {
            logger.severe("Received message with unknown header " + packet.getHeader());
            return;
        }

        PacketReceivePair<? extends Packet> pair = handlers.get(packet.getHeader());
        if (!pair.checkType(packet)) {
            logger.severe("Received message with header " + packet.getHeader()
                    + " with incorrect packet type, got " + msg.getClass() + ",expected " + pair.getClazz());
        }
        pair.invoke(packet, ctx, this);
    }

    public <T extends Packet> T type(Packet packet, Class<T> clz) {
        if (clz.isInstance(packet)) {
            return clz.cast(packet);
        }
        return null;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        super.channelReadComplete(ctx);
    }

    public <T extends Packet> void register(String header, Class<T> clazz, PacketReceiveHandler<T> handler) {
        if (!handlers.containsKey(header)) {
            handlers.put(header, new PacketReceivePair<>(handler, clazz));
        }
    }

    public <T extends Packet> void register(PacketHeaders header, Class<T> clazz, PacketReceiveHandler<T> handler) {
        this.register(header.toString(), clazz, handler);
    }

    public void deregister(String header) {
        handlers.remove(header);
    }

    public void registerGroup(HandlerGroup group) {
        Map<String, PacketReceivePair<? extends Packet>> map = group.register();
        handlerGroups.put(group.getClass(), map.keySet());
        handlers.putAll(map);
    }

    public void deregisterGroup(Class<? extends HandlerGroup> groupClazz) {
        Set<String> headers = handlerGroups.remove(groupClazz);
        handlers.keySet().removeAll(headers);
    }
}
