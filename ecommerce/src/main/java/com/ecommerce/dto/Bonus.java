package com.ecommerce.dto;

import java.math.RoundingMode;

/**
 * @author Márcio Tenório
 * @since 10/12/2024
 */

public class Bonus {

    private Integer user;

    private Integer bonus;

    public Bonus() {
    }

    public Bonus(Integer user, Product product) {
        this.user = user;
        this.bonus = product.getValue().setScale(0, RoundingMode.HALF_UP).intValueExact();
    }

    public Bonus(Integer user, Integer bonus) {
        this.user = user;
        this.bonus = bonus;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer userId) {
        this.user = userId;
    }

    public Integer getBonus() {
        return bonus;
    }

    public void setBonus(Integer bonus) {
        this.bonus = bonus;
    }

}
