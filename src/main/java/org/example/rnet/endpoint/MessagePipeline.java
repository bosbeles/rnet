package org.example.rnet.endpoint;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MessagePipeline implements Supplier<List<ChannelHandler>> {

    private static final int LENGTH_FIELD_LENGTH = 1;
    private final int maxFrameSize;
    private final MessageRegistry registry;


    public MessagePipeline(MessageRegistry registry) {
        this(registry,60);
    }

    public MessagePipeline(MessageRegistry registry, int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
        this.registry = registry;
    }


    @Override
    public List<ChannelHandler> get() {
        List<ChannelHandler> handlers = new ArrayList<>();
        handlers.add(new LengthFieldBasedFrameDecoder(maxFrameSize, 0, LENGTH_FIELD_LENGTH, 0, LENGTH_FIELD_LENGTH));
        handlers.add(new LengthFieldPrepender(LENGTH_FIELD_LENGTH));
        //handlers.add(new LoggingHandler(LogLevel.INFO));
        handlers.add(new MessageCodec(registry));


        return handlers;
    }
}