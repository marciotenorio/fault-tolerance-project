package com.ecommerce.dto;

/**
 * @author Márcio Tenório
 * @since 10/12/2024
 */

public class Bonus {

    private Integer user;

    private Integer bonus;

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

    public Bonus(Integer user, Integer bonus) {
        this.user = user;
        this.bonus = bonus;
    }

    public Bonus() {}
}
