package com.backend.gns.wallet.infrastructure.repositories;

import com.backend.gns.wallet.domain.enums.WalletFundingLevel;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.wallet.domain.models.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

  // Rechercher un portefeuille par son trackingId
  Optional<Wallet> findByTrackingId(UUID trackingId);

  // Rechercher un portefeuille par son type
  Page<Wallet> findByWalletType(WalletType walletType, Pageable pageable);

  // Rechercher un portefeuille par son statut
  Page<Wallet> findByStatus(WalletStatus status, Pageable pageable);

  // Rechercher un portefeuille par son type et/ou niveau solde
  @Query(
      "SELECT w FROM Wallet w WHERE "
          + "(:walletType IS NULL OR w.walletType = :walletType) AND "
          + "(:fundingLevel IS NULL OR w.fundingLevel = :fundingLevel)")
  Page<Wallet> findFiltered(
      @Param("walletType") WalletType walletType,
      @Param("fundingLevel") WalletFundingLevel fundingLevel,
      Pageable pageable);

  // Rechercher un portefeuille par son niveau de solde
  Page<Wallet> findByFundingLevel(WalletFundingLevel fundingLevel, Pageable pageable);

  @Query("SELECT w FROM Wallet w WHERE w.balance < :amount ORDER BY w.balance ASC")
  Page<Wallet> findByBalanceLessThan(@Param("amount") BigDecimal amount, Pageable pageable);

  @Query("SELECT w FROM Wallet w WHERE w.balance > :amount ORDER BY w.balance DESC")
  Page<Wallet> findByBalanceGreaterThan(@Param("amount") BigDecimal amount, Pageable pageable);
    @Modifying
    @Query("UPDATE Wallet w SET w.status = :status WHERE w.walletType = :type")
    void updateStatutByType(@Param("status") WalletStatus status, @Param("type") WalletType type);
}
