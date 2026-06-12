package com.backend.gns.wallet.infrastructure.repositories;

import com.backend.gns.wallet.domain.enums.WalletFundingLevel;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.wallet.domain.models.Wallet;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

  // Rechercher un portefeuille par son trackingId
  Optional<Wallet> findByTrackingId(UUID trackingId);

  // Rechercher un portefeuille par son type
  Page<Wallet> findByTypeWallet(WalletType typeWallet, Pageable pageable);

  // Rechercher un portefeuille par son statut
  Page<Wallet> findByStatutWallet(WalletStatus statutWallet, Pageable pageable);

  // Rechercher un portefeuille par son type et/ou niveau solde
  @Query(
      "SELECT w FROM Wallet w WHERE "
          + "(:typeWallet IS NULL OR w.typeWallet = :typeWallet) AND "
          + "(:niveauSolde IS NULL OR w.niveauSolde = :niveauSolde)")
  Page<Wallet> findFiltered(
      @Param("typeWallet") WalletType typeWallet,
      @Param("niveauSolde") WalletFundingLevel niveauSolde,
      Pageable pageable);

  // Rechercher un portefeuille par son niveau de solde
  Page<Wallet> findByNiveauSolde(WalletFundingLevel niveauSolde, Pageable pageable);

  @Query("SELECT w FROM Wallet w WHERE w.solde < :amount ORDER BY w.solde ASC")
  Page<Wallet> findBySoldeLessThan(@Param("amount") BigDecimal amount, Pageable pageable);

  @Query("SELECT w FROM Wallet w WHERE w.solde > :amount ORDER BY w.solde DESC")
  Page<Wallet> findBySoldeGreaterThan(@Param("amount") BigDecimal amount, Pageable pageable);
    @Modifying
    @Query("UPDATE Wallet w SET w.statutWallet = :status WHERE w.typeWallet = :type")
    void updateStatutByType(@Param("status") WalletStatus status, @Param("type") WalletType type);
}
