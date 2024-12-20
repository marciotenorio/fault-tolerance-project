package com.ecommerce.client;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.math.BigDecimal;

/**
 * @author Márcio Tenório
 * @since 19/12/2024
 */

@HttpExchange
public interface ExchangeClient2 {

    @GetExchange("/exchange")
    BigDecimal getCoinValue();
}
