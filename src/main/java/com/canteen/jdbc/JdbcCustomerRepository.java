package com.canteen.jdbc;

import com.canteen.config.DbConnectionFactory;
import com.canteen.domain.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class JdbcCustomerRepository implements CustomerRepository {
    private final DbConnectionFactory db;

    public JdbcCustomerRepository(DbConnectionFactory db) {
        this.db = db;
    }

    @Override
    public Optional<Customer> findById(long id) {
        String sql = "SELECT id, name, phone FROM customers WHERE id = ?";
        try (var conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Customer(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("phone")
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("DB error: find customer", e);
        }
    }
}