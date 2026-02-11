package com.canteen.patterns;

import com.canteen.domain.Order;
import com.canteen.domain.OrderType;

public final class PickupOrder implements DeliveryOption {
    @Override
    public OrderType type() {
        return OrderType.PICKUP;
    }

    @Override
    public void apply(Order.Builder builder, String deliveryAddress) {
    }
}
