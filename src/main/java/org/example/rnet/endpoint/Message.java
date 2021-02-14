package org.example.rnet.endpoint;

public interface Message<T> {

    int getMessageType();

    T decode(byte[] arr);

    byte[] encode();
}
