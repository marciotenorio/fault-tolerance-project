package com.ecommerce.service;

import com.ecommerce.client.ExchangeClient2;
import com.ecommerce.client.FidelityClient2;
import com.ecommerce.client.StoreClient;
import com.ecommerce.client.StoreClient2;
import com.ecommerce.dto.Bonus;
import com.ecommerce.dto.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BuyService {

    private final StoreClient storeClient;
    private final StoreClient2 storeClient2;
    private final ExchangeClient2 exchangeClient2;
    private final FidelityClient2 fidelityClient2;

    public BuyService(StoreClient storeClient,
                      StoreClient2 storeClient2,
                      ExchangeClient2 exchangeClient2,
                      FidelityClient2 fidelityClient2) {
        this.storeClient = storeClient;
        this.storeClient2 = storeClient2;
        this.exchangeClient2 = exchangeClient2;
        this.fidelityClient2 = fidelityClient2;
    }

    public String buyFtEnabled(Long productId, Integer user) {

        // Request 1
        Product product = storeClient.getById(productId);

        // todo: Request 2

        // Request 3
        String transactionId = storeClient.sell(product.getId());

        // todo: Request 4

        return transactionId;
    }

    public String buyFtDisabled(Long productId, Integer user) {
        // Request 1
        Product product = storeClient2.getById(productId);

        // Request 2
        BigDecimal coinValue = exchangeClient2.getCoinValue();

        // Request 3
        String transactionId = storeClient2.sell(product.getId());

        // Request 4
        fidelityClient2.applyBonus(new Bonus(user, coinValue.intValue() - 2));

        return transactionId;
    }

}
