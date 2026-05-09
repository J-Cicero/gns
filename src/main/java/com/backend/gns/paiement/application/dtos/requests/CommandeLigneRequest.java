package com.backend.gns.application.dtos.requests;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CommandeLigneRequest(
    UUID trackingCommandeId,
    UUID trackingProductId, 
    int quantite, 
    BigDecimal prixUnitaire) {}
