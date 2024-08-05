package org.moparforia.server.net.packethandlers.golf;

import org.moparforia.server.Server;
import org.moparforia.server.game.Player;
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
public class LoginHandler implements PacketHandler {
    @Override
    public PacketType getType() {
        return PacketType.DATA;
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("login");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        Player player = packet.getChannel().attr(Player.PLAYER_ATTRIBUTE_KEY).get();


        String username = "~anonym-" + (int) (Math.random() * 10000);

        player.setNick(username);
        player.setEmailVerified(true);
        player.setRegistered(false);

        packet.getChannel().writeAndFlush(new Packet(PacketType.DATA, Tools.tabularize("basicinfo", player.isEmailVerified(), player.getAccessLevel(), "t", "t")));
        packet.getChannel().writeAndFlush(new Packet(PacketType.DATA, Tools.tabularize("status", "lobbyselect", "300")));
        return true;
    }
}
