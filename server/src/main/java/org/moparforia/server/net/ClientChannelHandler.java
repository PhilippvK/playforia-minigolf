package org.moparforia.server.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.moparforia.server.Server;
import org.moparforia.server.event.*;
import org.moparforia.server.game.Player;

public class ClientChannelHandler extends ChannelDuplexHandler {

    private final Server server;

    public ClientChannelHandler(Server server) {
        this.server = server;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Packet packet = (Packet) msg;
        System.out.println("<<< " + packet);
        ctx.channel().attr(ClientState.CLIENT_STATE_ATTRIBUTE_KEY).get().setLastActivityTime(System.currentTimeMillis());
        server.addEvent(new PacketReceivedEvent(packet));
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent e) {
            if (e.state() == IdleState.READER_IDLE) {
                ClientState state = ctx.channel().attr(ClientState.CLIENT_STATE_ATTRIBUTE_KEY).get();
                long time = System.currentTimeMillis() - state.getLastActivityTime();
                if (time > 20000) {
                    ctx.channel().close();
                } else if (time > 5000) {
                    ctx.channel().writeAndFlush("c ping\n");
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        server.addEvent(new ClientConnectedEvent(ctx.channel()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        server.addEvent(new ClientDisconnectedEvent(channel));
        Player player = channel.attr(Player.PLAYER_ATTRIBUTE_KEY).get();
        if (player != null && server.hasPlayer(player.getId())) {
            int id = player.getId();
            server.addEvent(new TimedEvent(30_000) { // todo: confirm this time
                @Override
                public void process(Server server) {
                    if (server.hasPlayer(id) && !server.getPlayer(id).getChannel().isOpen()) {
                        System.out.println("Player timed-out: " + id);
                        server.addEvent(new PlayerDisconnectEvent(id));
                    }
                }
            });
        }
    }
}
