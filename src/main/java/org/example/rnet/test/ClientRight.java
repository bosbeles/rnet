package org.example.rnet.test;

import io.netty.channel.socket.SocketChannel;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.example.rnet.endpoint.ClientEndpoint;
import org.example.rnet.endpoint.Initializer;
import org.example.rnet.endpoint.MessagePipeline;
import org.example.rnet.endpoint.MessageRegistry;
import org.example.rnet.sim.MessageType;
import org.example.rnet.sim.PingMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ClientRight {

    public static void main(String[] args) {
        MessageRegistry registry = new MessageRegistry();
        registry.register(2, arr -> {
            PingMessage pingMessage = new PingMessage();
            pingMessage.decode(arr);
            return pingMessage;
        });
        Supplier<Initializer<SocketChannel>> initializerSupplier = () -> new Initializer(new MessagePipeline(registry));


        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

        AtomicInteger counter = new AtomicInteger();
        SynchronizedDescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();
        for (int i = 0; i < 12; i++) {
            final int index = i + 1;
            ClientEndpoint clientRight = new ClientEndpoint(initializerSupplier.get(), "localhost", 6000, 6000 + index);
            clientRight.onMessage((m, c) -> {
                counter.incrementAndGet();
                if (m.getMessageType() == MessageType.PING.ordinal()) {
                    PingMessage pingMessage = (PingMessage) m;
                    long latency = System.currentTimeMillis() - pingMessage.getT0();
                    stats.addValue(latency);

                }
            });
            clientRight.start();
        }

        scheduler.scheduleWithFixedDelay(() -> {
            System.out.println("Client Right: " + counter.get());
            System.out.println(stats.toString());
            stats.clear();
        }, 1, 10, TimeUnit.SECONDS);


    }
}
