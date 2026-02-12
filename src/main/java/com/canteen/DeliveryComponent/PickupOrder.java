package com.canteen.DeliveryComponent;

import com.canteen.OrderingComponent.Order;

public final class PickupOrder implements DeliveryOption {
    @Override
    public OrderType type() {
        return OrderType.PICKUP;
    }

    @Override
    public void apply(Order.Builder builder, String deliveryAddress) {
    }
}
