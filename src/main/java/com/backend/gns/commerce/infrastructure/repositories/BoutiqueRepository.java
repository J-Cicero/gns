package com.backend.gns.commerce.infrastructure.repositories;

import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.core.parametrage.domain.enums.KycStatus;
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
  Page<Boutique> findByMerchantTrackingId(
      @Param("merchantTrackingId") UUID merchantTrackingId, Pageable pageable);

  @Query("SELECT b FROM Boutique b WHERE b.wallet.trackingId = :walletTrackingId")
  Optional<Boutique> findByWalletTrackingId(@Param("walletTrackingId") UUID walletTrackingId);

  Page<Boutique> findByKycStatus(KycStatus kycStatus, Pageable pageable);

  java.util.List<Boutique> findByMerchant(com.backend.gns.commerce.domain.models.Merchant merchant);

  @Query(
      "SELECT b FROM Boutique b WHERE b.wallet IS NOT NULL AND b.wallet.limitAmount > 0 AND b.wallet.balance <= (b.wallet.limitAmount * :seuil)")
  Page<Boutique> findBoutiquesEnAlerteQuota(
      @Param("seuil") java.math.BigDecimal seuil, Pageable pageable);
}
