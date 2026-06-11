package com.backend.gns.commerce.application.dtos.requests;

import java.math.BigDecimal;
import java.util.UUID;

public record LiquidationRequest(
    UUID boutiqueId,
    BigDecimal montantALiquider
) {}
