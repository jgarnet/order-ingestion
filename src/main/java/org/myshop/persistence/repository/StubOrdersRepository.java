package org.myshop.persistence.repository;

import org.myshop.Container;
import org.myshop.order.Order;

import java.sql.SQLException;
import java.util.List;

public class StubOrdersRepository implements OrdersRepository {
    private final Container container;

    public StubOrdersRepository(Container container) {
        container.setOrdersRepository(this);
        this.container = container;
    }

    @Override
    public void insert(List<Order> orders) throws SQLException {
        this.container.getLogger().info("Inserting %s orders...", orders.size());
    }
}
