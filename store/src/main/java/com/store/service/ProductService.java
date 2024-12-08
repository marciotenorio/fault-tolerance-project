package com.store.service;

import org.springframework.stereotype.Service;

import com.store.exception.ResourceNotFoundException;
import com.store.model.Product;
import com.store.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found."));
    }

}
