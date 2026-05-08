package com.backend.gns.application.dtos.requests;

import com.backend.gns.domain.enums.CardStatut;

import java.util.UUID;
import lombok.Builder;

@Builder
public record CardRequest(
    String qrCodeStatique,
    CardStatut cardStatus,
    UUID studentTrackingId) {}

