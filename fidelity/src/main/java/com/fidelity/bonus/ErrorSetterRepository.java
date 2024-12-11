package com.fidelity.bonus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Márcio Tenório
 * @since 10/12/2024
 */

@Repository
public interface ErrorSetterRepository extends JpaRepository<ErrorSetter, Integer> {
}
