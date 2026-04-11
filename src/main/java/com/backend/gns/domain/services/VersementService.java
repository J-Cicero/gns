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
}
