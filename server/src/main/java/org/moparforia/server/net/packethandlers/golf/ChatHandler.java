package org.moparforia.server.net.packethandlers.golf;

import org.moparforia.server.Server;
import org.moparforia.server.game.Game;
import org.moparforia.server.game.Player;
import org.moparforia.server.game.PlayerCollection;
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
public class ChatHandler implements PacketHandler {
    @Override
    public PacketType getType() {
        return PacketType.DATA;
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("(lobby|game)\\t(say|sayp|command)\\t(.+?)(?:\\t(.+))?");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        Player player = packet.getChannel().attr(Player.PLAYER_ATTRIBUTE_KEY).get();
        PlayerCollection destination;
        if (message.group(1).equals("game")) {
            destination = player.getGame();
        } else if (message.group(1).equals("lobby")) {
            destination = player.getLobby();
        } else {
            packet.getChannel().close();
            return true;
        }
        if (message.group(2).equals("say")) {
            for (Player otherPlayer : destination.getPlayers()) {
                if (player != otherPlayer) {
                    if (message.group(1).equals("game")) {
                        otherPlayer.getChannel().writeAndFlush(new Packet(PacketType.DATA,
                                Tools.tabularize("game", "say", ((Game) destination).getPlayerId(player), message.group(3))));
                    } else {
                        otherPlayer.getChannel().writeAndFlush(new Packet(PacketType.DATA,
                                Tools.tabularize("lobby", "say", message.group(3), player.getNick(), message.group(4))));
                    }
                }
            }
        } else if (message.group(2).equals("sayp")) {
            for (Player otherPlayer : destination.getPlayers()) {
                if (otherPlayer.getNick().equals(message.group(3))) {
                    otherPlayer.getChannel().writeAndFlush(new Packet(PacketType.DATA,
                            Tools.tabularize(message.group(1), "sayp", player.getNick(), message.group(4))));
                    break;
                }
            }
        } else if (message.group(2).equals("command")) {
            //todo
        }
        return true;
    }
}
