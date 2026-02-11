package com.canteen.patterns;

import com.canteen.domain.Order;
import com.canteen.domain.OrderType;

public final class DineInOrder implements DeliveryOption {
    @Override
    public OrderType type() {
        return OrderType.DINE_IN;
    }

    @Override
    public void apply(Order.Builder builder, String deliveryAddress) {

    }
}
