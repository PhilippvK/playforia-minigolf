package org.moparforia.server.net.packethandlers.golf;

import org.moparforia.server.Server;
import org.moparforia.server.game.Lobby;
import org.moparforia.server.game.LobbyType;
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
public class LobbySelectHandler implements PacketHandler {

    public PacketType getType() {
        return PacketType.DATA;
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("lobbyselect\\t(rnop|select)(?:\\t([12x])(h)?)?");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        if (message.group(1).equals("rnop")) {
            packet.getChannel().writeAndFlush("d lobbyselect\tnop\t" + Tools.tabularize(server.getLobby(LobbyType.SINGLE).totalPlayerCount(), server.getLobby(LobbyType.DUAL).totalPlayerCount(), server.getLobby(LobbyType.MULTI).totalPlayerCount()));
        } else if (message.group(1).equals("select")) {
            // 1 for single, 1h for single hidden chat, 2 for dual, x for multi
            LobbyType lobbyType = LobbyType.getLobby(message.group(2));
            Player player = packet.getChannel().attr(Player.PLAYER_ATTRIBUTE_KEY).get();
            player.setChatHidden(message.group(3) != null && message.group(3).equals("h"));
            server.getLobby(lobbyType).addPlayer(player, Lobby.JOIN_TYPE_NORMAL);
        }
        return true;
    }
}
