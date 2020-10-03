package org.moparforia.server.net;

import org.moparforia.server.net.packethandlers.*;
import org.moparforia.server.net.packethandlers.golf.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Playforia
 * 14.6.2013
 */
public class PacketHandlerFactoryGeneratorClassHelperImplementationDecorator {

    //todo packethandlers for each game type ( should be pretty much the same for golf and golf2 )
    public static final HashMap<PacketType, ArrayList<PacketHandler>> getPacketHandlers() {
        PacketHandler[] handlers = new PacketHandler[]{new ChatHandler(), new GameHandler(),
                new LanguageHandler(), new LobbyHandler(), new LobbySelectHandler(), new LoginHandler(),
                new LoginTypeHandler(), new NewHandler(), new PongHandler(), new ReconnectHandler(),
                new TlogHandler(), new TrackTestLoginHandler(), new VersionHandler(), new LobbyCreateSinglePlayerHandler(),
                new LobbyMultiplayerHandler(), new QuitHandler()};
                //new LobbyMultiplayerHandler(), new LobbyDualplayerHandler(), new QuitHandler()};
        ArrayList<PacketHandler> data = new ArrayList<PacketHandler>();
        ArrayList<PacketHandler> command = new ArrayList<PacketHandler>();
        ArrayList<PacketHandler> string = new ArrayList<PacketHandler>();
        ArrayList<PacketHandler> none = new ArrayList<PacketHandler>();
        for (PacketHandler handler : handlers) {
            switch (handler.getType()) {
                case DATA:
                    data.add(handler);
                    break;
                case COMMAND:
                    command.add(handler);
                    break;
                case STRING:
                    string.add(handler);
                    break;
                case NONE:
                    none.add(handler);
                    break;
            }
        }
        HashMap<PacketType, ArrayList<PacketHandler>> map = new HashMap<PacketType, ArrayList<PacketHandler>>();
        map.put(PacketType.DATA, data);
        map.put(PacketType.COMMAND, command);
        map.put(PacketType.STRING, string);
        map.put(PacketType.NONE, none);
        return map;
    }
}
