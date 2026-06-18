package com.backend.gns.wallet.domain.services;

import com.backend.gns.wallet.application.dtos.requests.VersementRequest;
import com.backend.gns.wallet.application.dtos.responses.VersementResponse;
import com.backend.gns.wallet.domain.enums.VersementStatut;
import com.backend.gns.wallet.domain.enums.VersementType;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VersementService {
  VersementResponse create(VersementRequest request);
  Optional<VersementResponse> findByTrackingId(UUID trackingId);
  VersementResponse update(UUID trackingId, VersementRequest request);
  void delete(UUID trackingId);
  Page<VersementResponse> findByStatut(VersementStatut statut, Pageable pageable);
  Page<VersementResponse> findByTypeVersement(VersementType typeVersement, Pageable pageable);
  Page<VersementResponse> findByWalletTrackingId(UUID walletTrackingId, Pageable pageable);
  Page<VersementResponse> findAll(Pageable pageable);

  // Méthodes de masse enrichies avec WalletStatus
  void effectuerVersementMasseEtudiants(UUID scolariteYearTrackingId, WalletStatus statutCible, BigDecimal montantFixe);
  void effectuerVersementMasseBoutiques(BigDecimal seuil, WalletStatus statutCible, BigDecimal montantQuota);

  List<String> previewMasseEtudiants(UUID scolariteYearTrackingId);
  List<String> previewMasseBoutiques(BigDecimal seuil);
  void remiseAZeroMasseEtudiants(UUID scolariteYearTrackingId);
  void remiseAZeroMasseBoutiques();
}
