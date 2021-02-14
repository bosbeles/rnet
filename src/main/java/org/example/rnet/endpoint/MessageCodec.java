package org.example.rnet.endpoint;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {

    @Getter
    @Setter
    private MessageRegistry registry;


    public MessageCodec(MessageRegistry registry) {
        this.registry = registry;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        out.add(Unpooled.wrappedBuffer(msg.encode()));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        if (msg.readableBytes() > 0) {
            byte type = msg.readByte();
            byte[] bytes = new byte[msg.readableBytes()];
            msg.readBytes(bytes);
            Optional<Message> message = registry.createNew(type, bytes);
            out.add(message);

        }
    }
}
