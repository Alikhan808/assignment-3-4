package com.canteen.jdbc;

import com.canteen.config.DbConnectionFactory;
import com.canteen.domain.MenuItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMenuItemRepository implements MenuItemRepository {
    private final DbConnectionFactory db;

    public JdbcMenuItemRepository(DbConnectionFactory db) {
        this.db = db;
    }

    @Override
    public Optional<MenuItem> findById(long id) {
        String sql = "SELECT id, name, price, available FROM menu_items WHERE id = ?";
        try (var conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new MenuItem(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price"),
                        rs.getBoolean("available")
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("DB error: find menu item", e);
        }
    }

    @Override
    public List<MenuItem> findAllAvailable() {
        String sql = "SELECT id, name, price, available FROM menu_items WHERE available = TRUE ORDER BY id";
        List<MenuItem> list = new ArrayList<>();
        try (var conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new MenuItem(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price"),
                        rs.getBoolean("available")
                ));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("DB error: list available menu items", e);
        }
    }

    @Override
    public void setAvailability(long id, boolean available) {
        String sql = "UPDATE menu_items SET available = ? WHERE id = ?";
        try (var conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, available);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("DB error: set availability", e);
        }
    }
}