package org.moparforia.server.net.packethandlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.moparforia.server.Server;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketHandler;
import org.moparforia.server.net.PacketType;

public class PongHandler implements PacketHandler {

    @Override
    public PacketType getType() {
        return PacketType.COMMAND;
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("pong");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        return true;
    }
}
