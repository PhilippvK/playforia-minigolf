package org.moparforia.server.event;

import io.netty.channel.Channel;
import org.moparforia.server.Server;

public class ClientConnectedEvent extends Event {

    private final Channel channel;

    public ClientConnectedEvent(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void process(Server server) {
        System.out.println("Client connected: " + channel);
        server.addChannel(channel);
        channel.writeAndFlush("h 1\nc crt 250\nc ctr\n");
    }
}
