package org.myshop.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
    private UUID id = UUID.randomUUID();
    private List<OrderLine> orderLines;
    private Customer customer;
    private LocalDateTime orderDate;

    public UUID getId() {
        return id;
    }

    public Order setId(UUID id) {
        this.id = id;
        return this;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public Order setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
        return this;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Order setCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public Order setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
        return this;
    }
}
