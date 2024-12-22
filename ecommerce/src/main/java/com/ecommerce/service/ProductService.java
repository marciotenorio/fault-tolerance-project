package com.ecommerce.service;

import com.ecommerce.client.StoreClient2;
import com.ecommerce.config.BusinessException;
import com.ecommerce.dto.Product;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author Márcio Tenório
 * @since 21/12/2024
 */

@Service
public class ProductService {

    private final StoreClient2 storeClient;

    public ProductService(StoreClient2 storeClient) {
        this.storeClient = storeClient;
    }

    @Retry(name = "retryGetById", fallbackMethod = "fallbackGetById")
    public Product getById(Long productId) {
        return storeClient.getById(productId);
    }

    public Product fallbackGetById(Long productId, Throwable t) {
        Product[] productsHotCache = {
                null,
                new Product(1L, "Laptop", BigDecimal.valueOf(899.99)),
                new Product(2L, "Smartphone", BigDecimal.valueOf(499.50)),
                new Product(3L, "Tablet", BigDecimal.valueOf(299.99)),
                new Product(4L, "Headphones", BigDecimal.valueOf(79.99)),
                new Product(5L, "Smartwatch", BigDecimal.valueOf(199.99)),
                new Product(6L, "Keyboard", BigDecimal.valueOf(49.99)),
                new Product(7L, "Mouse", BigDecimal.valueOf(29.99)),
                new Product(8L, "Monitor", BigDecimal.valueOf(169.50)),
                new Product(9L, "External Hard Drive", BigDecimal.valueOf(120.00)),
                new Product(10L, "Webcam", BigDecimal.valueOf(39.99)),
                new Product(11L, "Speakers", BigDecimal.valueOf(59.99)),
                new Product(12L, "Desk Lamp", BigDecimal.valueOf(29.50)),
                new Product(13L, "Phone Case", BigDecimal.valueOf(19.99)),
                new Product(14L, "USB Flash Drive", BigDecimal.valueOf(12.99)),
                new Product(15L, "Bluetooth Adapter", BigDecimal.valueOf(15.99))
        };

        if(productId > (productsHotCache.length - 1) || productId == 0) {
            throw new BusinessException("Product id is out of range");
        }

        return productsHotCache[productId.intValue()];
    }
}
