package com.canteen.repository;

import com.canteen.domain.Order;
import com.canteen.domain.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    long create(Order order);
    Optional<Order> findById(long id);
    List<Order> findByStatus(OrderStatus status);
    void updateStatus(long id, OrderStatus status);
}