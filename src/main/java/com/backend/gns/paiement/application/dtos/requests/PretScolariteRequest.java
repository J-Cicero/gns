package com.backend.gns.paiement.application.dtos.requests;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PretScolariteRequest(
    UUID studentTrackingId, UUID universiteTrackingId, BigDecimal montant, String description) {}
