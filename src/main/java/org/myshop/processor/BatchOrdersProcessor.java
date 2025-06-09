package org.myshop.processor;

import org.myshop.logger.Logger;
import org.myshop.order.Order;
import org.myshop.persistence.repository.OrdersRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class BatchOrdersProcessor implements Runnable {
    private final BlockingQueue<BatchOrders> queue;
    private final BlockingQueue<BatchOrders> errorQueue;
    private final Logger logger;
    private final OrdersRepository ordersRepository;
    private static final int MAX_RETRIES = 3;

    @Inject
    public BatchOrdersProcessor(Queues queues, Logger logger, OrdersRepository ordersRepository) {
        this.queue = queues.getQueue();
        this.errorQueue = queues.getErrorQueue();
        this.logger = logger;
        this.ordersRepository = ordersRepository;
    }

    @Override
    public void run() {
        try {
            while (true) {
                BatchOrders batch = this.queue.take();
                this.logger.info("Accepted batch orders...");
                try {
                    this.process(batch);
                } catch (Exception e) {
                    this.handleFailure(batch, e);
                }
            }
        } catch (InterruptedException e) {
            this.logger.info("Thread interrupted");
            Thread.currentThread().interrupt();
        }
    }

    private void process(BatchOrders batch) throws Exception {
        List<Order> orders = batch.getOrders();
        int size = orders.size();
        this.logger.info("Processing %d orders, retryCount=%d...",  size, batch.getRetryCount());
        this.ordersRepository.insert(orders);
        this.logger.info("Successfully processed %d orders.", size);
    }

    private void handleFailure(BatchOrders batch, Exception e) {
        batch.incrementRetryCount();
        this.logger.error("Failed to process batch, attempt %d: %s", batch.getRetryCount(), e.getMessage());

        if (batch.getRetryCount() < MAX_RETRIES) {
            this.queue.offer(batch);
        } else {
            this.logger.error("Batch failed after max retries, moving to error queue.");
            this.errorQueue.offer(batch);
        }
    }
}
