package org.moparforia.server.game;

import org.moparforia.server.net.Packet;

import java.util.ArrayList;

public class PlayerCollection {

    private ArrayList<Player> players = new ArrayList<Player>();

    public int playerCount() {
        return players.size();
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    protected boolean addPlayer(Player player) {
        if (!hasPlayer(player)) {
            players.add(player);
            return true;
        }
        return false;
    }

    protected boolean removePlayer(Player player) {
        return players.remove(player);
    }

    public void writeAll(Packet packet) {
        for (Player player : getPlayers()) {
            player.getChannel().writeAndFlush(packet);
        }
    }

    public void writeAll(String message) {
        for (Player player : getPlayers()) {
            player.getChannel().writeAndFlush(message);
        }
    }

    public void writeExcluding(Player exclude, Packet packet) {
        for (Player player : getPlayers()) {
            if (player != exclude) {
                player.getChannel().writeAndFlush(packet);
            }
        }
    }

    public void writeExcluding(Player exclude, String message) {
        for (Player player : getPlayers()) {
            if (player != exclude) {
                player.getChannel().writeAndFlush(message);
            }
        }
    }
}
