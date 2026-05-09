package com.backend.gns.paiement.domain.services;

import com.backend.gns.paiement.application.dtos.requests.PaiementRequest;
import com.backend.gns.paiement.application.dtos.responses.PaiementResponse;
import com.backend.gns.paiement.domain.enums.PaiementStatut;
import com.backend.gns.paiement.domain.enums.PaiementType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaiementService {

  PaiementResponse create(PaiementRequest request);

  Optional<PaiementResponse> findByTrackingId(UUID trackingId);

  PaiementResponse update(UUID trackingId, PaiementRequest request);

  void delete(UUID trackingId);

  Page<PaiementResponse> findByStatutPaiement(PaiementStatut statutPaiement, Pageable pageable);

  Page<PaiementResponse> findByPaiementStatut(PaiementStatut paiementStatut, Pageable pageable);

  Page<PaiementResponse> findByTypePaiement(PaiementType typePaiement, Pageable pageable);

  Page<PaiementResponse> findByPaiementType(PaiementType paiementType, Pageable pageable);

  Page<PaiementResponse> findByCommandeTrackingId(UUID commandeTrackingId, Pageable pageable);

  Page<PaiementResponse> findByWalletTrackingId(UUID walletTrackingId, Pageable pageable);

  Page<PaiementResponse> findAll(Pageable pageable);
}
