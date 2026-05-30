package com.backend.gns.user.infrastructure.repositories;

import com.backend.gns.user.domain.models.BankOperator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankOperatorRepository extends JpaRepository<BankOperator, Long> {
    Optional<BankOperator> findByTrackingId(UUID trackingId);
}
