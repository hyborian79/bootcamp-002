package com.bootcamp.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DiscountProcessor {

    private static final BigDecimal ONE_HUNDRED =
            new BigDecimal("100");

    private static final BigDecimal ZERO =
            new BigDecimal("0.00");

    public BigDecimal apply(Product product, Discount discount) {
        validateRequiredValues(product, discount);
        validateProduct(product);
        validateDiscount(discount);

        if (!product.id().equals(discount.productId())) {
            return money(product.price());
        }

        BigDecimal finalPrice = switch (discount.type()) {
            case PERCENTAGE -> applyPercentageDiscount(
                    product.price(),
                    discount.value()
            );
            case FIXED_AMOUNT -> product.price()
                    .subtract(discount.value());
        };

        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            return ZERO;
        }

        return money(finalPrice);
    }

    private BigDecimal applyPercentageDiscount(
            BigDecimal price,
            BigDecimal percentage
    ) {
        BigDecimal discountAmount = price
                .multiply(percentage)
                .divide(ONE_HUNDRED, 10, RoundingMode.HALF_UP);

        return price.subtract(discountAmount);
    }

    private void validateRequiredValues(
            Product product,
            Discount discount
    ) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Product cannot be null"
            );
        }

        if (discount == null) {
            throw new IllegalArgumentException(
                    "Discount cannot be null"
            );
        }

        if (product.id() == null || product.price() == null) {
            throw new IllegalArgumentException(
                    "Product fields cannot be null"
            );
        }

        if (discount.productId() == null
                || discount.type() == null
                || discount.value() == null) {
            throw new IllegalArgumentException(
                    "Discount fields cannot be null"
            );
        }
    }

    private void validateProduct(Product product) {
        if (product.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Product price cannot be negative"
            );
        }
    }

    private void validateDiscount(Discount discount) {
        switch (discount.type()) {
            case PERCENTAGE -> validatePercentage(discount.value());
            case FIXED_AMOUNT -> validateFixedAmount(discount.value());
        }
    }

    private void validatePercentage(BigDecimal percentage) {
        boolean isNegative =
                percentage.compareTo(BigDecimal.ZERO) < 0;

        boolean exceedsMaximum =
                percentage.compareTo(ONE_HUNDRED) > 0;

        if (isNegative || exceedsMaximum) {
            throw new IllegalArgumentException(
                    "Percentage discount must be between 0 and 100"
            );
        }
    }

    private void validateFixedAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Fixed discount cannot be negative"
            );
        }
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}