package com.backend.gns.domain.dtos.responses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID trackingId,
        UUID merchantTrackingId,
        String nom,
        String description,
        Double prix,
        Integer stock,
        boolean estDisponible,
        LocalDate dateAjout,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
