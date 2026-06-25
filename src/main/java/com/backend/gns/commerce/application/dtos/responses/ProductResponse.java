package com.backend.gns.commerce.application.dtos.responses;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ProductResponse(
    UUID trackingId,
    UUID boutiqueTrackingId,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    Boolean isAvailable,
    LocalDateTime addedAt) {}
