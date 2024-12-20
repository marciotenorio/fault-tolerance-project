package com.ecommerce.jobs;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecommerce.client.FidelityClient;
import com.ecommerce.dto.Bonus;
import com.ecommerce.model.FailedBonusRequest;
import com.ecommerce.repository.FailedBonusRequestRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@EnableAsync
public class RetryRequestBonusJob {

    private final FailedBonusRequestRepository repository;
    private final FidelityClient fidelityClient;

    public RetryRequestBonusJob(FailedBonusRequestRepository repository, FidelityClient fidelityClient) {
        this.repository = repository;
        this.fidelityClient = fidelityClient;
    }

    @Async
    @Scheduled(fixedDelayString = "5", timeUnit = TimeUnit.MINUTES)
    public void retryRequestsBonus() {
        List<FailedBonusRequest> requests = repository.findAll();
        requests.forEach(req -> {
            Bonus body = new Bonus(req.getUser(), req.getBonus());
            fidelityClient.bonus(body);
            repository.deleteById(req.getId());
        });
    }

}
