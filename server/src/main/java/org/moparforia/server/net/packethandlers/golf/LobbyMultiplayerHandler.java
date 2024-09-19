package org.moparforia.server.net.packethandlers.golf;

import org.moparforia.server.Server;
import org.moparforia.server.game.Lobby;
import org.moparforia.server.game.Player;
import org.moparforia.server.game.gametypes.golf.MultiGame;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketHandler;
import org.moparforia.server.net.PacketType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LobbyMultiplayerHandler implements PacketHandler {

    public PacketType getType() {
        return PacketType.DATA;
    }

    // lobby	cmpt	-	-	0	3	10	1	20	60	0	1	0	0
    //lobby	    cmpt	-	-	0	2	10	1	20	60	0	1	0	0"
    // lobby   jmpt    543543
    // lobby   jmpt    542534 cock

    public Pattern getPattern() {
        return Pattern.compile("lobby\\t(c|j)mpt\\t([a-zA-Z0-9 _`~!@#$%^&*()+={}:;<>,\\\\.\\[\\]\\'\\\"?\\/|\\-]+)(?:\\t)?([a-zA-Z0-9 _`~!@#$%^&*()+={}:;<>,\\\\.\\[\\]\\'\\\"?\\/|\\-]+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?(?:\\t)?(\\d+)?");
    }

    public boolean handle(Server server, Packet packet, Matcher message) {
        Player player = packet.getChannel().attr(Player.PLAYER_ATTRIBUTE_KEY).get();
        Lobby lobby = player.getLobby();

        if (message.group(1).equals("c")) {
            String gameName = message.group(2);
            String password = message.group(3);

            int perms = Integer.parseInt(message.group(4)); // not sure about this.
            int playerCount = Integer.parseInt(message.group(5));
            int numberOfTracks = Integer.parseInt(message.group(6));
            int trackType = Integer.parseInt(message.group(7));
            int maxStrokes = Integer.parseInt(message.group(8));
            int strokeTimeout = Integer.parseInt(message.group(9));
            int water = Integer.parseInt(message.group(10));
            int collision = Integer.parseInt(message.group(11));
            int scoreSystem = Integer.parseInt(message.group(12));
            int weightEnd = Integer.parseInt(message.group(13));

            new MultiGame(player, server.getNextGameId(), gameName, password, numberOfTracks, perms, trackType, maxStrokes, strokeTimeout,
                    water, collision, scoreSystem, weightEnd, playerCount);

        } else if (message.group(1).equals("j")) {
            int gameId = Integer.parseInt(message.group(2));

            if (lobby.gameExists(gameId)) {
                MultiGame game = (MultiGame) lobby.getGame(gameId);
                String password = "-";
                if (message.groupCount() > 4) {
                    password = message.group(3);
                }
                // returns false if the retard gets the pass wrong todo: message to announce retard
                game.addPlayer(player, password);

            } else {
                return false; // MELTDOWN MELTDOWN WTF DO WE DO!?!?
            }

        } else {
            return false;
        }

        return true;
    }
}
