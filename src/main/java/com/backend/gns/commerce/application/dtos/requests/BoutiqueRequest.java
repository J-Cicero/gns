package com.backend.gns.commerce.application.dtos.requests;

import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record BoutiqueRequest(
    UUID merchantTrackingId,
    UUID walletTrackingId,
    String name,
    String description,
    KycStatus kycStatus,
    Double latitude,
    Double longitude) {}
