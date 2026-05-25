package com.backend.gns.Shared.infrastructure.repositories;

import com.backend.gns.Shared.domain.models.ConfigurationGns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfigurationGnsRepository extends JpaRepository<ConfigurationGns, Long> {
    Optional<ConfigurationGns> findByCle(String cle);
    Optional<ConfigurationGns> findByTrackingId(UUID trackingId);
}
