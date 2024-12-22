package com.ecommerce.client;

import java.math.BigDecimal;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface ExchangeClient {

    @GetExchange("/exchange")
    BigDecimal exchange();

}
