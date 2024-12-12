package com.ecommerce.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.ecommerce.dto.Product;

@HttpExchange
public interface StoreClient {

    @GetExchange(value = "/product/{id}")
    Product getById(@PathVariable Long id);

    @PostExchange(value = "/sell")
    String sell(@RequestBody Long product);

}
