package org.example.rnet.endpoint;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ServerEndpoint extends BaseEndpoint {

    private boolean onceConnected;

    private double retryCoeff = 2.0;
    private long minRetryTime = 1000;
    private long currentRetryTime = minRetryTime;
    private long maxRetryTime = 30000;
    private long startupTimeout = 60_000;
    private int startupMaxRetry = -1;
    private boolean closeAfterStartupTimeout = false;

    private int disconnectTimeout = 40_000;
    private int disconnectMaxRetry = -1;
    private boolean closeAfterReconnectTimeout = false;

    private Instant startTime;
    private Instant disconnectedTime;


    private final Initializer<SocketChannel> initializer;

    public ServerEndpoint(Initializer<SocketChannel> initializer, int port) {
        this(initializer, "localhost", port);
    }

    public ServerEndpoint(Initializer<SocketChannel> initializer, String hostname, int port) {
        super(hostname, port);
        this.initializer = initializer;
        plugBusinessHandler(initializer);

    }

    @Override
    public synchronized void start() {
        stop();
        group = new NioEventLoopGroup(4);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress(hostname, port));

            serverBootstrap.childHandler(initializer);
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            log.info("Server is open at {}:{}", hostname, port);

            channel = channelFuture.channel();
            channel.eventLoop().scheduleAtFixedRate(() -> {
                channelGroup.flush();
            }, 100, 50, TimeUnit.MILLISECONDS);
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    @Override
    public synchronized void stop() {
        if (group != null) {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.stop();
    }

}
