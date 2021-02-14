package org.example.rnet.endpoint;

public class RedirectServer {

    /*

    private final String fromHost;
    private final String toHost;
    private final int fromPort;
    private final int toPort;
    protected EventLoopGroup group;

    public RedirectServer(int fromPort, int toPort) {
        this("localhost", fromPort, "localhost", toPort);
    }

    public RedirectServer(String fromHost, int fromPort, String toHost, int toPort) {
        this.fromPort = fromPort;
        this.toPort = toPort;
        this.fromHost = fromHost;
        this.toHost = toHost;
    }


    public synchronized void start() {
        stop();
        group = new NioEventLoopGroup(4);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress(fromHost, fromPort));
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 100);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new RedirectHandler());
                }
            });

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


    @ChannelHandler.Sharable
    private static class RedirectHandlerFrom extends ChannelInboundHandlerAdapter {

        final static AttributeKey<Integer> PORT = AttributeKey.valueOf("port");

        private Map<Integer, Set<Channel>> channelMap = new ConcurrentHashMap<>();


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.channel().attr(PORT).set(((InetSocketAddress)ctx.channel().remoteAddress()).getPort());

            super.channelActive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Integer incomingPort = ctx.channel().attr(PORT).get();
            Set<Channel> channels = channelMap.get(incomingPort);
            if(channels != null) {
                channels.forEach(ch -> ch.write(msg));
            }
            ReferenceCountUtil.release(msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    @ChannelHandler.Sharable
    private static class RedirectHandlerTo implements ChannelHandler {


        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }
    }
    */

}
