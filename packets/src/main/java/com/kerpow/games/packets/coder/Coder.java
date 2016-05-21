package com.kerpow.games.packets.coder;

import com.kerpow.games.packets.MessageHandler;
import com.kerpow.games.packets.coder.storage.ArrayHandlerStorage;
import com.kerpow.games.packets.coder.storage.MappedHandlerStorage;

import java.util.List;

public abstract class Coder<TKey, THandler> {

    private final HandlerStorage<TKey, THandler> storage;

    public Coder(HandlerStorage<TKey, THandler> storage) {
        this.storage = storage;
    }

    public void addHandler(TKey key, THandler handler) {
        this.storage.put(key, handler);
    }

    protected THandler getHandler(TKey key) {
        return storage.get(key);
    }

    public static <TKey, THandler> HandlerStorage<TKey, THandler> getStorageForSize(int size) {
        return size < 15 ? new ArrayHandlerStorage<TKey, THandler>() : new MappedHandlerStorage<TKey, THandler>();
    }
}
