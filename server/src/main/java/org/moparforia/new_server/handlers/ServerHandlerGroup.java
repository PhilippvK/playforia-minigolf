package org.moparforia.new_server.handlers;

import org.moparforia.new_server.Player;
import org.moparforia.shared.networking.HandlerGroup;

public interface ServerHandlerGroup extends HandlerGroup {
    default void disconnect(Player player) {}
}
