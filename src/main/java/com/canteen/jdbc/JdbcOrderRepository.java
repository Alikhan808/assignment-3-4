package com.canteen.jdbc;

import com.canteen.config.DbConnectionFactory;
import com.canteen.domain.Order;
import com.canteen.domain.OrderStatus;
import com.canteen.domain.OrderType;
import com.canteen.repository.OrderRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcOrderRepository implements OrderRepository {
    private final DbConnectionFactory db;

    public JdbcOrderRepository(DbConnectionFactory db) {
        this.db = db;
    }

    @Override
    public long create(Order order) {
        String sql = """
            INSERT INTO orders(customer_id, status, order_type, delivery_address)
            VALUES (?, ?, ?, ?)
            """;
        try (var conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, order.getCustomerId());
            ps.setString(2, order.getStatus().name());
            ps.setString(3, order.getType().name());
            ps.setString(4, order.getDeliveryAddress());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new RuntimeException("Failed to get generated order id");
                return keys.getLong(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("DB error: create order", e);
        }
    }

    @Override
    public Optional<Order> findById(long id) {
        String sql = "SELECT id, customer_id, status, order_type, delivery_address FROM orders WHERE id = ?";
        try (var conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Order shell = new Order.Builder()
                        .customerId(rs.getLong("customer_id"))
                        .type(OrderType.valueOf(rs.getString("order_type")))
                        .deliveryAddress(rs.getString("delivery_address"))
                        .addItem(new com.canteen.domain.OrderItem(0, 0, 0, 1, java.math.BigDecimal.ONE))
                        .build();

                shell.setId(rs.getLong("id"));
                shell.setStatus(OrderStatus.valueOf(rs.getString("status")));
                return Optional.of(shell);
            }
        } catch (Exception e) {
            throw new RuntimeException("DB error: find order", e);
        }
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        String sql = "SELECT id, customer_id, status, order_type, delivery_address FROM orders WHERE status = ? ORDER BY id";
        List<Order> list = new ArrayList<>();
        try (var conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order shell = new Order.Builder()
                            .customerId(rs.getLong("customer_id"))
                            .type(OrderType.valueOf(rs.getString("order_type")))
                            .deliveryAddress(rs.getString("delivery_address"))
                            .addItem(new com.canteen.domain.OrderItem(0, 0, 0, 1, java.math.BigDecimal.ONE))
                            .build();
                    shell.setId(rs.getLong("id"));
                    shell.setStatus(OrderStatus.valueOf(rs.getString("status")));
                    list.add(shell);
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("DB error: list orders by status", e);
        }
    }


    @Override
    public void updateStatus(long id, OrderStatus status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (var conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("DB error: update order status", e);
        }
    }
}
