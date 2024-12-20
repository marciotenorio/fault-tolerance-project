package com.ecommerce.service;

import org.springframework.stereotype.Service;

import com.ecommerce.client.FidelityClient;
import com.ecommerce.client.StoreClient;
import com.ecommerce.dto.Bonus;
import com.ecommerce.dto.Product;
import com.ecommerce.model.FailedBonusRequest;
import com.ecommerce.repository.FailedBonusRequestRepository;

@Service
public class BuyService {

    private final StoreClient storeClient;
    private final FidelityClient fidelityClient;
    private final FailedBonusRequestRepository failedBonusRequestRepository;

    public BuyService(StoreClient storeClient, FidelityClient fidelityClient,
            FailedBonusRequestRepository failedBonusRequestRepository) {
        this.storeClient = storeClient;
        this.fidelityClient = fidelityClient;
        this.failedBonusRequestRepository = failedBonusRequestRepository;
    }

    public String buyFtEnabled(Long productId, Integer user) {
        // Request 1
        Product product = storeClient.getById(productId);

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
        Product product = storeClient.getById(productId);

        // todo: Request 2

        // Request 3
        String transactionId = storeClient.sell(product.getId());

        // Request 4
        Bonus bonus = new Bonus(user, product);
        fidelityClient.bonus(bonus);

        return transactionId;
    }

}
