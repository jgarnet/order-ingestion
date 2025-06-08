package org.myshop.processor;

import org.myshop.order.Order;

import java.util.List;

public class BatchOrder {
    private final List<Order> orders;
    private int retryCount;

    public BatchOrder(List<Order> orders) {
        this.orders = orders;
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
}
