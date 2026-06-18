package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.student.domain.enums.CardStatut;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CardResponse(
    UUID trackingId,
    String cardNumber,
    String qrCodeData,
    CardStatut status,
    LocalDateTime emissionDate,
    LocalDateTime expirationDate,
    UUID walletTrackingId
) {}
