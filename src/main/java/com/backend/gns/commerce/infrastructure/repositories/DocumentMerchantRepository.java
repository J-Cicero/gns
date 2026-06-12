package com.backend.gns.commerce.infrastructure.repositories;

import com.backend.gns.commerce.domain.models.DocumentMerchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentMerchantRepository extends JpaRepository<DocumentMerchant, Long> {
    Optional<DocumentMerchant> findByTrackingId(UUID trackingId);
}
