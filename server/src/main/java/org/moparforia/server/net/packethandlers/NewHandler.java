package org.moparforia.server.net.packethandlers;

import io.netty.channel.Channel;
import org.moparforia.server.Server;
import org.moparforia.server.event.PlayerConnectedEvent;
import org.moparforia.server.game.Player;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketHandler;
import org.moparforia.server.net.PacketType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewHandler implements PacketHandler {

    @Override
    public PacketType getType() {
        return PacketType.COMMAND;
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("new");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        Channel channel = packet.getChannel();
        int id = server.getNextPlayerId();
        Player player = new Player(channel, id);
        channel.attr(Player.PLAYER_ATTRIBUTE_KEY).set(player);
        server.addPlayer(player);
        channel.writeAndFlush("c id " + id + "\n");
        server.addEvent(new PlayerConnectedEvent(player.getId(), false));
        return true;
    }

}
