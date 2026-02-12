package com.canteen.DeliveryComponent;

import com.canteen.OrderingComponent.Order;


public interface DeliveryOption {
    OrderType type();
    void apply(Order.Builder builder, String deliveryAddress);
}
