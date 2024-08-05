package org.moparforia.server.net.packethandlers;

import org.moparforia.server.Server;
import org.moparforia.server.game.Lobby;
import org.moparforia.server.game.Player;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketHandler;
import org.moparforia.server.net.PacketType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * QUITTEN TITTENS HANDLERING IN HERE
 */
public class QuitHandler implements PacketHandler {
    @Override
    public PacketType getType() {
        return PacketType.DATA;
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("(lobby\\t)?quit");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        Player player = packet.getChannel().attr(Player.PLAYER_ATTRIBUTE_KEY).get();
        if (message.group(1).contains("lobby")) {
            player.getLobby().removePlayer(player, Lobby.PART_REASON_USERLEFT);
        }
        packet.getChannel().disconnect();
        packet.getChannel().close();
        return true;
    }
}
