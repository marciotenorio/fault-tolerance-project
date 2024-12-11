package com.fidelity.bonus;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * @author Márcio Tenório
 * @since 10/12/2024
 */

@Entity
public class ErrorSetter {

    @Id
    private Integer id;

    private boolean isDelayed;

    private LocalDateTime start;

    public ErrorSetter(Integer id, boolean isDelayed, LocalDateTime start) {
        this.id = id;
        this.isDelayed = isDelayed;
        this.start = start;
    }

    public ErrorSetter() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isDelayed() {
        return isDelayed;
    }

    public void setDelayed(boolean delayed) {
        isDelayed = delayed;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }
}
