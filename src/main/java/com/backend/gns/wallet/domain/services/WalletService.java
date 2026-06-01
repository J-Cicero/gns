package com.backend.gns.wallet.domain.services;

import com.backend.gns.wallet.application.dtos.requests.WalletRequest;
import com.backend.gns.wallet.application.dtos.responses.WalletResponse;
import com.backend.gns.wallet.domain.enums.WalletFundingLevel;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WalletService {

  WalletResponse create(WalletRequest request);

  Optional<WalletResponse> findByTrackingId(UUID trackingId);

  WalletResponse update(UUID trackingId, WalletRequest request);

  void delete(UUID trackingId);

  Page<WalletResponse> findByTypeWallet(WalletType typeWallet, Pageable pageable);

  Page<WalletResponse> findFiltered(WalletType typeWallet, WalletFundingLevel niveauSolde, Pageable pageable);

  Page<WalletResponse> findByStatutWallet(WalletStatus statutWallet, Pageable pageable);

  Page<WalletResponse> findByNiveauSolde(WalletFundingLevel niveauSolde, Pageable pageable);

  Page<WalletResponse> findBySoldeLessThan(BigDecimal amount, Pageable pageable);

  Page<WalletResponse> findBySoldeGreaterThan(BigDecimal amount, Pageable pageable);

  Page<WalletResponse> findAll(Pageable pageable);

  void crediter(UUID walletTrackingId, BigDecimal montant);

  void debiter(UUID walletTrackingId, BigDecimal montant);

  void remettreAZero(UUID walletTrackingId);

  void remettreAZeroGroupe(java.util.List<UUID> walletTrackingIds);
}
