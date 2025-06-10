package org.myshop.queue;

public interface ErrorStore<Type, Identifier> {
    void put(Identifier id, Type error);
    Type take(Identifier id);
    Type peek(Identifier id);
    boolean has(Identifier id);
    void remove(Identifier id);
    void clear();
}