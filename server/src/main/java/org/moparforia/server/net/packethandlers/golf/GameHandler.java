package org.moparforia.server.net.packethandlers.golf;

import org.moparforia.server.Server;
import org.moparforia.server.game.Game;
import org.moparforia.server.game.Player;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketHandler;
import org.moparforia.server.net.PacketType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Playforia
 * 12.6.2013
 */
public class GameHandler implements PacketHandler {
    @Override
    public PacketType getType() {
        return PacketType.DATA;
    }

    @Override
    public Pattern getPattern() {
        // return Pattern.compile("game\\t(beginstroke|endstroke|voteskip|skip|newgame|back)(?:\\t([0-9a-z]{4}|[0-9]))?(?:\\t)?([ft]+)?");
        return Pattern.compile("game\\t(.+?)(?:\\t([0-9a-z]{4}|[0-9]))?(?:\\t)?([ftp]+)?");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        Player player = packet.getChannel().attr(Player.PLAYER_ATTRIBUTE_KEY).get();
        Game game = player.getGame();
        return game.handlePacket(server, player, message);
    }
}
