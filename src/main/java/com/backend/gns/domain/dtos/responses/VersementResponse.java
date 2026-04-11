package com.backend.gns.domain.dtos.responses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record VersementResponse(
        UUID trackingId,
        UUID walletTrackingId,
        Double montantVerse,
        String typeVersement,
        LocalDate datePrevue,
        LocalDate dateEffective,
        String statut,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
