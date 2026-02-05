package com.canteen.service;

import com.canteen.exceptions.PaymentFailedException;

import java.math.BigDecimal;

public class PaymentService {
    public void pay(long ignoredCustomerId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentFailedException("Payment amount must be > 0");
        }
    }
}
