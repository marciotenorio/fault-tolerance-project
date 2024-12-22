package com.ecommerce.service;

import com.ecommerce.client.ExchangeClient;
import com.ecommerce.client.ExchangeClient2;
import com.ecommerce.client.FidelityClient;
import com.ecommerce.client.FidelityClient2;
import com.ecommerce.client.StoreClient;
import com.ecommerce.client.StoreClient2;
import com.ecommerce.config.BusinessException;
import com.ecommerce.dto.Bonus;
import com.ecommerce.dto.Product;
import org.springframework.stereotype.Service;

import com.ecommerce.model.FailedBonusRequest;
import com.ecommerce.repository.FailedBonusRequestRepository;
import java.math.BigDecimal;

@Service
public class BuyService {

    private final StoreClient storeClient;
    private final ExchangeClient exchangeClient;
    private final FidelityClient fidelityClient;
    private final FailedBonusRequestRepository failedBonusRequestRepository;
    private final StoreClient2 storeClient2;
    private final ExchangeClient2 exchangeClient2;
    private final FidelityClient2 fidelityClient2;
    private final ExchangeLocalCache exchangeLocalCache;

    public BuyService(StoreClient storeClient, ExchangeClient exchangeClient, FidelityClient fidelityClient,
            FailedBonusRequestRepository failedBonusRequestRepository, StoreClient2 storeClient2,
            ExchangeClient2 exchangeClient2, FidelityClient2 fidelityClient2, ExchangeLocalCache exchangeLocalCache) {
        this.storeClient = storeClient;
        this.exchangeClient = exchangeClient;
        this.fidelityClient = fidelityClient;
        this.failedBonusRequestRepository = failedBonusRequestRepository;
        this.storeClient2 = storeClient2;
        this.exchangeClient2 = exchangeClient2;
        this.fidelityClient2 = fidelityClient2;
        this.exchangeLocalCache = exchangeLocalCache;
    }

    public String buyFtEnabled(Long productId, Integer user) {
        // Request 1
        Product product = storeClient.getById(productId);

        // Request 2
        BigDecimal coinValue;
        try {
            coinValue = exchangeClient.exchange();
            exchangeLocalCache.saveValue(coinValue);
        } catch (Exception e) {
            coinValue = exchangeLocalCache.getLastValue().orElseThrow(() -> new BusinessException(
                    "Unable to retrieve the currency conversion value at the moment. Please try again later."));
        }

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
