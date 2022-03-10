package org.moparforia.new_server.handlers;

import org.moparforia.new_server.GolfServer;
import org.moparforia.new_server.Player;
import org.moparforia.new_server.ServerLobby;
import org.moparforia.shared.game.Lobby;
import org.moparforia.shared.game.LobbyType;
import org.moparforia.shared.networking.Packet;
import org.moparforia.shared.networking.PacketReceiveHandler;
import org.moparforia.shared.networking.PacketReceivePair;
import org.moparforia.shared.networking.packets.CreateLobbyPacket;
import org.moparforia.shared.networking.packets.LobbyFinderPacket;
import org.moparforia.shared.networking.packets.LobbyPacket;
import org.moparforia.shared.networking.packets.PlayerPacket;
import org.moparforia.shared.networking.packets.SelectGameTypePacket;
import org.moparforia.shared.networking.packets.headers.ChatMessageHeaders;
import org.moparforia.shared.networking.packets.headers.PacketHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LobbyFinderGroup implements ServerHandlerGroup {
    private GolfServer server;
    private final PacketReceiveHandler<SelectGameTypePacket> selectGameType = ((packet, ctx, handler) -> {
        Player player = server.findPlayer(ctx);
        LobbyType type = packet.getType();
        PlayerPacket joined_packet = new PlayerPacket(ChatMessageHeaders.PLAYER_JOINED, player.getNick());
        server.getFindingPlayers(type).forEach(p -> p.getCtx().writeAndFlush(joined_packet));

        server.addFindingPlayer(type, player);
        if (type == LobbyType.MULTI) {
            player.send(
                    new LobbyFinderPacket(
                            PacketHeaders.LOBBIES,
                            server.getSerializableLobbies(type),
                            server.getFindingPlayers(type).stream().map(Player::getNick).collect(Collectors.toList())
                    )
            );
        }
    });
    private final PacketReceiveHandler<CreateLobbyPacket> createLobby = ((packet, ctx, handler) -> {
        Lobby lobby = packet.getLobby();
        ServerLobby serverLobby = new ServerLobby(lobby, packet.getPassword());
        server.getLobbies(LobbyType.MULTI).add(serverLobby);
        LobbyPacket lobbyPacket = new LobbyPacket(PacketHeaders.ADD_LOBBY, serverLobby.toLobby());
        server.getFindingPlayers(LobbyType.MULTI).forEach(p -> p.getCtx().writeAndFlush(lobbyPacket));
    });

    @Override
    public void disconnect(Player player) {
        server.removeFinderPlayer(player);
    }

    public LobbyFinderGroup(GolfServer server) {
        this.server = server;
    }

    @Override
    public Map<String, PacketReceivePair<? extends Packet>> register() {
        Map<String, PacketReceivePair<? extends Packet>> handlers = new HashMap<>();
        handlers.put(PacketHeaders.SELECT_GAME_TYPE.getHeader(),
                new PacketReceivePair<>(selectGameType, SelectGameTypePacket.class)
        );
        handlers.put(PacketHeaders.CREATE_LOBBY.getHeader(),
                new PacketReceivePair<>(createLobby, CreateLobbyPacket.class)
        );
        return handlers;
    }
}
