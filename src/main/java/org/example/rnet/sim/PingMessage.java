package org.example.rnet.sim;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.rnet.endpoint.Message;

@AllArgsConstructor
@NoArgsConstructor
public class PingMessage implements Message<PingMessage> {


    @Getter
    @Setter
    private long t0;

    @Getter
    @Setter
    private long t1;

    @Getter
    @Setter
    private long t2;

    @Override
    public int getMessageType() {
        return MessageType.PING.ordinal();
    }

    @Override
    public PingMessage decode(byte[] arr) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(arr);
        t0 = byteBuf.readLong();
        t1 = byteBuf.readLong();
        t2 = byteBuf.readLong();

        return this;
    }

    @Override
    public byte[] encode() {
        ByteBuf byteBuf = Unpooled.buffer(25);
        byteBuf.writeByte(getMessageType());
        byteBuf.writeLong(t0);
        byteBuf.writeLong(t1);
        byteBuf.writeLong(t2);
        return byteBuf.array();
    }
}
