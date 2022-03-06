package org.moparforia.client.networking;


import com.aapeli.applet.AApplet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.moparforia.client.networking.handlers.InitialHandlerGroup;
import org.moparforia.shared.networking.PacketHandler;
import org.moparforia.shared.networking.packets.headers.PacketHeaders;
import org.moparforia.shared.networking.packets.PingPacket;

/**
 * Handler implementation for the object echo client.  It initiates the
 * ping-pong traffic between the object echo client and server by sending the
 * first message to the server.
 */
public class ClientHandler extends PacketHandler {
    private final Client client;

    public ClientHandler(Client client) {
        this.client = client;
        this.register(PacketHeaders.PING, PingPacket.class, ((packet, ctx, handler) -> {
            logger.info("Received ping");
            ctx.writeAndFlush(packet);
        }));
        this.registerGroup(new InitialHandlerGroup(client));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // Send the first message if this handler is a client-side handler.
//        ChannelFuture future = ctx.writeAndFlush(firstMessage);
//        ChannelFuture future = ctx.writeAndFlush(new SimplePacket("test", "xyz"));
//        future.addListener(FIRE_EXCEPTION_ON_FAILURE); // Let object serialisation exceptions propagate.
//        client.getApplet().setGameState(1);
        client.setConnected(true);
        client.getApplet().setGameState(0);
        super.channelActive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                logger.severe("Disconnecting due to no inbound traffic");
                ctx.close();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        client.getApplet().setEndState(AApplet.END_ERROR_CONNECTION);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
