package com.backend.gns.commerce.application.dtos.responses;

import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record BoutiqueResponse(
    UUID trackingId,
    UUID merchantTrackingId,
    UUID walletTrackingId,
    String name,
    String description,
    KycStatus kycStatus,
    Double latitude,
    Double longitude,
    BigDecimal balance,
    BigDecimal limitAmount,
    String walletStatus) {}
