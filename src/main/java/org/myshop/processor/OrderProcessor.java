package org.myshop.processor;

import org.myshop.Container;
import org.myshop.logger.Logger;
import org.myshop.order.Order;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class OrderProcessor implements Runnable {
    private final int threadNumber;
    private final BlockingQueue<BatchOrder> queue;
    private final BlockingQueue<BatchOrder> errorQueue;
    private final Logger logger;
    private static final int MAX_RETRIES = 3;

    public OrderProcessor(int threadNumber, Container container) {
        this.threadNumber = threadNumber;
        this.queue = container.getBatchOrdersService().getQueue();
        this.errorQueue = container.getBatchOrdersService().getErrorQueue();
        this.logger = container.getLogger();
    }

    @Override
    public void run() {
        try {
            while (true) {
                BatchOrder batch = this.queue.take();
                this.logger.info("[WORKER-%d] Accepted batch orders...%n", this.threadNumber);
                try {
                    this.process(batch);
                } catch (Exception e) {
                    this.handleFailure(batch, e);
                }
            }
        } catch (InterruptedException e) {
            this.logger.info("[WORKER-%d] Thread interrupted%n", this.threadNumber);
            Thread.currentThread().interrupt();
        }
    }

    private void process(BatchOrder batch) throws Exception {
        List<Order> orders = batch.getOrders();
        this.logger.info("[WORKER-%d] Processing %d orders, attempt %d...%n", this.threadNumber, orders.size(), batch.getRetryCount() + 1);
        // todo: put processing logic
    }

    private void handleFailure(BatchOrder batch, Exception e) {
        batch.incrementRetryCount();
        this.logger.error("[WORKER-%d] Failed to process batch, attempt %d: %s%n", this.threadNumber, batch.getRetryCount() +  1, e.getMessage());

        if (batch.getRetryCount() < MAX_RETRIES) {
            this.queue.offer(batch);
        } else {
            this.logger.error("[WORKER-%d] Batch failed after max retries, moving to error queue.%n", this.threadNumber);
            this.errorQueue.offer(batch);
        }
    }
}
