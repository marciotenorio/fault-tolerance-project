package com.ecommerce.client;

import com.ecommerce.dto.Product;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * @author Márcio Tenório
 * @since 19/12/2024
 */

@HttpExchange
public interface StoreClient2 {


    @GetExchange(value = "/product/{id}")
    Product getById(@PathVariable Long id);

    @PostExchange(value = "/sell")
    String sell(@RequestBody Long product);
}
