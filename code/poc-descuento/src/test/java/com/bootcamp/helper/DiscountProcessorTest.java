package com.bootcamp.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiscountProcessorTest {

    private DiscountProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new DiscountProcessor();
    }

    @Test
    void shouldReturnSamePriceWhenPercentageDiscountIsZero() {
        Product product = product("PRODUCT-1", "100.00");
        Discount discount = percentageDiscount("PRODUCT-1", "0");

        BigDecimal result = processor.apply(product, discount);

        assertMoneyEquals("100.00", result);
    }

    @Test
    void shouldApplyPercentageDiscount() {
        Product product = product("PRODUCT-1", "100.00");
        Discount discount = percentageDiscount("PRODUCT-1", "20");

        BigDecimal result = processor.apply(product, discount);

        assertMoneyEquals("80.00", result);
    }

    @Test
    void shouldApplyFixedAmountDiscount() {
        Product product = product("PRODUCT-1", "100.00");
        Discount discount = fixedDiscount("PRODUCT-1", "25.00");

        BigDecimal result = processor.apply(product, discount);

        assertMoneyEquals("75.00", result);
    }

    @Test
    void shouldReturnZeroWhenPercentageDiscountIsOneHundred() {
        Product product = product("PRODUCT-1", "100.00");
        Discount discount = percentageDiscount("PRODUCT-1", "100");

        BigDecimal result = processor.apply(product, discount);

        assertMoneyEquals("0.00", result);
    }

    @Test
    void shouldNotReturnNegativePriceWhenFixedDiscountExceedsPrice() {
        Product product = product("PRODUCT-1", "50.00");
        Discount discount = fixedDiscount("PRODUCT-1", "70.00");

        BigDecimal result = processor.apply(product, discount);

        assertMoneyEquals("0.00", result);
    }

    @Test
    void shouldNotApplyDiscountWhenItBelongsToAnotherProduct() {
        Product product = product("PRODUCT-1", "100.00");
        Discount discount = percentageDiscount("PRODUCT-2", "20");

        BigDecimal result = processor.apply(product, discount);

        assertMoneyEquals("100.00", result);
    }

    @Test
    void shouldRoundResultToTwoDecimalPlaces() {
        Product product = product("PRODUCT-1", "99.99");
        Discount discount = percentageDiscount("PRODUCT-1", "15");

        BigDecimal result = processor.apply(product, discount);

        assertMoneyEquals("84.99", result);
    }

    @ParameterizedTest
    @CsvSource({
            "-1",
            "-10",
            "100.01",
            "101"
    })
    void shouldRejectInvalidPercentageDiscount(String percentage) {
        Product product = product("PRODUCT-1", "100.00");
        Discount discount = percentageDiscount("PRODUCT-1", percentage);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processor.apply(product, discount)
        );

        assertEquals(
                "Percentage discount must be between 0 and 100",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNegativeFixedDiscount() {
        Product product = product("PRODUCT-1", "100.00");
        Discount discount = fixedDiscount("PRODUCT-1", "-5.00");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processor.apply(product, discount)
        );

        assertEquals(
                "Fixed discount cannot be negative",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNegativeProductPrice() {
        Product product = product("PRODUCT-1", "-10.00");
        Discount discount = percentageDiscount("PRODUCT-1", "20");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processor.apply(product, discount)
        );

        assertEquals(
                "Product price cannot be negative",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullProduct() {
        Discount discount = percentageDiscount("PRODUCT-1", "20");

        assertThrows(
                IllegalArgumentException.class,
                () -> processor.apply(null, discount)
        );
    }

    @Test
    void shouldRejectNullDiscount() {
        Product product = product("PRODUCT-1", "100.00");

        assertThrows(
                IllegalArgumentException.class,
                () -> processor.apply(product, null)
        );
    }

    private Product product(String id, String price) {
        return new Product(id, new BigDecimal(price));
    }

    private Discount percentageDiscount(String productId, String value) {
        return new Discount(
                productId,
                DiscountType.PERCENTAGE,
                new BigDecimal(value)
        );
    }

    private Discount fixedDiscount(String productId, String value) {
        return new Discount(
                productId,
                DiscountType.FIXED_AMOUNT,
                new BigDecimal(value)
        );
    }

    private void assertMoneyEquals(String expected, BigDecimal actual) {
        assertEquals(
                0,
                new BigDecimal(expected).compareTo(actual),
                "El importe obtenido no coincide con el esperado"
        );
    }
}