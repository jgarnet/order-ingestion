package org.myshop.queue;

import java.util.concurrent.ConcurrentHashMap;

public class HashMapErrorStore<Type, Identifier> implements ErrorStore<Type, Identifier> {
    private final ConcurrentHashMap<Identifier, Type> map = new ConcurrentHashMap<>();

    @Override
    public void put(Identifier id, Type error) {
        this.map.put(id, error);
    }

    @Override
    public Type take(Identifier id) {
        Type item = this.map.get(id);
        if (item != null) {
            this.map.remove(id);
        }
        return item;
    }

    @Override
    public Type peek(Identifier id) {
        return this.map.get(id);
    }

    @Override
    public boolean has(Identifier id) {
        return this.map.containsKey(id);
    }

    @Override
    public void remove(Identifier id) {
        this.map.remove(id);
    }

    @Override
    public void clear() {
        this.map.clear();
    }
}
