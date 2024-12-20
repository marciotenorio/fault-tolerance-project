package com.ecommerce.model;

import java.time.LocalDateTime;

import com.ecommerce.dto.Bonus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class FailedBonusRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer bonus;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    public FailedBonusRequest() {
    }

    public FailedBonusRequest(Bonus bonus) {
        this.userId = bonus.getUser();
        this.bonus = bonus.getBonus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUser() {
        return userId;
    }

    public void setUser(Integer user) {
        this.userId = user;
    }

    public Integer getBonus() {
        return bonus;
    }

    public void setBonus(Integer bonus) {
        this.bonus = bonus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
