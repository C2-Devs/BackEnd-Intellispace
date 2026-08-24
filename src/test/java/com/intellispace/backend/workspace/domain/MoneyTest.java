package com.intellispace.backend.workspace.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;
import com.intellispace.backend.workspace.domain.Record.Money;

class MoneyTest {

    @Test
    void acceptsValidAmountAndCurrency() {
        Money money = new Money(new BigDecimal("499.99"), "INR");
        assertThat(money.amount()).isEqualByComparingTo("499.99");
    }

    @Test
    void rejectsNegativeAmount() {
        assertThatThrownBy(() -> new Money(new BigDecimal("-1.00"), "INR"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void acceptsZero() {
        // Zero is a legitimate budget (or price) — only negative is nonsensical. Worth a passing test,
        // not just an absence of failing ones, since it's the boundary right next to the rejected case.
        assertThatCode(() -> new Money(BigDecimal.ZERO, "INR")).doesNotThrowAnyException();
    }

    @Test
    void rejectsUnrecognizedCurrencyCode() {
        assertThatThrownBy(() -> new Money(new BigDecimal("10.00"), "XYZ"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("XYZ");
    }

    @Test
    void rejectsNullAmount() {
        assertThatThrownBy(() -> new Money(null, "INR")).isInstanceOf(IllegalArgumentException.class);
    }
}
