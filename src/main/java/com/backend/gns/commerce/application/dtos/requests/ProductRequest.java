package com.backend.gns.commerce.application.dtos.requests;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProductRequest(
    UUID boutiqueTrackingId,
    String name,
    String description,
    BigDecimal price,
    int stock,
    Boolean isAvailable,
    LocalDateTime addedAt) {}
