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

public class ReconnectHandler implements PacketHandler {

    @Override
    public PacketType getType() {
        return PacketType.COMMAND;
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("old ([\\-\\d]+)");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        int id = Integer.valueOf(message.group(1));
        if (!server.hasPlayer(id))
            packet.getChannel().close();
        else {
            Player p = server.getPlayer(id);
            Channel c = packet.getChannel();
            p.setChannel(c);
            c.attr(Player.PLAYER_ATTRIBUTE_KEY).set(p);
            c.writeAndFlush("c rcok\n");
            server.addEvent(new PlayerConnectedEvent(id, true));
        }
        return true;
    }

}
