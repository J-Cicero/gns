package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.PaiementRequest;
import com.backend.gns.application.dtos.responses.PaiementResponse;
import com.backend.gns.domain.enums.PaiementStatut;
import com.backend.gns.domain.enums.PaiementType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaiementService {

    PaiementResponse create(PaiementRequest request);

    Optional<PaiementResponse> findByTrackingId(UUID trackingId);

    PaiementResponse update(UUID trackingId, PaiementRequest request);

    void delete(UUID trackingId);

    List<PaiementResponse> findByStatutPaiement(PaiementStatut statutPaiement);

    List<PaiementResponse> findByTypePaiement(PaiementType typePaiement);

    List<PaiementResponse> findByCommandeTrackingId(UUID commandeTrackingId);

    List<PaiementResponse> findByWalletTrackingId(UUID walletTrackingId);

    List<PaiementResponse> findAll();
}
