package org.myshop.batch;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Queues {
    private final BlockingQueue<BatchOrders> queue = new LinkedBlockingQueue<>();
    private final BlockingQueue<BatchOrders> errorQueue = new LinkedBlockingQueue<>();

    public BlockingQueue<BatchOrders> getQueue() {
        return queue;
    }

    public BlockingQueue<BatchOrders> getErrorQueue() {
        return errorQueue;
    }
}
