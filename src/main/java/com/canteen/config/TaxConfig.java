package com.canteen.config;

import java.math.BigDecimal;

public final class TaxConfig {
    private static final TaxConfig INSTANCE = new TaxConfig();
    private BigDecimal taxRate = new BigDecimal("0.10"); // 10%
    private TaxConfig() {}
    public static TaxConfig getInstance() {
        return INSTANCE;
    }
    public BigDecimal getTaxRate() {
        return taxRate;
    }
    public void setTaxRate(BigDecimal taxRate) {
        if (taxRate == null || taxRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("taxRate must be >= 0");
        }
        this.taxRate = taxRate;
    }
}
