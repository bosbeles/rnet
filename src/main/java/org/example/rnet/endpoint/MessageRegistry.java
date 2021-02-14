package org.example.rnet.endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class MessageRegistry {

    Map<Integer, Function<byte[], Message>> factories = new HashMap<>();

    public void register(int type, Function<byte[], Message> factory) {
        factories.put(type, factory);
    }

    public Optional<Message> createNew(int type, byte[] arr) {
        Function<byte[], Message> factory = factories.get(type);
        if (factory != null) {
            return Optional.ofNullable(factory.apply(arr));
        }
        return Optional.empty();
    }
}
