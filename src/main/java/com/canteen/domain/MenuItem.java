package com.canteen.domain;

import java.math.BigDecimal;

public record MenuItem(long id, String name, BigDecimal price, boolean available) { }
