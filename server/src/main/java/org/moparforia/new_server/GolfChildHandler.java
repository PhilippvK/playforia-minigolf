package org.moparforia.new_server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.moparforia.new_server.handlers.ClientInitializerGroup;
import org.moparforia.new_server.handlers.ServerHandlerGroup;
import org.moparforia.shared.networking.PacketHandler;
import org.moparforia.shared.networking.packets.PingPacket;

public class GolfChildHandler extends ServerPacketHandler {

    private final GolfServer server;

    public GolfChildHandler(GolfServer golfServer) {
        this.server = golfServer;
        this.register("ping", PingPacket.class, ((packet, ctx, handler) -> logger.info("Received ping")));
        this.registerGroup(new ClientInitializerGroup(server));
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE || e.state() == IdleState.ALL_IDLE) {
                logger.info("Closing due to no inbound packet");
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(new PingPacket());
                logger.info("Writing Ping packet");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.disconnect();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Player player = new Player(ctx, this);
        server.addPlayer(player);
        logger.info("User connected");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Player player = new Player(ctx, this);
        this.disconnect(player);
        server.removePlayer(player);
        logger.info("User disconnected");
        super.channelInactive(ctx);
    }
}