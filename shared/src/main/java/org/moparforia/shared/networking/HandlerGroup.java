package org.moparforia.shared.networking;

import java.util.Map;

public interface HandlerGroup {
    Map<String, PacketReceivePair<? extends Packet>> register();
}
