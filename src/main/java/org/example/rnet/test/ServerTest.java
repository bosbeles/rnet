package org.example.rnet.test;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import org.example.rnet.endpoint.Initializer;
import org.example.rnet.endpoint.MessagePipeline;
import org.example.rnet.endpoint.MessageRegistry;
import org.example.rnet.endpoint.ServerEndpoint;
import org.example.rnet.sim.PingMessage;
import org.example.rnet.sim.TestForwardTable;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ServerTest {

    public static void main(String[] args) {

        TestForwardTable table = new TestForwardTable(5000, 6000,
                100, 12, 32);


        MessageRegistry registry = new MessageRegistry();
        registry.register(2, arr -> {
            PingMessage pingMessage = new PingMessage();
            pingMessage.decode(arr);
            pingMessage.setT1(System.currentTimeMillis());
            return pingMessage;
        });
        Supplier<Initializer<SocketChannel>> initializerSupplier = () -> new Initializer(new MessagePipeline(registry));
        ServerEndpoint server = new ServerEndpoint(initializerSupplier.get(), 5000);

        ServerEndpoint serverRight = new ServerEndpoint(initializerSupplier.get(), 6000);


        AtomicInteger serverCounter = new AtomicInteger();
        server.onMessage((m, c) -> {
            serverCounter.incrementAndGet();
            List<Integer> list = table.getLeftToRightMap().get(((InetSocketAddress) c.remoteAddress()).getPort());
            list.forEach(port -> {
                Channel channel = serverRight.getChannelMap().get(port);
                if (channel != null) {
                    serverRight.send(m, channel);

                }
            });
        });

        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            System.out.println("Server: " + serverCounter.get());
        }, 1, 10, TimeUnit.SECONDS);

        new Thread(serverRight::start).start();
        new Thread(server::start).start();


    }
}
