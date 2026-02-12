package com.canteen.OrderingComponent;

import java.util.List;


public interface OrderItemRepository {
    void addItem(OrderItem item);
    List<OrderItem> findByOrderId(long orderId);
}