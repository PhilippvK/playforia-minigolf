package org.moparforia.new_server;

import org.moparforia.new_server.handlers.ServerHandlerGroup;
import org.moparforia.shared.networking.HandlerGroup;
import org.moparforia.shared.networking.PacketHandler;

import java.util.HashSet;
import java.util.Set;

public class ServerPacketHandler extends PacketHandler {
    private final Set<ServerHandlerGroup> handlerGroups = new HashSet<>();

    @Override
    public void registerGroup(HandlerGroup group) {
        super.registerGroup(group);
        if (group instanceof ServerHandlerGroup) {
            handlerGroups.add((ServerHandlerGroup) group);
        }

    }

    @Override
    public void deregisterGroup(Class<? extends HandlerGroup> groupClazz) {
        super.deregisterGroup(groupClazz);
        handlerGroups.removeIf(groupClazz::isInstance);
    }

    public void disconnect(Player player) {
        for (ServerHandlerGroup group: handlerGroups) {
            group.disconnect(player);
        }
    }
}
