package org.moparforia.new_server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.IdleStateHandler;
import org.moparforia.shared.game.Lobby;
import org.moparforia.shared.game.LobbyType;
import org.moparforia.shared.networking.BaseChannelInitializer;
import org.moparforia.shared.networking.packets.PlayerPacket;
import org.moparforia.shared.networking.packets.headers.ChatMessageHeaders;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
     * Modification of which utilizes Java object serialization.
     */
public class GolfServer {

    private final Map<ChannelHandlerContext, Player> players = new HashMap<>();
    private final Map<LobbyType, List<ServerLobby>> lobbies = new HashMap<>();
    private final Map<LobbyType, Set<Player>> finderPlayers = new HashMap<>();

    private final String host;
    private final int port;
    private final boolean allowCheating;

    public GolfServer(String host, int port, boolean allowCheating) {
        this.host = host;
        this.port = port;
        this.allowCheating = allowCheating;
        for (LobbyType type : LobbyType.values()) {
            lobbies.put(type, new ArrayList<>());
            finderPlayers.put(type, new HashSet<>());
        }
    }
    
    public void start() throws CertificateException, SSLException, InterruptedException {
        final SslContext sslCtx;
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();

        EventLoopGroup bossGroup = new NioEventLoopGroup(4);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new BaseChannelInitializer() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(sslCtx.newHandler(ch.alloc()));

                            super.initChannel(ch);

//                            pipeline.addLast(new IdleStateHandler(10, 5, 10));
                            pipeline.addLast(new GolfChildHandler(GolfServer.this));
                        }

                    });

            // Bind and start to accept incoming connections.
            bootstrap.bind(host, port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void addPlayer(Player player) {
        players.put(player.getCtx(), player);
    }

    public void removePlayer(Player player) {
        players.remove(player.getCtx());
    }

    public void removeFinderPlayer(Player player) {
        for (Set<Player> players: finderPlayers.values()) {
            if (players.contains(player)) {
                players.remove(player);
                PlayerPacket packet = new PlayerPacket(ChatMessageHeaders.PLAYER_LEFT, player.getNick());
                players.forEach(
                        p -> p.getCtx().writeAndFlush(packet)
                );
            }
        }
    }

    public Player findPlayer(ChannelHandlerContext ctx) {
        return players.get(ctx);
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    public List<ServerLobby> getLobbies(LobbyType type) {
        return lobbies.get(type);
    }

    public List<Lobby> getSerializableLobbies(LobbyType type) {
        return Collections.unmodifiableList(
                getLobbies(type).stream().map(ServerLobby::toLobby).collect(Collectors.toList())
        );
    }

    public int getNumberOfPlayers(LobbyType type) {
        return lobbies.get(type).size() + finderPlayers.get(type).size();
    }

    public void addFindingPlayer(LobbyType type, Player player) {
        finderPlayers.get(type).add(player);
    }

    public Set<Player> getFindingPlayers(LobbyType type) {
        return finderPlayers.get(type);
    }

    public boolean isAllowCheating() {
        return allowCheating;
    }


}
