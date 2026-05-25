package com.backend.gns.paiement.application.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PretScolariteResponse(
    UUID trackingId,
    UUID studentTrackingId,
    String studentNom,
    UUID universiteTrackingId,
    String universiteNom,
    BigDecimal montant,
    boolean estRembourse,
    String description,
    LocalDateTime createdAt
) {}
