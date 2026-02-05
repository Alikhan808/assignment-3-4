package com.canteen.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {
    private long id;
    private long customerId;
    private OrderStatus status;
    private OrderType type;
    private String deliveryAddress; // null для pickup/dine-in
    private final List<OrderItem> items;

    private Order(Builder builder) {
        this.id = builder.id;
        this.customerId = builder.customerId;
        this.status = builder.status;
        this.type = builder.type;
        this.deliveryAddress = builder.deliveryAddress;
        this.items = new ArrayList<>(builder.items);
    }

    public long getId() { return id; }
    public long getCustomerId() { return customerId; }
    public OrderStatus getStatus() { return status; }
    public OrderType getType() { return type; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }

    public void setId(long id) { this.id = id; }
    public void setStatus(OrderStatus status) { this.status = status; }

    //Builder
    public static class Builder {
        private long id = 0;
        private long customerId;
        private OrderStatus status = OrderStatus.ACTIVE;
        private OrderType type;
        private String deliveryAddress;
        private final List<OrderItem> items = new ArrayList<>();

        public Builder customerId(long customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder type(OrderType type) {
            this.type = type;
            return this;
        }

        public Builder deliveryAddress(String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }

        public Builder addItem(OrderItem item) {
            this.items.add(Objects.requireNonNull(item));
            return this;
        }

        public Order build() {
            if (customerId <= 0) throw new IllegalArgumentException("customerId must be > 0");
            if (type == null) throw new IllegalArgumentException("type is required");
            if (type == OrderType.DELIVERY && (deliveryAddress == null || deliveryAddress.isBlank())) {
                throw new IllegalArgumentException("deliveryAddress is required for DELIVERY");
            }
            if (items.isEmpty()) throw new IllegalArgumentException("order must contain at least 1 item");
            return new Order(this);
        }
    }
}
