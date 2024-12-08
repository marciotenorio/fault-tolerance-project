package com.store.controller;

import org.springframework.web.bind.annotation.RestController;

import com.store.model.Product;
import com.store.service.ProductService;

import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class ProductController {

    private final Random random = new Random();

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("product/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) throws InterruptedException {

        if (shouldSimulateFailure(0.2)) {
            // Simula falha por omissão
            Thread.sleep(5000);
            return null;
        }

        return ResponseEntity.ok(service.findById(id));
    }

    private boolean shouldSimulateFailure(double probability) {
        return random.nextDouble() < probability;
    }

}
