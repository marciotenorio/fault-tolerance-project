package com.store.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class Product {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // "value" é uma palavra reservada no H2
    private BigDecimal price;

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
        return price;
    }

    public void setValue(BigDecimal value) {
        this.price = value;
    }

}
