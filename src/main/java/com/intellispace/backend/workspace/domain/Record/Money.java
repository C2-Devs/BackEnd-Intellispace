package com.intellispace.backend.workspace.domain.Record;

import java.math.BigDecimal;
import java.util.Currency;

public record Money(BigDecimal amount, String currency) {

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("Money amount must not be null");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Money amount must not be negative: " + amount);
        }
        try {
            Currency.getInstance(currency);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Not a recognized ISO 4217 currency code: " + currency, e);
        }
    }
}