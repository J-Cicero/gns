package com.backend.gns.commerce.application.dtos.requests;

import com.backend.gns.core.domain.enums.KycStatus;
import java.util.UUID;
import lombok.Builder;

@Builder
public record BoutiqueRequest(
    UUID merchantTrackingId,
    UUID walletTrackingId,
    String name,
    String shopCategory,
    String mapPath,
    KycStatus kycStatus,
    Double latitude,
    Double longitude) {}
