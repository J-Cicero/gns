package com.backend.gns.commerce.application.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProductResponse(
    UUID trackingId,
    UUID boutiqueTrackingId,
    String nom,
    String description,
    BigDecimal prix,
    int stock,
    Boolean estDisponible,
    LocalDateTime dateAjout) {}
