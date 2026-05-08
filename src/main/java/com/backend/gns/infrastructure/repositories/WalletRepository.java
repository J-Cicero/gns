package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.enums.WalletStatus;
import com.backend.gns.domain.enums.WalletType;
import com.backend.gns.domain.models.Wallet;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

  // Rechercher un portefeuille par son trackingId
  Optional<Wallet> findByTrackingId(UUID trackingId);

  // Rechercher un portefeuille par son type
  Page<Wallet> findByTypeWallet(WalletType typeWallet, Pageable pageable);

  // Rechercher un portefeuille par son statut
  Page<Wallet> findByStatutWallet(WalletStatus statutWallet, Pageable pageable);

  @Query("SELECT w FROM Wallet w WHERE w.solde < :amount ORDER BY w.solde ASC")
  Page<Wallet> findBySoldeLessThan(@Param("amount") BigDecimal amount, Pageable pageable);

  @Query("SELECT w FROM Wallet w WHERE w.solde > :amount ORDER BY w.solde DESC")
  Page<Wallet> findBySoldeGreaterThan(@Param("amount") BigDecimal amount, Pageable pageable);
}
