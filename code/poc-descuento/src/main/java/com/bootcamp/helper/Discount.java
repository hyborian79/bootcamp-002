package com.bootcamp.helper;

import java.math.BigDecimal;

public record Discount(
        String productId,
        DiscountType type,
        BigDecimal value
) {
}