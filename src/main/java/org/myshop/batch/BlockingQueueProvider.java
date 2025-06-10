package org.myshop.batch;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueProvider<T> implements QueueProvider<T> {
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    @Override
    public T poll() {
        try {
            return this.queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    @Override
    public void put(T payload) {
        this.queue.offer(payload);
    }
}