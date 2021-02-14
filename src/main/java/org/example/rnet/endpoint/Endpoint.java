package org.example.rnet.endpoint;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Endpoint {

    enum State {
        STARTING, CONNECTED, STOPPED, DISCONNECTED, RECONNECTING, TIMEOUT
    }

    void start();

    void stop();

    void send(Message data);

    void send(Message data, Channel channel);

    void onMessage(BiConsumer<Message, Channel> onMessage);

    void onError(Consumer<Throwable> onError);

    void onStateChange(BiConsumer<State, State> onStateChange);

    String getName();

    void setName(String name);

    EventLoopGroup getEventLoop();

    Map<Integer, Channel> getChannelMap();

}
