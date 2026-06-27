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
    @com.fasterxml.jackson.annotation.JsonProperty("statutCarte")
    CardStatut status,
    LocalDateTime emissionDate,
    @com.fasterxml.jackson.annotation.JsonProperty("dateExpiration")
    LocalDateTime expirationDate,
    UUID walletTrackingId,
    String studentNom,
    String studentPrenom
) {}
