package org.myshop.batch;

public interface QueueProvider<T> {
    T poll();
    void put(T payload);
}
