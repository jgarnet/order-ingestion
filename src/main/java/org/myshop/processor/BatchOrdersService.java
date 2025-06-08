package org.myshop.processor;

import org.myshop.Container;
import org.myshop.configuration.ConfigurationProperties;
import org.myshop.logger.Logger;
import org.myshop.order.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class BatchOrdersService {
    private final int batchSize;
    private final ExecutorService executorService;
    private final BlockingQueue<BatchOrder> queue = new LinkedBlockingQueue<>();
    private final BlockingQueue<BatchOrder> errorQueue = new LinkedBlockingQueue<>();
    private final Logger logger;

    public BatchOrdersService(Container container) {
        container.setBatchOrdersService(this);
        ConfigurationProperties configurationProperties = container.getConfigurationProperties();
        this.batchSize = configurationProperties.getInteger("BATCH_SIZE", 5);
        final int threadPoolSize = configurationProperties.getInteger("THREAD_POOL_SIZE", 10);
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.logger = container.getLogger();

        this.logger.info("Initializing Order Processing with batch size: %d, thread pool: %d...%n", this.batchSize, threadPoolSize);
        for (int i = 0; i < threadPoolSize; i++) {
            this.executorService.submit(new OrderProcessor(i + 1, container));
        }
    }

    public void ingestOrders(List<Order> orders) {
        this.logger.info("Ingesting %d order(s)...%n", orders.size());
        List<Order> batch = new ArrayList<>();
        for (Order order : orders) {
            batch.add(order);
            if (batch.size() == this.batchSize) {
                this.queue.offer(new BatchOrder(new ArrayList<>(batch)));
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            this.queue.offer(new BatchOrder(batch));
        }
    }

    public BlockingQueue<BatchOrder> getQueue() {
        return queue;
    }

    public BlockingQueue<BatchOrder> getErrorQueue() {
        return errorQueue;
    }

    public void shutdown() {
        this.executorService.shutdown();
    }
}
