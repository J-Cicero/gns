package com.backend.gns.admin.infrastructure.repositories;

import com.backend.gns.admin.domain.models.AdminUL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminULRepository extends JpaRepository<AdminUL, Long> {
    Optional<AdminUL> findByTrackingId(UUID trackingId);
}
