package com.backend.gns.commerce.infrastructure.repositories;

import com.backend.gns.commerce.domain.models.DocumentMerchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentMerchantRepository extends JpaRepository<DocumentMerchant, Long> {
    Optional<DocumentMerchant> findByTrackingId(UUID trackingId);
    List<DocumentMerchant> findByMerchantTrackingId(UUID merchantTrackingId);
    @Query("SELECT d FROM DocumentMerchant d WHERE d.merchant.trackingId = :trackingId")
    Page<DocumentMerchant> findByMerchantTrackingId(@Param("trackingId") UUID trackingId, Pageable pageable);

}
