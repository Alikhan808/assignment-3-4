package com.canteen.patterns;

import com.canteen.domain.Order;
import com.canteen.domain.OrderType;


public interface DeliveryOption {
    OrderType type();
    void apply(Order.Builder builder, String deliveryAddress);
}
