package com.backend.gns.student.application.dtos.requests;

import com.backend.gns.student.domain.enums.CardStatut;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CardRequest(
    String cardNumber,
    String qrCodeData,
    CardStatut status,
    UUID walletTrackingId
) {}
