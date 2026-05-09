package com.backend.gns.commerce.infrastructure.repositories;

import com.backend.gns.Shared.domain.enums.KycStatus;
import com.backend.gns.commerce.domain.models.Boutique;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoutiqueRepository extends JpaRepository<Boutique, Long> {

  Optional<Boutique> findByTrackingId(UUID trackingId);

  @Query("SELECT b FROM Boutique b WHERE b.merchant.trackingId = :merchantTrackingId")
  Page<Boutique> findByMerchantTrackingId(
      @Param("merchantTrackingId") UUID merchantTrackingId, Pageable pageable);

  @Query("SELECT b FROM Boutique b WHERE b.wallet.trackingId = :walletTrackingId")
  Optional<Boutique> findByWalletTrackingId(@Param("walletTrackingId") UUID walletTrackingId);

  Page<Boutique> findByStatutKYC(KycStatus statutKYC, Pageable pageable);
}
