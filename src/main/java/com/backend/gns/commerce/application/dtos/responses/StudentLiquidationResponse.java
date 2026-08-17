package com.backend.gns.commerce.application.dtos.responses;

import com.backend.gns.commerce.domain.enums.LiquidationStatut;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record StudentLiquidationResponse(
    UUID trackingId,
    UUID studentTrackingId,
    String studentName,
    BigDecimal amountDeducted,
    LocalDateTime createdAt,
    LocalDateTime validatedAt,
    LiquidationStatut status
) {}
