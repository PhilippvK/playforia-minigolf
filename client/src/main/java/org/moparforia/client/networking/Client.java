package org.moparforia.client.networking;

import agolf.GameApplet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;
import org.moparforia.shared.game.Lobby;
import org.moparforia.shared.networking.BaseChannelInitializer;
import org.moparforia.shared.networking.Packet;
import org.moparforia.shared.networking.packets.CreateLobbyPacket;
import org.moparforia.shared.networking.packets.InitialPacket;
import org.moparforia.shared.networking.packets.JoinGamePacket;
import org.moparforia.shared.utils.MinigolfVersion;

import javax.net.ssl.SSLException;
import java.util.logging.Logger;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

/**
 * Modification of which utilizes Java object serialization.
 */
public final class Client {
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    private final String host;
    private final int port;
    private final GameApplet applet;
    private Bootstrap bootstrap;
    private NioEventLoopGroup group;
    private Channel channel;

    private String username;
    private boolean isConnected = false;
    private boolean isLoggedIn = false;

    public Client(String host, int port, GameApplet applet) {
        this.host = host;
        this.port = port;
        this.applet = applet;
    }

    public void connect() throws InterruptedException, SSLException {
        final SslContext sslCtx;
        sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        ClientHandler handler = new ClientHandler(this);
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new BaseChannelInitializer() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(sslCtx.newHandler(ch.alloc(), "localhost", 8007));

                        super.initChannel(ch);

//                        pipeline.addLast("idleStateHandler", new IdleStateHandler(10,0,0));
                        pipeline.addLast(handler);
                    }
                });

        // Start the connection attempt.
        channel = bootstrap.connect(host, port).sync().channel();
    }

    public void disconnect() throws InterruptedException {
       try {
           logger.info("Disconnecting");
           channel.disconnect().sync();
       } finally {
           group.shutdownGracefully();
       }
    }

    public GameApplet getApplet() {
        return applet;
    }

    public void send(Packet packet) {
        channel.writeAndFlush(packet).addListener(FIRE_EXCEPTION_ON_FAILURE);
    }

    public void login(String username) {
        this.username = username;
        this.send(new InitialPacket(username, MinigolfVersion.getVersion(), false));
    }

    public String getUsername() {
        return username;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public void joinLobby(Lobby lobby, String password) {
        this.send(new JoinGamePacket(lobby.getName(), password));
    }

    public void joinLobby(Lobby lobby) {
        this.send(new JoinGamePacket(lobby.getName()));
    }

    public void createLobby(Lobby lobby, String password) {
        this.send(new CreateLobbyPacket(lobby, password));
    }
}