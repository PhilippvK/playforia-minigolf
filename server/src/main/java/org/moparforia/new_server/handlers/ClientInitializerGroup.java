package org.moparforia.new_server.handlers;

import org.moparforia.new_server.GolfServer;
import org.moparforia.new_server.Player;
import org.moparforia.shared.game.LobbyType;
import org.moparforia.shared.networking.HandlerGroup;
import org.moparforia.shared.networking.Packet;
import org.moparforia.shared.networking.PacketReceiveHandler;
import org.moparforia.shared.networking.PacketReceivePair;
import org.moparforia.shared.networking.packets.InitialPacket;
import org.moparforia.shared.networking.packets.LobbyPlayersPacket;
import org.moparforia.shared.networking.packets.headers.ErrorHeaders;
import org.moparforia.shared.networking.packets.headers.PacketHeaders;
import org.moparforia.shared.utils.MinigolfVersion;

import java.util.HashMap;
import java.util.Map;

public class ClientInitializerGroup implements HandlerGroup {
    private GolfServer server;

    private final PacketReceiveHandler<InitialPacket> infoSent = ((packet, ctx, handler) -> {
        if (!packet.getVersion().equals(MinigolfVersion.getVersion())) {
            ctx.writeAndFlush(new Packet(ErrorHeaders.VERSION_MISMATCH));
            ctx.disconnect();
            return;
        }
        String username = packet.getName();
        if (server.getPlayers()
                .stream().map(Player::getNick).anyMatch(username::equals)) {
            ctx.writeAndFlush(new Packet(ErrorHeaders.USERNAME_TAKEN));
            ctx.disconnect();
            return;
        }
        if (!server.isAllowCheating()) {
            if(packet.isCheatsEnabled()) {
                ctx.writeAndFlush(new Packet(ErrorHeaders.CHEATING_DISABLED));
                ctx.disconnect();
                return;
            }
        }
        Player player = server.findPlayer(ctx);
        player.setLoggedIn(true);
        player.setNick(packet.getName());
        ctx.writeAndFlush(new LobbyPlayersPacket(PacketHeaders.LOGIN_SUCCESSFUL,
                server.getNumberOfPlayers(LobbyType.SINGLE),
                server.getNumberOfPlayers(LobbyType.DUAL),
                server.getNumberOfPlayers(LobbyType.MULTI)));
        handler.deregisterGroup(ClientInitializerGroup.class);
        handler.registerGroup(new LobbyFinderGroup(server));
    });

    public ClientInitializerGroup(GolfServer server) {
        this.server = server;
    }

    @Override
    public Map<String, PacketReceivePair<? extends Packet>> register() {
        Map<String, PacketReceivePair<? extends Packet>> handlers = new HashMap<>();
        handlers.put(PacketHeaders.INITIAL_INFO.getHeader(),
                new PacketReceivePair<>(infoSent, InitialPacket.class)
        );
        return handlers;
    }
}
