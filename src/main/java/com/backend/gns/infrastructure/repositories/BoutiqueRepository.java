package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Boutique;
import com.backend.gns.domain.enums.KycStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BoutiqueRepository extends JpaRepository<Boutique, Long> {

    Optional<Boutique> findByTrackingId(UUID trackingId);

    @Query("SELECT b FROM Boutique b WHERE b.merchant.trackingId = :merchantTrackingId")
    Page<Boutique> findByMerchantTrackingId(@Param("merchantTrackingId") UUID merchantTrackingId, Pageable pageable);

    @Query("SELECT b FROM Boutique b WHERE b.wallet.trackingId = :walletTrackingId")
    Optional<Boutique> findByWalletTrackingId(@Param("walletTrackingId") UUID walletTrackingId);

    Page<Boutique> findByStatutKYC(KycStatus statutKYC, Pageable pageable);
}
