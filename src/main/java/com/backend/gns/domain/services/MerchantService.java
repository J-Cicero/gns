package com.backend.gns.domain.services;

import com.backend.gns.domain.dtos.requests.MerchantRequest;
import com.backend.gns.domain.dtos.responses.MerchantResponse;

import java.util.List;
import java.util.UUID;

public interface MerchantService {

    MerchantResponse create(MerchantRequest request);

    List<MerchantResponse> getAll();

    MerchantResponse getByTrackingId(UUID trackingId);

    MerchantResponse update(UUID trackingId, MerchantRequest request);

    void delete(UUID trackingId);
}
