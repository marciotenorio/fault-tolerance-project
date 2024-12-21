package com.ecommerce.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.ecommerce.dto.Bonus;

import jakarta.validation.Valid;

@HttpExchange
public interface FidelityClient {

    @PostExchange(value = "bonus")
    public void bonus(@Valid @RequestBody Bonus bonusDto);

}
