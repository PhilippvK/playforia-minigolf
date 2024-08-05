package org.moparforia.server.net.packethandlers.golf;

import org.moparforia.server.Server;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketHandler;
import org.moparforia.server.net.PacketType;
import org.moparforia.shared.Tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Playforia
 * 11.6.2013
 */
public class LoginTypeHandler implements PacketHandler {
    @Override
    public PacketType getType() {
        return PacketType.DATA;
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("logintype\\t(nr|reg|ttm)");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        packet.getChannel().writeAndFlush(new Packet(PacketType.DATA, Tools.tabularize("status", "login")));
        return true;
    }
}
