package com.backend.gns.commerce.domain.services;

import com.backend.gns.commerce.application.dtos.requests.LiquidationRequest;
import com.backend.gns.commerce.application.dtos.responses.LiquidationResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LiquidationService {
    LiquidationResponse create(LiquidationRequest request);
    Optional<LiquidationResponse> findByTrackingId(UUID trackingId);
    List<LiquidationResponse> findByBoutiqueId(UUID boutiqueId);
    LiquidationResponse validerLiquidation(UUID trackingId, String referenceVirement);
}
