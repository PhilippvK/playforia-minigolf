package org.moparforia.server.game.gametypes.golf;

import org.moparforia.shared.Track;
import org.moparforia.server.game.LobbyType;
import org.moparforia.server.game.Player;
import org.moparforia.server.game.gametypes.GolfGame;
import org.moparforia.server.net.Packet;
import org.moparforia.server.net.PacketType;
import org.moparforia.server.track.TrackCategory;
import org.moparforia.server.track.TrackManager;
import org.moparforia.shared.Tools;

import java.util.ArrayList;

/**
 * dual shizzle
 */
public class DualGame extends GolfGame {

    private boolean started;

    public DualGame(Player challenger, Player challenged, int gameId, int numberOfTracks,
                    int tracksType, int maxStrokes, int strokeTimeout, int waterEvent,
                    int collision, int trackScoring, int trackScoringEnd) {
        /*super(gameId, LobbyType.DUAL, "dualgame-" + (int) (Math.random() * 10000), null,
                false, numberOfTracks, -1, tracksType,
                maxStrokes, strokeTimeout, waterEvent, collision,
                trackScoring, trackScoringEnd, 2);*/
        super(gameId, LobbyType.SINGLE, "dualgame-" + (int) (Math.random() * 10000), null,
                false, numberOfTracks, -1, tracksType,
                maxStrokes, strokeTimeout, waterEvent, collision,
                trackScoring, trackScoringEnd, 2);
        challenged.getChannel().write(new Packet(PacketType.DATA,
                Tools.tabularize("lobby", "challenge", challenger.getNick(), numberOfTracks,
                        tracksType, maxStrokes, strokeTimeout, waterEvent,
                        collision, trackScoring, trackScoringEnd)));
        addPlayer(challenger);
        challenger.getLobby().addGame(this);
    }

    @Override
    public ArrayList<Track> initTracks() {
        return TrackManager.getRandomTracks(numberOfTracks, TrackCategory.getByTypeId(tracksType));
    }

    @Override
    public void sendJoinMessages(Player player) {
        if (started)
            super.sendJoinMessages(player);
    }

    public void start() {
        startGame();
        started = true;
        for (Player p : getPlayers())
            sendJoinMessages(p);
    }
}
