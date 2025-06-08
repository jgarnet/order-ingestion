package org.myshop.processor;

import org.myshop.Container;
import org.myshop.logger.Logger;
import org.myshop.order.Order;
import org.myshop.persistence.repository.OrdersRepository;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class OrderProcessor implements Runnable {
    private final BlockingQueue<BatchOrder> queue;
    private final BlockingQueue<BatchOrder> errorQueue;
    private final Logger logger;
    private final OrdersRepository ordersRepository;
    private static final int MAX_RETRIES = 3;

    public OrderProcessor(Container container) {
        this.queue = container.getBatchOrdersService().getQueue();
        this.errorQueue = container.getBatchOrdersService().getErrorQueue();
        this.logger = container.getLogger();
        this.ordersRepository = container.getOrdersRepository();
    }

    @Override
    public void run() {
        try {
            while (true) {
                BatchOrder batch = this.queue.take();
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

    private void process(BatchOrder batch) throws Exception {
        List<Order> orders = batch.getOrders();
        int size = orders.size();
        this.logger.info("Processing %d orders, attempt %d...",  size, batch.getRetryCount() + 1);
        this.ordersRepository.insert(orders);
        this.logger.info("Successfully processed %d orders.", size);
    }

    private void handleFailure(BatchOrder batch, Exception e) {
        batch.incrementRetryCount();
        this.logger.error("Failed to process batch, attempt %d: %s", batch.getRetryCount() +  1, e.getMessage());

        if (batch.getRetryCount() < MAX_RETRIES) {
            this.queue.offer(batch);
        } else {
            this.logger.error("Batch failed after max retries, moving to error queue.");
            this.errorQueue.offer(batch);
        }
    }
}
