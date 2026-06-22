package com.backend.gns.commerce.application.dtos.requests;

import java.math.BigDecimal;
import java.util.UUID;

public record StudentLiquidationRequest(
    UUID studentTrackingId,
    UUID scolariteYearTrackingId,
    BigDecimal amountToDeduct
) {}
