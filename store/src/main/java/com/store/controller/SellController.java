package com.store.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("sell")
public class SellController {

    private static final AtomicBoolean shouldFail = new AtomicBoolean(false);

    @PostMapping
    public ResponseEntity<String> sell(@RequestBody Long product) {

        if (shouldFail.get()) {
            throw new RuntimeException();
        }

        if (ThreadLocalRandom.current().nextDouble() <= 0.1) {
            triggerFailure();
        }

        return ResponseEntity.ok(UUID.randomUUID().toString());
    }

    private void triggerFailure() {
        shouldFail.set(true);

        new Thread(() -> {
            try {
                Thread.sleep(Duration.ofSeconds(5));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                shouldFail.set(false);
            }
        }).start();
    }

}
