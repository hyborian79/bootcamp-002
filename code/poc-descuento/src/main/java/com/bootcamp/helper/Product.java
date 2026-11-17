package com.bootcamp.helper;

import java.math.BigDecimal;

public record Product(
        String id,
        BigDecimal price
) {
}