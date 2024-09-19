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

public class EndHandler implements PacketHandler {

    @Override
    public PacketType getType() {
        return PacketType.COMMAND;
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("end");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        return true;
    }

}
