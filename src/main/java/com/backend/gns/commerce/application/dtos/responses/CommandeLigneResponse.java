package com.backend.gns.commerce.application.dtos.responses;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CommandeLigneResponse(
    UUID trackingId,
    UUID trackingCommandeId,
    UUID trackingProductId,
    int quantite,
    BigDecimal prixUnitaire) {}
