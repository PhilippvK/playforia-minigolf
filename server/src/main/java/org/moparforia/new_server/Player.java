package org.moparforia.new_server;

import io.netty.channel.ChannelHandlerContext;
import org.moparforia.shared.networking.Packet;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

public class Player {
    private final ChannelHandlerContext ctx;
    private final GolfChildHandler handler;
    private String nick;private boolean isLoggedIn = false;

    public Player(ChannelHandlerContext ctx, GolfChildHandler handler) {
        this.handler = handler;
        this.ctx = ctx;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void send(Packet packet) {
        ctx.writeAndFlush(packet).addListener(FIRE_EXCEPTION_ON_FAILURE);
    }
}
