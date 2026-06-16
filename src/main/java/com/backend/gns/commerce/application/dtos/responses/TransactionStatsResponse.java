package com.backend.gns.commerce.application.dtos.responses;

import java.math.BigDecimal;

public record TransactionStatsResponse(
    BigDecimal totalVolume,
    BigDecimal totalCommission,
    long totalCount
) {}
