package com.ecommerce.service;

import com.ecommerce.client.ExchangeClient2;
import com.ecommerce.client.FidelityClient;
import com.ecommerce.client.FidelityClient2;
import com.ecommerce.client.StoreClient;
import com.ecommerce.client.StoreClient2;
import com.ecommerce.config.BusinessException;
import com.ecommerce.dto.Bonus;
import com.ecommerce.dto.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ecommerce.model.FailedBonusRequest;
import com.ecommerce.repository.FailedBonusRequestRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class BuyService {

    private final StoreClient storeClient;
    private final FidelityClient fidelityClient;
    private final FailedBonusRequestRepository failedBonusRequestRepository;
    private final StoreClient2 storeClient2;
    private final ExchangeClient2 exchangeClient2;
    private final FidelityClient2 fidelityClient2;
    private final ProductService productService;

    public BuyService(StoreClient storeClient, FidelityClient fidelityClient,
            FailedBonusRequestRepository failedBonusRequestRepository, StoreClient2 storeClient2,
            ExchangeClient2 exchangeClient2, FidelityClient2 fidelityClient2, ProductService productService) {
        this.storeClient = storeClient;
        this.fidelityClient = fidelityClient;
        this.failedBonusRequestRepository = failedBonusRequestRepository;
        this.storeClient2 = storeClient2;
        this.exchangeClient2 = exchangeClient2;
        this.fidelityClient2 = fidelityClient2;
        this.productService = productService;
    }

    public String buyFtEnabled(Long productId, Integer user) {
        // Request 1
        Product product = productService.getById(productId);

        // todo: Request 2

        // Request 3
        String transactionId = storeClient.sell(product.getId());

        // Request 4
        Bonus bonus = new Bonus(user, product);
        try {
            fidelityClient.bonus(new Bonus(user, product));
        } catch (Exception e) {
            FailedBonusRequest failedBonusRequest = new FailedBonusRequest(bonus);
            failedBonusRequestRepository.save(failedBonusRequest);
        }

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
        Bonus bonus = new Bonus(user, product);
        fidelityClient2.applyBonus(bonus);

        return transactionId;
    }

}
