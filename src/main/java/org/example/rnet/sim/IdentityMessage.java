package org.example.rnet.sim;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.rnet.endpoint.Message;

@AllArgsConstructor
@NoArgsConstructor
public class IdentityMessage implements Message<IdentityMessage> {


    @Getter
    private int id;

    @Override
    public int getMessageType() {
        return MessageType.IDENTITY.ordinal();
    }

    @Override
    public IdentityMessage decode(byte[] arr) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(arr);
        id = byteBuf.readByte();
        return this;
    }

    @Override
    public byte[] encode() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) getMessageType();
        bytes[1] = (byte) id;
        return bytes;
    }
}
