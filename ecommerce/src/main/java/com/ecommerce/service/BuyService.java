package com.ecommerce.service;

import org.springframework.stereotype.Service;

import com.ecommerce.client.StoreClient;
import com.ecommerce.dto.Product;

@Service
public class BuyService {

    private final StoreClient storeClient;

    public BuyService(StoreClient storeClient) {
        this.storeClient = storeClient;
    }

    public String buyFtEnabled(Long productId, Long user) {

        // Request 1
        Product product = storeClient.getById(productId);

        // todo: Request 2

        // Request 3
        String transactionId = storeClient.sell(product.getId());

        // todo: Request 4

        return transactionId;
    }

    public String buyFtDisabled(Long productId, Long user) {
        // Request 1
        Product product = storeClient.getById(productId);

        // todo: Request 2

        // Request 3
        String transactionId = storeClient.sell(product.getId());

        // todo: Request 4

        return transactionId;
    }

}
