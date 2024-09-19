package org.moparforia.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.moparforia.server.event.Event;
import org.moparforia.server.game.Lobby;
import org.moparforia.server.game.LobbyType;
import org.moparforia.server.game.Player;
import org.moparforia.server.net.*;
import org.moparforia.shared.tracks.TrackLoadException;
import org.moparforia.shared.tracks.TracksLocation;
import org.moparforia.shared.tracks.filesystem.FileSystemTrackManager;
import org.moparforia.shared.tracks.filesystem.FileSystemStatsManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Server implements Runnable {

    public static final boolean DEBUG = true;
    public static final String DEFAULT_TRACKS_DIRECTORY = "tracks";

    private HashMap<Integer, Player> players = new HashMap<>();
    private ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private BlockingQueue<Event> events = new LinkedBlockingQueue<>();
    private HashMap<PacketType, ArrayList<PacketHandler>> packetHandlers = new HashMap<>();

    private String host;
    private int port;
    private final boolean verbose;
    private Optional<String> tracksDirectory;

    private ChannelFuture serverChannelFuture;
    private boolean running;

    private HashMap<LobbyType, Lobby> lobbies = new HashMap<>();
    //private ArrayList<LobbyRef> lobbies = new ArrayList<LobbyRef>();
    //private HashMap<Integer, Game> games = new HashMap<Integer, Game>();

    private int playerIdCounter;
    private int gameIdCounter;


    public Server(String host, int port, boolean verbose, Optional<String> tracksDirectory) {
        this.host = host;
        this.port = port;
        this.verbose = verbose;
        this.tracksDirectory = tracksDirectory;
        for (LobbyType lt : LobbyType.values()) {
            lobbies.put(lt, new Lobby(lt));
        }
    }

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
     */
    public void addEvent(Event evt) {
        try {
            events.put(evt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
            TracksLocation tracksLocation = this.getTracksLocation();
            FileSystemTrackManager.getInstance().load(tracksLocation);
            FileSystemStatsManager.getInstance().load(tracksLocation);
        } catch (TrackLoadException | IOException | URISyntaxException e) {
            System.err.println("Unable to load tracks: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        packetHandlers = PacketHandlerFactory.getPacketHandlers();
        System.out.println("Loaded " + packetHandlers.size() + " packet handler type(s)");

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap()
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.attr(ClientState.CLIENT_STATE_ATTRIBUTE_KEY).set(new ClientState());
                    ch.pipeline()
                        .addLast(
                            new DelimiterBasedFrameDecoder(2000, Delimiters.lineDelimiter()),
                            new PacketDecoder(),
                            new PacketEncoder(Server.this.verbose),
                            new IdleStateHandler(2, 0, 0),
                            new ClientChannelHandler(Server.this)
                            );
                        }
                })
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            this.serverChannelFuture = bootstrap.bind(new InetSocketAddress(host, port)).sync();
            this.running = true;
            new Thread(this).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stop() throws InterruptedException {
        this.serverChannelFuture.channel().close().sync();
        this.running = false;
    }

    @Override
    public void run() {
        System.out.println("Started server on host " + this.host + " with port " + this.port);
        while (this.running) {
            try {
                Event evt = events.take();
                if (evt.shouldProcess(this)) {
                    evt.process(this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Determines where to look for tracks.
     * In order of priority:
     * 1. If user has specified a tracks directory CLI flags, search for it in the default FileSystem
     * 2. Else, if running in a jar file, use bundled tracks
     * 3. Else, if running in an IDE, use resources folder
     * 4. Else, use default filesystem and look in default directory
     */
    private TracksLocation getTracksLocation() throws URISyntaxException, IOException {
        if (tracksDirectory.isPresent()) {
            System.out.println("Using CLI argument for tracks location: " + tracksDirectory.get());
            return new TracksLocation(FileSystems.getDefault(), tracksDirectory.get());
        }

        URL resource = this.getClass().getResource("/tracks");
        if (resource != null) {
            URI resourceUri = resource.toURI();
            if (resourceUri.getScheme().equals("jar")) {
                // tracks are bundled in jar
                System.out.println("Using bundled jar resources for tracks location");
                return new TracksLocation(FileSystems.newFileSystem(resourceUri, Collections.emptyMap()), "/tracks");
            }
            // running in IDE
            String tracksPath = Paths.get(resourceUri).toString();
            System.out.println("Using path to resources for tracks location: " + tracksPath);
            return new TracksLocation(FileSystems.getDefault(), tracksPath);
        }
        // running outside of jar, outside of IDE
        System.out.println("Using default tracks directory for tracks location");
        return new TracksLocation(FileSystems.getDefault(), DEFAULT_TRACKS_DIRECTORY);
    }
}
