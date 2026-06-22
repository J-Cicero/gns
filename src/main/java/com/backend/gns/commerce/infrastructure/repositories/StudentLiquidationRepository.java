package com.backend.gns.commerce.infrastructure.repositories;

import com.backend.gns.commerce.domain.models.StudentLiquidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentLiquidationRepository extends JpaRepository<StudentLiquidation, Long> {
    Optional<StudentLiquidation> findByTrackingId(UUID trackingId);
}
