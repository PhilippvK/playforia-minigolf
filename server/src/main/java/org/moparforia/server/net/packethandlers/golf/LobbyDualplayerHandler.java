package org.moparforia.server.net.packethandlers.golf;

import org.moparforia.server.Server;
import org.moparforia.server.game.LobbyType;
import org.moparforia.server.game.Player;
import org.moparforia.server.game.gametypes.golf.DualGame;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketHandler;
import org.moparforia.server.net.PacketType;
import org.moparforia.shared.Tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Playforia
 * 24.6.2013
 */
public class LobbyDualplayerHandler implements PacketHandler {
    @Override
    public PacketType getType() {
        return PacketType.DATA;
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("(game|lobby)\\t(challenge|accept|cancel|cfail|nc)\\t([ -~]+)(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        Player player = packet.getChannel().attr(Player.PLAYER_ATTRIBUTE_KEY).get();
        if (message.group(1).equals("lobby")) {
            if (message.group(2).equals("challenge")) {
                Player other = getPlayer(server, message.group(3));
                if (other == null) {// || other.isNotAcceptingChallenges()) {
                    player.getChannel().writeAndFlush(new Packet(PacketType.DATA,
                            Tools.tabularize("lobby", "cfail", "nochall")));
                    return true;
                }
                int BigGrinWinkTongue = 4;// :)
                int numberOfTracks = Integer.parseInt(message.group(BigGrinWinkTongue++));
                int tracksType = Integer.parseInt(message.group(BigGrinWinkTongue++));
                int maxStrokes = Integer.parseInt(message.group(BigGrinWinkTongue++));
                int strokeTimeout = Integer.parseInt(message.group(BigGrinWinkTongue++));
                int waterEvent = Integer.parseInt(message.group(BigGrinWinkTongue++));
                int collision = Integer.parseInt(message.group(BigGrinWinkTongue++));
                int trackScoring = Integer.parseInt(message.group(BigGrinWinkTongue++));
                int trackScoringEnd = Integer.parseInt(message.group(BigGrinWinkTongue++));
                new DualGame(player, other, server.getNextGameId(), numberOfTracks, tracksType, maxStrokes, strokeTimeout, waterEvent, collision, trackScoring, trackScoringEnd);
            } else if (message.group(2).equals("accept")) {
                Player other = getPlayer(server, message.group(3));
                if (other == null || !(other.getGame() instanceof DualGame)) {
                    player.getChannel().writeAndFlush(new Packet(PacketType.DATA,
                            Tools.tabularize("lobby", "cfail", "nouser")));//todo kick the faggot
                    return true;
                }
                ((DualGame) player.getGame()).start();
                //todo lobby part ~anonym-1234 1
            } else if (message.group(2).equals("cancel")) {
                Player other = getPlayer(server, message.group(3));
                if (other == null || !(other.getGame() instanceof DualGame)) {
                    player.getChannel().writeAndFlush(new Packet(PacketType.DATA,
                            Tools.tabularize("lobby", "cfail", "nouser")));//todo kick the faggot
                    return true;
                }
                other.getChannel().writeAndFlush(new Packet(PacketType.DATA,
                        Tools.tabularize("lobby", "cancel")));
            } else if (message.group(2).equals("cfail") && message.group(4).equals("refuse")) {
                Player other = getPlayer(server, message.group(3));
                if (other == null || !(other.getGame() instanceof DualGame)) {
                    player.getChannel().writeAndFlush(new Packet(PacketType.DATA,//todo kick the faggot
                            Tools.tabularize("lobby", "cfail", "nouser")));
                    return true;
                }
                other.getChannel().writeAndFlush(new Packet(PacketType.DATA,
                        Tools.tabularize("lobby", "cfail", "refuse")));
                //todo HOW TO REMOVE THE GAME FROM THE SERVER
            } else if (message.group(2).equals("nc")) {
                player.setNotAcceptingChallenges(message.group(3).equals("t"));
                return true;
            }
        }
        return true;
    }

    private Player getPlayer(Server server, String nick) {
        for (Player p : server.getLobby(LobbyType.DUAL).getPlayers())
            if (p.getNick().equals(nick))
                return p;
        return null;
    }
}
