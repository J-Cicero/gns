package com.backend.gns.commerce.application.dtos.responses;

import com.backend.gns.commerce.domain.enums.LiquidationStatut;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record LiquidationResponse(
    UUID trackingId,
    String boutiqueName,
    BigDecimal montantALiquider,
    LocalDateTime dateCreation,
    LocalDateTime dateValidation,
    LiquidationStatut statut,
    String referenceVirement
) {}
