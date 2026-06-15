package com.backend.gns.commerce.application.dtos.responses;

import com.backend.gns.commerce.domain.enums.LiquidationStatut;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record LiquidationResponse(
    UUID trackingId,
    String boutiqueName,
    BigDecimal amountToLiquidate,
    LocalDateTime createdAt,
    LocalDateTime validatedAt,
    LiquidationStatut status,
    String transferReference
) {}
