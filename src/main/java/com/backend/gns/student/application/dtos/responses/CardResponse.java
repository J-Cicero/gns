package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.student.domain.enums.CardStatut;

import java.util.UUID;
import lombok.Builder;

@Builder
public record CardResponse(
    UUID trackingId,
    String qrCodeStatique,
    CardStatut cardStatus,
    UUID studentTrackingId) {}
