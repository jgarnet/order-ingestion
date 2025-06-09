package org.myshop.batch;

import org.apache.commons.collections4.CollectionUtils;
import org.myshop.configuration.ConfigurationProperties;
import org.myshop.exception.ValidationException;
import org.myshop.logger.Logger;
import org.myshop.order.Order;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Singleton
public class BatchOrdersService {
    private final int batchSize;
    private final Integer maxOrdersPerRequest;
    private final ExecutorService executorService;
    private final Logger logger;
    private final Queues queues;

    @Inject
    public BatchOrdersService(ConfigurationProperties properties, Logger logger, Provider<BatchOrdersProcessor> processorProvider, Queues queues) {
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
        this.logger = logger;
        this.queues = queues;

        this.logger.info("Initializing Order Processing with batch size: %d, thread pool: %d...", this.batchSize, threadPoolSize);
        for (int i = 0; i < threadPoolSize; i++) {
            this.executorService.submit(processorProvider.get());
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
                this.queues.getQueue().offer(new BatchOrders(new ArrayList<>(batch)));
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            this.queues.getQueue().offer(new BatchOrders(batch));
        }
    }

    public void shutdown() {
        this.executorService.shutdown();
    }
}
