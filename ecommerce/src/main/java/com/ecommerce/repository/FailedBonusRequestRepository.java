package com.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.model.FailedBonusRequest;

@Repository
public interface FailedBonusRequestRepository extends JpaRepository<FailedBonusRequest, Long> {

}
