package org.moparforia.client.networking.handlers;

import com.aapeli.applet.AApplet;
import org.moparforia.client.networking.Client;
import org.moparforia.shared.networking.HandlerGroup;
import org.moparforia.shared.networking.Packet;
import org.moparforia.shared.networking.PacketReceiveHandler;
import org.moparforia.shared.networking.PacketReceivePair;
import org.moparforia.shared.networking.packets.LobbyPlayersPacket;
import org.moparforia.shared.networking.packets.headers.ErrorHeaders;
import org.moparforia.shared.networking.packets.headers.PacketHeaders;

import java.util.HashMap;
import java.util.Map;

public class InitialHandlerGroup implements HandlerGroup {

    private Client client;

    private final PacketReceiveHandler<LobbyPlayersPacket> loginConfirmed = ((packet, ctx, handler) -> {
        client.getApplet().setGameState(2, 300);
        client.setLoggedIn(true);
        System.out.println("Logged in");
        client.getApplet().gameContainer.lobbySelectionPanel.setNumberOfPlayers(
                packet.getSingle(),
                packet.getDual(),
                packet.getMulti()
        );
        client.getApplet().setGameSettings(true, 1, false, false);
        handler.deregisterGroup(InitialHandlerGroup.class);
        handler.registerGroup(new LobbySelectGroup(client));
    });

    private final PacketReceiveHandler<Packet> invalidVersion = ((packet, ctx, handler) -> {
        System.out.println("Invalid version");
        client.getApplet().setGameState(AApplet.END_ERROR_VERSION);
    });

    private final PacketReceiveHandler<Packet> nickInUse = ((packet, ctx, handler) -> {
        client.getApplet().setGameState(1, 4);
    });

    private final PacketReceiveHandler<Packet> noCheating = ((packet, ctx, handler) -> {
        System.out.println("Cheating is wrong");
        client.getApplet().setEndState(AApplet.END_ERROR_CONNECTION);
    });

    public InitialHandlerGroup(Client client) {
        this.client = client;
    }

    @Override
    public Map<String, PacketReceivePair<? extends Packet>> register() {
        Map<String, PacketReceivePair<? extends Packet>> handlers = new HashMap<>();
        handlers.put(PacketHeaders.LOGIN_SUCCESSFUL.getHeader(),
                new PacketReceivePair<>(loginConfirmed, LobbyPlayersPacket.class)
        );
        handlers.put(ErrorHeaders.VERSION_MISMATCH.getHeader(),
                new PacketReceivePair<>(invalidVersion, Packet.class)
        );
        handlers.put(ErrorHeaders.USERNAME_TAKEN.getHeader(),
                new PacketReceivePair<>(nickInUse, Packet.class)
        );
        handlers.put(ErrorHeaders.CHEATING_DISABLED.getHeader(),
                new PacketReceivePair<>(noCheating, Packet.class)
        );
        return handlers;
    }


}
