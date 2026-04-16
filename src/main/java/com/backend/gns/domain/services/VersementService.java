package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.VersementRequest;
import com.backend.gns.application.dtos.responses.VersementResponse;
import com.backend.gns.domain.enums.VersementStatut;
import com.backend.gns.domain.enums.VersementType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VersementService {

    VersementResponse create(VersementRequest request);

    Optional<VersementResponse> findByTrackingId(UUID trackingId);

    VersementResponse update(UUID trackingId, VersementRequest request);

    void delete(UUID trackingId);

    List<VersementResponse> findByStatut(VersementStatut statut);

    List<VersementResponse> findByTypeVersement(VersementType typeVersement);

    List<VersementResponse> findByWalletTrackingId(UUID walletTrackingId);

    List<VersementResponse> findAll();
}
