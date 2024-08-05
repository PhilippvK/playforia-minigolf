package org.moparforia.server.net.packethandlers;

import org.moparforia.server.Server;
import org.moparforia.server.game.GameType;
import org.moparforia.server.game.Player;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketHandler;
import org.moparforia.server.net.PacketType;
import org.moparforia.shared.Tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionHandler implements PacketHandler {

    @Override
    public PacketType getType() {
        return PacketType.DATA;
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("version\\t(\\d+)");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        GameType gameType = GameType.getType(Integer.parseInt(message.group(1)));
        if (gameType == null) {
            packet.getChannel().close();
            return true;
        }
        Player player = packet.getChannel().attr(Player.PLAYER_ATTRIBUTE_KEY).get();
        player.setGameType(gameType);
        if(gameType == GameType.GOLF) {
            player.getChannel().writeAndFlush(new Packet(PacketType.DATA, Tools.tabularize("status", "login")));
        }//todo
        return true;
    }

}
