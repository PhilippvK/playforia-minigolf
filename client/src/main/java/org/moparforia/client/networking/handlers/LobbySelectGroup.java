package org.moparforia.client.networking.handlers;

import com.aapeli.applet.AApplet;
import org.moparforia.client.networking.Client;
import org.moparforia.shared.networking.HandlerGroup;
import org.moparforia.shared.networking.Packet;
import org.moparforia.shared.networking.PacketReceiveHandler;
import org.moparforia.shared.networking.PacketReceivePair;
import org.moparforia.shared.networking.packets.InitialPacket;
import org.moparforia.shared.networking.packets.LobbyFinderPacket;
import org.moparforia.shared.networking.packets.LobbyPacket;
import org.moparforia.shared.networking.packets.SimplePacket;
import org.moparforia.shared.networking.packets.headers.PacketHeaders;

import java.util.HashMap;
import java.util.Map;

public class LobbySelectGroup implements HandlerGroup {
    private Client client;

    private final PacketReceiveHandler<LobbyFinderPacket> lobbies = ((packet, ctx, handler) -> {
        client.getApplet().setGameState(3, 3);
        client.getApplet().gameContainer.lobbyPanel.lobbyMultiPlayerPanel.setLobbies(packet.getLobbies());
    });

    private final PacketReceiveHandler<SimplePacket> removeLobby = ((packet, ctx, handler) -> {
        client.getApplet().gameContainer.lobbyPanel.lobbyMultiPlayerPanel.removeLobby(packet.getString());
    });

//    private final PacketReceiveHandler<SimplePacket> playerJoined = ((packet, ctx, handler) -> {
//        client.getApplet().gameContainer.lobbyPanel.lobbyChatPanelMulti.gui_output.addMessage();
//    });

    private final PacketReceiveHandler<LobbyPacket> add = ((packet, ctx, handler) -> {
        client.getApplet().gameContainer.lobbyPanel.lobbyMultiPlayerPanel.addLobby(packet.getLobby());
    });

    public LobbySelectGroup(Client client) {
        this.client = client;
    }

    @Override
    public Map<String, PacketReceivePair<? extends Packet>> register() {
        Map<String, PacketReceivePair<? extends Packet>> handlers = new HashMap<>();
        handlers.put(PacketHeaders.LOBBIES.getHeader(),
                new PacketReceivePair<>(lobbies, LobbyFinderPacket.class)
        );
        handlers.put(PacketHeaders.REMOVE_LOBBY.getHeader(),
                new PacketReceivePair<>(removeLobby, SimplePacket.class)
        );
        handlers.put(PacketHeaders.ADD_LOBBY.getHeader(),
                new PacketReceivePair<>(add, LobbyPacket.class)
        );
        return handlers;
    }
}
