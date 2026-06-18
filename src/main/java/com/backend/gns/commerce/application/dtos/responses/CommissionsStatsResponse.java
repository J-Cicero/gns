package com.backend.gns.commerce.application.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CommissionsStatsResponse(
    UUID bankTrackingId,
    BigDecimal gnsCommissionTotal,
    BigDecimal bankShareTotal,
    Long totalTransactionsCount,
    LocalDateTime periodStart,
    LocalDateTime periodEnd
) {}
