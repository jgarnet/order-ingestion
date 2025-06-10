package org.myshop.batch;

import org.myshop.order.Order;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BatchOrders {
    private final UUID id = UUID.randomUUID();
    private final List<Order> orders;
    private int retryCount;
    private int requeueCount;
    private Set<String> errors = new HashSet<>();

    public BatchOrders(List<Order> orders) {
        this.orders = orders;
    }

    public UUID getId() {
        return id;
    }

    public List<Order> getOrders() {
        return this.orders;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public int getRequeueCount() {
        return requeueCount;
    }

    public void requeue() {
        this.requeueCount++;
        this.retryCount = 0;
    }

    public Set<String> getErrors() {
        return this.errors;
    }
}
