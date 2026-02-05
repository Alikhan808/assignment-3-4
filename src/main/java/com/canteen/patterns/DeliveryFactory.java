package com.canteen.patterns;

import com.canteen.domain.OrderType;

public final class DeliveryFactory {
    private DeliveryFactory() {}

    public static OrderType fromString(String raw) {
        if (raw == null) throw new IllegalArgumentException("order type is required");
        return switch (raw.trim().toUpperCase()) {
            case "PICKUP" -> OrderType.PICKUP;
            case "DELIVERY" -> OrderType.DELIVERY;
            case "DINEIN", "DINE_IN" -> OrderType.DINE_IN;
            default -> throw new IllegalArgumentException("Unknown order type: " + raw);
        };
    }
}
