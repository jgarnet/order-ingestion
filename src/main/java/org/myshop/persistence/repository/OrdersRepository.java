package org.myshop.persistence.repository;

import org.myshop.order.Order;

import java.sql.SQLException;
import java.util.List;

public interface OrdersRepository {
    void insert(List<Order> orders) throws SQLException;
}
