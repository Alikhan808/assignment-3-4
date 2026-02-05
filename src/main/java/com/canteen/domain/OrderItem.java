package com.canteen.domain;

import java.math.BigDecimal;

public record OrderItem(long id, long orderId, long menuItemId, int quantity, BigDecimal unitPrice) { }
