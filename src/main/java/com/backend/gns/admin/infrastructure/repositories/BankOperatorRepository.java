package com.backend.gns.admin.infrastructure.repositories;

import com.backend.gns.admin.domain.models.BankOperator;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankOperatorRepository extends JpaRepository<BankOperator, Long> {
  Optional<BankOperator> findByTrackingId(UUID trackingId);
}
