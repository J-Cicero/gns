package com.backend.gns.academique.application.dtos.responses;

import java.util.UUID;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record UniversiteResponse(
    UUID trackingId,
    String code,
    String nom,
    String ville,
    boolean estActive,
    UUID walletTrackingId,
    BigDecimal soldeWallet
) {}
