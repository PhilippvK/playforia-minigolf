package org.moparforia.server.event;

import org.jboss.netty.channel.Channel;
import org.moparforia.server.Server;
import org.moparforia.server.game.Lobby;
import org.moparforia.server.game.Player;

public class ClientDisconnectedEvent extends Event {

    private Channel channel;

    public ClientDisconnectedEvent(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void process(Server server) {
        Player player;
        if ((player = (Player)channel.getAttachment()) != null) {
            if (player.getLobby() != null) {
              player.getLobby().removePlayer(player, Lobby.PART_REASON_USERLEFT);
            }
        }
        System.out.println("Client disconnected: " + channel);
        channel = null;
    }

}
