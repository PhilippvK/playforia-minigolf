package org.moparforia.server.net.packethandlers.golf;

import org.moparforia.server.Server;
import org.moparforia.server.game.Player;
import org.moparforia.server.game.gametypes.golf.ChampionshipGame;
import org.moparforia.server.game.gametypes.golf.TrainingGame;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketHandler;
import org.moparforia.server.net.PacketType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Playforia
 * 13.6.2013
 */
public class LobbyCreateSinglePlayerHandler implements PacketHandler {
    @Override
    public PacketType getType() {
        return PacketType.DATA;
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("lobby\\tcsp(t|c)\\t(\\d+)(?:\\t(\\d+)\\t(\\d+))?");
    }                 //CLIENT> WRITE "d 5 lobby	cspt	10	7	0"

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        Player player = packet.getChannel().attr(Player.PLAYER_ATTRIBUTE_KEY).get();
        int number = Integer.parseInt(message.group(2));
        if (message.group(1).equals("t")) {
            int trackType = Integer.parseInt(message.group(3));
            int water = Integer.parseInt(message.group(4));
            new TrainingGame(player, server.getNextGameId(), trackType, number, water);

        } else if (message.group(1).equals("c")) {
            new ChampionshipGame(player, server.getNextGameId(), number);
        } else {
            return false;
        }
        return true;
    }
}
