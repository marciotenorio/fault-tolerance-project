package com.ecommerce.client;

import com.ecommerce.dto.Bonus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * @author Márcio Tenório
 * @since 19/12/2024
 */

@HttpExchange("bonus")
public interface FidelityClient2 {

    @PostExchange
    void applyBonus(@RequestBody Bonus bonus);
}
