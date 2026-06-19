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
        String firstName,
        String lastName,
        String phoneNumber,
        boolean isActive,
        KycStatus kycStatus,
        LocalDateTime birthDate,
        String birthPlace,
        String studentNumber,
        UUID universiteTrackingId,
        UUID walletTrackingId) {}
