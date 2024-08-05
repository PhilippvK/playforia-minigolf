package org.moparforia.server.net.packethandlers.golf;

import org.moparforia.server.Server;
import org.moparforia.server.game.Lobby;
import org.moparforia.server.game.LobbyType;
import org.moparforia.server.game.Player;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketHandler;
import org.moparforia.server.net.PacketType;
import org.moparforia.shared.Tools;
import org.moparforia.shared.tracks.TrackManager;
import org.moparforia.shared.tracks.TrackSet;
import org.moparforia.shared.tracks.filesystem.FileSystemTrackManager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Playforia
 * 12.6.2013
 */
public class LobbyHandler implements PacketHandler {
    private static final TrackManager manager = FileSystemTrackManager.getInstance();

    @Override
    public PacketType getType() {
        return PacketType.DATA;
    }

    @Override
    public Pattern getPattern() {
        return Pattern.compile("lobby\\t(back|select|tracksetlist)(?:\\t([12x])(h)?)?");
    }

    @Override
    public boolean handle(Server server, Packet packet, Matcher message) {
        Player player = packet.getChannel().attr(Player.PLAYER_ATTRIBUTE_KEY).get();
        if (message.group(1).equals("back")) {
            if (player.getLobby() == null) {
                packet.getChannel().close();
            } else {
                Lobby lobby = player.getLobby();
                lobby.removePlayer(player, Lobby.PART_REASON_USERLEFT);
            }
        } else if (message.group(1).equals("select")) {
            LobbyType newLobbyType = LobbyType.getLobby(message.group(2));
            Lobby lobby = server.getLobby(newLobbyType);
            player.setChatHidden(message.group(3) != null && message.group(3).equals("h"));
            if (player.getLobby() == null) {
                // this shouldnt happen
                lobby.addPlayer(player, Lobby.JOIN_TYPE_NORMAL);
            } else if (player.getLobby() == lobby) {
                // todo: will this ever happen ?
            } else {
                int reason = newLobbyType == LobbyType.MULTI ? Lobby.PART_REASON_JOINED_MP : Lobby.PART_REASON_SWITCHEDLOBBY;
                player.getLobby().removePlayer(player, reason);
            }
            lobby.addPlayer(player, Lobby.JOIN_TYPE_NORMAL);
        } else if (message.group(1).equals("tracksetlist")) {
            List<TrackSet> trackSets = manager.getTrackSets();
            String[][] tracksInfo = new String[trackSets.size()][11];
            for (int i = 0; i < trackSets.size(); i++) {
                TrackSet trackSet = trackSets.get(i);
                tracksInfo[i][0] = trackSet.getName();
                tracksInfo[i][1] = String.valueOf(trackSet.getDifficulty().getId());
                tracksInfo[i][2] = String.valueOf(trackSet.getTracks().size());
                for (int j = 3; j < 11; j++) {//todo track records
                    tracksInfo[i][j] = j % 2 == 0 ? "1" : "No one";
                }
            }
            String cmd = "";
            for (int i = 0; i < tracksInfo.length; i++) {
                cmd += Tools.tabularize(tracksInfo[i]) + (i == tracksInfo.length - 1 ? "" : '\t');
            }
            packet.getChannel().writeAndFlush(new Packet(PacketType.DATA,
                    Tools.tabularize("lobby", "tracksetlist", cmd)));
        }
        return true;
    }
}
