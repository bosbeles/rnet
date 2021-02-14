package org.example.rnet.endpoint;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ClientEndpoint extends BaseEndpoint {

    private final ChannelInitializer<SocketChannel> initializer;
    private int localPort;
    private EventLoopGroup sharedEventLoopGroup;


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


    public ClientEndpoint(Initializer<SocketChannel> initializer, int port) {
        this(initializer, "localhost", port);
    }

    public ClientEndpoint(Initializer<SocketChannel> initializer, String hostname, int port) {
        super(hostname, port);
        this.initializer = initializer;
        plugBusinessHandler(initializer);

    }

    public ClientEndpoint(Initializer<SocketChannel> initializer, String hostname, int port, int localPort) {
        super(hostname, port);
        this.localPort = localPort;
        this.initializer = initializer;
        plugBusinessHandler(initializer);

    }

    public ClientEndpoint(Initializer<SocketChannel> initializer, String hostname, int port, int localPort, EventLoopGroup sharedEventLoopGroup) {
        super(hostname, port);
        this.initializer = initializer;
        this.sharedEventLoopGroup = sharedEventLoopGroup;
        plugBusinessHandler(initializer);

    }


    @Override
    public synchronized void start() {
        stopStart();
        startTime = Instant.now();
        group = sharedEventLoopGroup != null ? sharedEventLoopGroup : new NioEventLoopGroup();
        doConnect();
    }


    public void doConnect() {
        try {
            Bootstrap clientBootstrap = new Bootstrap();

            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.localAddress(localPort);
            clientBootstrap.remoteAddress(new InetSocketAddress(hostname, port));
            clientBootstrap.handler(initializer);


            ChannelFuture channelFuture = clientBootstrap.connect().addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    peerConnected(future.channel().localAddress());
                }
            });

            channel = channelFuture.channel();

        } catch (Exception e) {
            log.error("Client error", e);
        } finally {
            log.trace("Finally");
        }

    }

    private synchronized void stopFail() {
        log.info("Fail");
        stateChanged(State.STOPPED);
        stop();
    }

    private synchronized void stopStart() {
        log.info("Starting");
        stateChanged(State.STARTING);
        stop();
    }


    private void peerConnected(SocketAddress socketAddress) {
        stateChanged(State.CONNECTED);

        log.info("Client connected to remote {}:{} at local address {}", hostname, port, socketAddress);
    }



}
