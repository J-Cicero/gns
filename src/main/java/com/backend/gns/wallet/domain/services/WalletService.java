package com.backend.gns.wallet.domain.services;

import com.backend.gns.wallet.application.dtos.requests.WalletRequest;
import com.backend.gns.wallet.application.dtos.responses.WalletResponse;
import com.backend.gns.wallet.domain.enums.WalletFundingLevel;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletService {

  WalletResponse create(WalletRequest request);

  Optional<WalletResponse> findByTrackingId(UUID trackingId);

  WalletResponse update(UUID trackingId, WalletRequest request);

  void delete(UUID trackingId);

  // Recherche & Filtrage
  Page<WalletResponse> findByTypeWallet(WalletType typeWallet, Pageable pageable);
  Page<WalletResponse> findFiltered(WalletType typeWallet, WalletFundingLevel niveauSolde, Pageable pageable);
  Page<WalletResponse> findByStatutWallet(WalletStatus statutWallet, Pageable pageable);
  Page<WalletResponse> findByNiveauSolde(WalletFundingLevel niveauSolde, Pageable pageable);
  Page<WalletResponse> findBySoldeLessThan(BigDecimal amount, Pageable pageable);
  Page<WalletResponse> findBySoldeGreaterThan(BigDecimal amount, Pageable pageable);
  Page<WalletResponse> findAll(Pageable pageable);

  // Transactions (Logique métier unifiée)
  @Transactional
  void crediter(UUID walletTrackingId, BigDecimal montant);

  @Transactional
  void debiter(UUID walletTrackingId, BigDecimal montant);

  boolean hasSufficientBalance(UUID walletTrackingId, BigDecimal amount);

  // Gestion technique
  void remettreAZero(UUID walletTrackingId);

  @Transactional
  void remettreAZeroGroupe(List<UUID> walletTrackingIds);

  void gelerTousLesWalletsEtudiant(boolean geler);
}