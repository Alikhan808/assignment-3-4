package com.canteen.DeliveryComponent;

import com.canteen.OrderingComponent.Order;

public final class DeliveryOrder implements DeliveryOption {
    @Override
    public OrderType type() {
        return OrderType.DELIVERY;
    }

    @Override
    public void apply(Order.Builder builder, String deliveryAddress) {
        if (deliveryAddress == null || deliveryAddress.isBlank()) {
            throw new IllegalArgumentException("deliveryAddress is required for DELIVERY");
        }
        builder.deliveryAddress(deliveryAddress);
    }
}
