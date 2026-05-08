package com.backend.gns.application.dtos.responses;

import com.backend.gns.domain.enums.CardStatut;

import java.util.UUID;
import lombok.Builder;

@Builder
public record CardResponse(
    UUID trackingId,
    String qrCodeStatique,
    CardStatut cardStatus,
    UUID studentTrackingId) {}
