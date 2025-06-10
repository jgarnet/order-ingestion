package org.myshop.queue;

public interface QueueProvider<T> {
    T poll();
    void put(T payload);
}
