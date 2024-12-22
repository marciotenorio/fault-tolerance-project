package com.ecommerce.dto;

import java.math.BigDecimal;

public class Product {
    
    private Long id;

    private String name;

    private BigDecimal value;

    public Product() {}

    public Product(Long id, String name, BigDecimal value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

}
