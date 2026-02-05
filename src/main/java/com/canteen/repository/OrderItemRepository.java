package com.canteen.repository;

import com.canteen.domain.OrderItem;
import java.util.List;

public interface OrderItemRepository {
    void addItem(OrderItem item);
    List<OrderItem> findByOrderId(long orderId);
}