package com.backend.gns.domain.services;

import com.backend.gns.domain.dtos.requests.VersementRequest;
import com.backend.gns.domain.dtos.responses.VersementResponse;

import java.util.List;
import java.util.UUID;

public interface VersementService {

    VersementResponse create(VersementRequest request);

    List<VersementResponse> getAll();

    VersementResponse getByTrackingId(UUID trackingId);

    VersementResponse update(UUID trackingId, VersementRequest request);

    void delete(UUID trackingId);

    /**
     * Cree un versement avec statut EXECUTE et dateEffective = aujourd'hui.
     */
    VersementResponse creerVersementExecute(VersementRequest request);

    /**
     * F8 - Remboursement automatique DBS : reinitialise wallet, recrédite 14/15, crée nouveau versement.
     */
    VersementResponse executerRemboursementDBS(UUID versementTrackingId);
}
