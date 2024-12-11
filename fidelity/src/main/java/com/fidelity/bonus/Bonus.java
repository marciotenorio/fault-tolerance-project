package com.fidelity.bonus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * @author Márcio Tenório
 * @since 10/12/2024
 */

@Entity
public class Bonus {

    @Id
    @NotNull
    @Column(name = "user_id")
    @Min(value = 1)
    private Integer user;

    @NotNull
    @Min(value = 1, message = "O bônus precisa ser positivo")
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
}
