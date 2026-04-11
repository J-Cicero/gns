package com.backend.gns.domain.services;

import com.backend.gns.domain.dtos.requests.PaiementRequest;
import com.backend.gns.domain.dtos.responses.PaiementResponse;

import java.util.List;
import java.util.UUID;

public interface PaiementService {

    PaiementResponse create(PaiementRequest request);

    List<PaiementResponse> getAll();

    PaiementResponse getByTrackingId(UUID trackingId);

    PaiementResponse update(UUID trackingId, PaiementRequest request);

    void delete(UUID trackingId);
}
