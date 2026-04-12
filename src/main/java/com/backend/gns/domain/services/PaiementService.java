package com.backend.gns.domain.services;

import com.backend.gns.domain.dtos.requests.PaiementRequest;
import com.backend.gns.domain.dtos.requests.PaiementScolariteRequest;
import com.backend.gns.domain.dtos.requests.PaiementSimpleRequest;
import com.backend.gns.domain.dtos.requests.PaiementHybrideRequest;
import com.backend.gns.domain.dtos.responses.PaiementResponse;

import java.util.List;
import java.util.UUID;

public interface PaiementService {

    PaiementResponse create(PaiementRequest request);

    List<PaiementResponse> getAll();

    PaiementResponse getByTrackingId(UUID trackingId);

    PaiementResponse update(UUID trackingId, PaiementRequest request);

    void delete(UUID trackingId);

    /**
     * F7 - Effectue un paiement de scolarite (type SCOLARITE, pas de commission).
     */
    PaiementResponse effectuerPaiementScolarite(PaiementScolariteRequest request);

    /**
     * F4 - Effectue un paiement simple chez un commercant.
     */
    PaiementResponse effectuerPaiement(PaiementSimpleRequest request);

    /**
     * F5 - Effectue un paiement hybride avec switch automatique entre deux wallets.
     */
    PaiementResponse effectuerPaiementHybride(PaiementHybrideRequest request);
}
