package org.myshop.processor;

import org.apache.commons.collections4.CollectionUtils;
import org.myshop.Container;
import org.myshop.configuration.ConfigurationProperties;
import org.myshop.exception.ValidationException;
import org.myshop.logger.Logger;
import org.myshop.order.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BatchOrdersService {
    private final int batchSize;
    private final Integer maxOrdersPerRequest;
    private final ExecutorService executorService;
    private final BlockingQueue<BatchOrder> queue = new LinkedBlockingQueue<>();
    private final BlockingQueue<BatchOrder> errorQueue = new LinkedBlockingQueue<>();
    private final Logger logger;

    public BatchOrdersService(Container container) {
        container.setBatchOrdersService(this);
        ConfigurationProperties properties = container.getConfigurationProperties();
        this.batchSize = properties.getInteger("BATCH_SIZE", 5);
        this.maxOrdersPerRequest = properties.getInteger("MAX_ORDERS_PER_REQUEST", 1000);
        final int threadPoolSize = properties.getInteger("THREAD_POOL_SIZE", 10);
        this.executorService = Executors.newFixedThreadPool(threadPoolSize, new ThreadFactory() {
            private int count = 1;

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("BATCH-" + count++);
                return t;
            }
        });
        this.logger = container.getLogger();

        this.logger.info("Initializing Order Processing with batch size: %d, thread pool: %d...", this.batchSize, threadPoolSize);
        for (int i = 0; i < threadPoolSize; i++) {
            this.executorService.submit(new OrderProcessor(container));
        }
    }

    public List<String> validate(List<Order> orders) {
        List<String> errors = new ArrayList<>();
        if (CollectionUtils.isEmpty(orders) || orders.size() > this.maxOrdersPerRequest) {
            errors.add(String.format("Request must contain between 1 and %d orders.", maxOrdersPerRequest));
        }
        for (int i = 0; i < orders.size(); i++) {
            try {
                Order order = orders.get(i);
                order.validate();
            } catch (ValidationException e) {
                errors.add(String.format("Order %d - %s", i, e.getError()));
            }
        }
        return errors;
    }

    public void ingestOrders(List<Order> orders) {
        this.logger.info("Ingesting %d order(s)...", orders.size());
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
