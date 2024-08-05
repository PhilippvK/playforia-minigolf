package org.moparforia.server.net.packethandlers.golf;

import org.moparforia.server.Server;
//import org.moparforia.server.db.Database;
import org.moparforia.server.game.Player;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketHandler;
import org.moparforia.server.net.PacketType;
import org.moparforia.shared.Tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Playforia
 * 13.6.2013
 */
public class TrackTestLoginHandler implements PacketHandler {
    Pattern namePattern;

    public TrackTestLoginHandler() {
        namePattern = Pattern.compile("[^ -~]");
    }

    @Override
    public PacketType getType() {
        return PacketType.DATA;
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("ttlogin\\t(.*)\\t(.*)");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        String username = message.group(1);
        username = username.trim();
        if (namePattern.matcher(username).find()) {
            return false;//todo disconnect client
        }
        String password = message.group(2);
        //todo load player from db?

        boolean anonym = true; //!Database.getInstance().authenticateUser(username,password);
        if (anonym) {
            if (username.length() == 0) {
                username = "~anonym-" + (int) (Math.random() * 10000);
                // Warning: Currently duplicates wont get noticed...
            }
        }

        Player player = packet.getChannel().attr(Player.PLAYER_ATTRIBUTE_KEY).get();
        player.setNick(username);
        player.setEmailVerified(true);
        player.setRegistered(!anonym);
        packet.getChannel().writeAndFlush(new Packet(PacketType.DATA, Tools.tabularize("basicinfo", player.isEmailVerified(), player.getAccessLevel(), "t", "f")));
        packet.getChannel().writeAndFlush(new Packet(PacketType.DATA, Tools.tabularize("status", "lobbyselect", 300)));
        return true;
    }
}
