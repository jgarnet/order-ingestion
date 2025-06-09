package org.myshop.persistence.repository;

import org.myshop.logger.Logger;
import org.myshop.order.Order;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

public class StubOrdersRepository implements OrdersRepository {
    private final Logger logger;

    @Inject
    public StubOrdersRepository(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void insert(List<Order> orders) throws SQLException {
        this.logger.info("Inserting %s orders...", orders.size());
    }
}
