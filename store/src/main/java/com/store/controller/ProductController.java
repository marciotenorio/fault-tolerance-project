package com.store.controller;

import org.springframework.web.bind.annotation.RestController;

import com.store.model.Product;
import com.store.service.ProductService;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("product")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) throws InterruptedException {

        if (shouldSimulateFailure(0.2)) {
            // Simula falha por omissão
            Thread.sleep(5000);
            return null;
        }

        return ResponseEntity.ok(service.findById(id));
    }

    private boolean shouldSimulateFailure(double probability) {
        return ThreadLocalRandom.current().nextDouble() <= probability;
    }

}
