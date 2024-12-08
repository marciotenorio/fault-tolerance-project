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

    public String buy(Long productId) {

        Product product = storeClient.getById(productId);

        // TODO: retonar identificador aleatório da transação
        return null;
    }

}
