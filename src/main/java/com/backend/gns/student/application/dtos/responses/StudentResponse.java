package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record StudentResponse(
    UUID trackingId,
    String email,
    String lastName,
    String firstName,
    Boolean isActive,
    String phoneNumber,
    LocalDateTime birthDate,
    String birthPlace, // Ajouté
    String studentIdNumber,
    KycStatus kycStatus,
    UUID walletTrackingId,
    BigDecimal balance,
    UUID universiteTrackingId,
    String universiteFullName) {}
