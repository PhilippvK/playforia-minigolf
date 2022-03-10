package org.moparforia.server;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.moparforia.server.event.Event;
import org.moparforia.server.game.Lobby;
import org.moparforia.shared.game.LobbyType;
import org.moparforia.server.game.Player;
import org.moparforia.server.net.*;
import org.moparforia.shared.tracks.TrackLoadException;
import org.moparforia.shared.tracks.filesystem.FileSystemTrackManager;
import org.moparforia.shared.tracks.filesystem.FileSystemStatsManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Server implements Runnable {

    public static final boolean DEBUG = true;

    private HashMap<Integer, Player> players = new HashMap<Integer, Player>();
    private ChannelGroup allChannels = new DefaultChannelGroup();
    private ConcurrentLinkedQueue<Event> events = new ConcurrentLinkedQueue<Event>();
    private HashMap<PacketType, ArrayList<PacketHandler>> packetHandlers = new HashMap<PacketType, ArrayList<PacketHandler>>();

    private String host;
    private int port;

    private HashMap<LobbyType, Lobby> lobbies = new HashMap<LobbyType, Lobby>();
    //private ArrayList<LobbyRef> lobbies = new ArrayList<LobbyRef>();
    //private HashMap<Integer, Game> games = new HashMap<Integer, Game>();

    private int playerIdCounter;
    private int gameIdCounter;


    public Server(String host, int port) {
        this.host = host;
        this.port = port;
        for (LobbyType lt : LobbyType.values()) {
            lobbies.put(lt, new Lobby(lt));
        }
    }

//    public Server() {
//        for (LobbyType lt : LobbyType.values()) {
//            lobbies.put(lt, new Lobby(lt));
//        }
//    }

    public int getNextPlayerId() {
        return playerIdCounter++;
    }

    public int getNextGameId() {
        return gameIdCounter++;
    }

    public Lobby getLobby(LobbyType id) {
        if (lobbies.containsKey(id))
            return lobbies.get(id);
        return null;
    }

    public HashMap<LobbyType, Lobby> getLobbies() {
        return lobbies;
    }

    /*public void addGame(Game g) {
        if (!games.containsValue(g))
            games.put(g.getGameId(), g);
    }

    public Game getGame(int gameId) {
        return games.get(gameId);
    }

    public HashMap<Integer, Game> getGames() {
        return games;
    }

    public HashMap<Integer, Game> getGames(String lobbyId) {
        HashMap<Integer, Game> map = new HashMap<Integer, Game>();
        for (Map.Entry<Integer, Game> e : games.entrySet()) {
            if (e.getValue().getLobbyType().equals(lobbyId)) {
                map.put(e.getKey(), e.getValue());
            }
        }
        return map;
    }*/

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void addChannel(Channel channel) {
        allChannels.add(channel);
    }

    /**
     * This is the only method that should be called from another thread (ie, the ClientChannelHandler)
     *
     * @param evt
     */
    public void addEvent(Event evt) {
        events.add(evt);
    }

    public ArrayList<PacketHandler> getPacketHandlers(PacketType type) {
        return packetHandlers.get(type);
    }

    public HashMap<Integer, Player> getPlayers() {
        return players;
    }

    public boolean hasPlayer(int id) {
        return players.containsKey(id);
    }

    public Player getPlayer(int id) {
        return players.get(id);
    }

    public void addPlayer(Player p) {
        if (!players.containsValue(p))
            players.put(p.getId(), p);
    }

    public void start() {
        try {
            FileSystemTrackManager.getInstance().load();
            FileSystemStatsManager.getInstance().load();
        } catch (TrackLoadException | IOException e) {
            System.err.println("Unable to load tracks: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        packetHandlers = PacketHandlerFactoryGeneratorClassHelperImplementationDecorator.getPacketHandlers();
        System.out.println("Loaded " + packetHandlers.size() + " packet handler type(s)");

        ChannelFactory factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        ServerBootstrap bootstrap = new ServerBootstrap(factory);
        final ClientChannelHandler clientHandler = new ClientChannelHandler(this);
        final IdleStateHandler idleState = new IdleStateHandler(new HashedWheelTimer(1, TimeUnit.SECONDS), 2, 0, 0);
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(
                        new DelimiterBasedFrameDecoder(250, Delimiters.lineDelimiter()),
                        new PacketDecoder(),
                        new PacketEncoder(),
                        idleState,
                        clientHandler);
            }
        });
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        try {
            bootstrap.bind(new InetSocketAddress(host, port));
            new Thread(this).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Started server on host " + this.host + " with port " + this.port);
        while (true) {
            try {
                Thread.sleep(10);
                Iterator<Event> iterator = events.iterator();
                while (iterator.hasNext()) {
                    Event evt = iterator.next();
                    try {
                        if (evt.shouldProcess(this)) {
                            evt.process(this);
                            iterator.remove();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        iterator.remove();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
