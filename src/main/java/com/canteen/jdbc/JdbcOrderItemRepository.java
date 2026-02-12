package com.canteen.jdbc;

import com.canteen.config.DbConnectionFactory;
import com.canteen.OrderingComponent.OrderItem;
import com.canteen.OrderingComponent.OrderItemRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JdbcOrderItemRepository implements OrderItemRepository {
    private final DbConnectionFactory db;

    public JdbcOrderItemRepository(DbConnectionFactory db) {
        this.db = db;
    }
    @Override
    public void addItem(OrderItem item) {
        String sql = """
            INSERT INTO order_items(order_id, menu_item_id, quantity, unit_price)
            VALUES (?, ?, ?, ?)
            """;
        try (var conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, item.orderId());
            ps.setLong(2, item.menuItemId());
            ps.setInt(3, item.quantity());
            ps.setBigDecimal(4, item.unitPrice());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("DB error: add order item", e);
        }
    }

    @Override
    public List<OrderItem> findByOrderId(long orderId) {
        String sql = "SELECT id, order_id, menu_item_id, quantity, unit_price FROM order_items WHERE order_id = ? ORDER BY id";
        List<OrderItem> list = new ArrayList<>();
        try (var conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new OrderItem(
                            rs.getLong("id"),
                            rs.getLong("order_id"),
                            rs.getLong("menu_item_id"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("unit_price")
                    ));
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("DB error: list order items", e);
        }
    }
}
