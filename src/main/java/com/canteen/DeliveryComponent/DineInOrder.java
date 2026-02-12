package com.canteen.DeliveryComponent;

import com.canteen.OrderingComponent.Order;

public final class DineInOrder implements DeliveryOption {
    @Override
    public OrderType type() {
        return OrderType.DINE_IN;
    }

    @Override
    public void apply(Order.Builder builder, String deliveryAddress) {

    }
}
