package org.myshop.order;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.myshop.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
    private UUID id = UUID.randomUUID();
    private List<OrderLine> orderLines;
    private Customer customer;
    private LocalDateTime orderDate;

    public void validate() throws ValidationException {
        if (CollectionUtils.isEmpty(orderLines)) {
            throw new ValidationException("Order is missing order lines.");
        }
        if (this.customer == null) {
            throw new ValidationException("Order is missing customer information.");
        }
        if (this.orderDate == null) {
            throw new ValidationException("Order is missing order date.");
        }
        customer.validate();
        for (int i = 0; i < this.orderLines.size(); i++) {
            OrderLine line = this.orderLines.get(i);
            try {
                line.validate();
            } catch (ValidationException e) {
                throw new ValidationException(String.format("Line %d - %s", i, e.getError()));
            }
        }
    }

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
