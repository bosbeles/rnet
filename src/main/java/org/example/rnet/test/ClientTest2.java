package org.example.rnet.test;

import io.netty.channel.socket.SocketChannel;
import org.example.rnet.endpoint.*;
import org.example.rnet.sim.PingMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ClientTest2 {

    public static void main(String[] args) {
        MessageRegistry registry = new MessageRegistry();
        registry.register(2, arr -> {
            PingMessage pingMessage = new PingMessage();
            pingMessage.decode(arr);
            return pingMessage;
        });
        Supplier<Initializer<SocketChannel>> initializerSupplier = () -> new Initializer(new MessagePipeline(registry));


        AtomicInteger clientCounter = new AtomicInteger();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
        for (int i = 50; i < 100; i++) {
            final int index = i + 1;
            ClientEndpoint client = new ClientEndpoint(initializerSupplier.get(), "localhost", 5000, 5000 + index);

            client.onStateChange((oldState, newState) -> {
                if (newState == Endpoint.State.CONNECTED) {
                    scheduler.scheduleWithFixedDelay(() -> {
                        for (int k = 0; k < 4; k++) {
                            PingMessage message = new PingMessage();
                            message.setT0(System.currentTimeMillis());
                            client.send(message);
                            clientCounter.incrementAndGet();
                        }
                        client.flush();
                    }, 1000, 10, TimeUnit.MILLISECONDS);
                }
            });

            client.start();


        }


        scheduler.scheduleWithFixedDelay(() -> {
            System.out.println("Client: " + clientCounter.get());
        }, 1, 10, TimeUnit.SECONDS);

    }
}
