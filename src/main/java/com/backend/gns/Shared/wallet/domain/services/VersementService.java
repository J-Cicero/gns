package com.backend.gns.Shared.wallet.domain.services;

import com.backend.gns.Shared.wallet.application.dtos.requests.VersementRequest;
import com.backend.gns.Shared.wallet.application.dtos.responses.VersementResponse;
import com.backend.gns.Shared.wallet.domain.enums.VersementStatut;
import com.backend.gns.Shared.wallet.domain.enums.VersementType;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VersementService {

  VersementResponse create(VersementRequest request);

  Optional<VersementResponse> findByTrackingId(UUID trackingId);

  VersementResponse update(UUID trackingId, VersementRequest request);

  void delete(UUID trackingId);

  Page<VersementResponse> findByStatut(VersementStatut statut, Pageable pageable);

  Page<VersementResponse> findByTypeVersement(VersementType typeVersement, Pageable pageable);

  Page<VersementResponse> findByWalletTrackingId(UUID walletTrackingId, Pageable pageable);

  Page<VersementResponse> findAll(Pageable pageable);

  void effectuerVersementMasseEtudiants(UUID scolariteYearTrackingId, BigDecimal montantFixe);

  void effectuerVersementMasseBoutiques(BigDecimal seuil, BigDecimal montantQuota);
}
