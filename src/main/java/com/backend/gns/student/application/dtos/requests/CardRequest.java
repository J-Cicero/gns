package com.backend.gns.student.application.dtos.requests;

import com.backend.gns.student.domain.enums.CardStatut;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CardRequest(
    String cardNumber,
    String qrCodeData,
    CardStatut status,
    UUID walletTrackingId
) {}
