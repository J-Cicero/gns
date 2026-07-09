package com.backend.gns.commerce.application.dtos.requests;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ProductRequest(
    UUID boutiqueTrackingId,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    Boolean isAvailable,
    LocalDateTime addedAt,
    String imageUrl) {}
