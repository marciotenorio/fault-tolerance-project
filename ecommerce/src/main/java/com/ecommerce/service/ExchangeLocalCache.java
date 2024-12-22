package com.ecommerce.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class ExchangeLocalCache {
    private volatile BigDecimal lastValue;

    public void saveValue(BigDecimal value) {
        this.lastValue = value;
    }

    public Optional<BigDecimal> getLastValue() {
        return Optional.ofNullable(this.lastValue);
    }
}
