package com.canteen.patterns;

public final class DeliveryFactory {
    private DeliveryFactory() {}

    public static DeliveryOption fromString(String raw) {
        if (raw == null) throw new IllegalArgumentException("order type is required");

        return switch (raw.trim().toUpperCase()) {
            case "PICKUP" -> new PickupOrder();
            case "DELIVERY" -> new DeliveryOrder();
            case "DINEIN", "DINE_IN" -> new DineInOrder();
            default -> throw new IllegalArgumentException("Unknown order type: " + raw);
        };
    }
}
