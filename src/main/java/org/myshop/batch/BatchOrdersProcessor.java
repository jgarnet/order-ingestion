package org.myshop.batch;

import org.myshop.Constants;
import org.myshop.logger.Logger;
import org.myshop.order.Order;
import org.myshop.persistence.repository.OrdersRepository;
import org.myshop.queue.ErrorStore;
import org.myshop.queue.QueueProvider;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.UUID;

public class BatchOrdersProcessor implements Runnable {
    private final QueueProvider<BatchOrders> queue;
    private final ErrorStore<BatchOrders, UUID> errorStore;
    private final Logger logger;
    private final OrdersRepository ordersRepository;
    private static final int MAX_RETRIES = 3;

    @Inject
    public BatchOrdersProcessor(@Named(Constants.BATCH_ORDERS_QUEUE) QueueProvider<BatchOrders> queue,
                                @Named(Constants.ERROR_STORE) ErrorStore<BatchOrders, UUID> errorStore,
                                Logger logger,
                                OrdersRepository ordersRepository
    ) {
        this.queue = queue;
        this.errorStore = errorStore;
        this.logger = logger;
        this.ordersRepository = ordersRepository;
    }

    @Override
    public void run() {
        while (true) {
            BatchOrders batch = this.queue.poll();
            this.logger.info("Accepted batch orders...");
            try {
                this.process(batch);
            } catch (Exception e) {
                this.handleFailure(batch, e);
            }
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
            batch.getErrors().add(e.getMessage());
            this.queue.put(batch);
        } else {
            this.logger.error("Batch (id=%s) failed after max retries, moving to error queue.", batch.getId());
            this.errorStore.put(batch.getId(), batch);
        }
    }
}
