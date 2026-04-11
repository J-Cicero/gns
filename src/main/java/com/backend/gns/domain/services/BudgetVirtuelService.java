package com.backend.gns.domain.services;

import com.backend.gns.domain.dtos.requests.BudgetVirtuelRequest;
import com.backend.gns.domain.dtos.responses.BudgetVirtuelResponse;

import java.util.List;
import java.util.UUID;

public interface BudgetVirtuelService {

    BudgetVirtuelResponse create(BudgetVirtuelRequest request);

    List<BudgetVirtuelResponse> getAll();

    BudgetVirtuelResponse getByTrackingId(UUID trackingId);

    BudgetVirtuelResponse update(UUID trackingId, BudgetVirtuelRequest request);

    void delete(UUID trackingId);
}
