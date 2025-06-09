package org.myshop.persistence.repository;

import org.myshop.order.Order;
import org.myshop.order.OrderLine;
import org.myshop.persistence.database.Database;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class MySqlOrdersRepository implements OrdersRepository {
    private static final String INSERT_ORDER = "INSERT INTO orders (id, order_date) VALUES (?, ?)";
    private static final String INSERT_CUSTOMER = "INSERT INTO order_customers (order_id, first_name, last_name, email, phone) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_LINE = "INSERT INTO order_lines (order_id, line_number, product_id, quantity) VALUES (?, ?, ?, ?)";

    private final Database database;

    @Inject
    public MySqlOrdersRepository(Database database) {
        this.database = database;
    }

    @Override
    public void insert(List<Order> orders) throws SQLException {
        try (Connection conn = this.database.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (Order order : orders) {
                    byte[] id = this.uuidToBytes(order.getId());
                    // insert order
                    try (PreparedStatement stmt = conn.prepareStatement(INSERT_ORDER)) {
                        stmt.setBytes(1, id);
                        stmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));
                        stmt.execute();
                    }
                    // insert customer
                    try (PreparedStatement stmt = conn.prepareStatement(INSERT_CUSTOMER)) {
                        stmt.setBytes(1, id);
                        stmt.setString(2, order.getCustomer().getFirstName());
                        stmt.setString(3, order.getCustomer().getLastName());
                        stmt.setString(4, order.getCustomer().getEmailAddress());
                        stmt.setString(5, order.getCustomer().getPhoneNumber());
                        stmt.execute();
                    }
                    // insert order lines
                    List<OrderLine> lines = order.getOrderLines();
                    for (int i = 0; i < lines.size(); i++) {
                        OrderLine line = lines.get(i);
                        try (PreparedStatement stmt = conn.prepareStatement(INSERT_LINE)) {
                            stmt.setBytes(1, id);
                            stmt.setInt(2, i);
                            stmt.setString(3, line.getProduct().getId());
                            stmt.setInt(4, line.getQuantity());
                            stmt.execute();
                        }
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public byte[] uuidToBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    public UUID bytesToUUID(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long mostSig = buffer.getLong();
        long leastSig = buffer.getLong();
        return new UUID(mostSig, leastSig);
    }
}
