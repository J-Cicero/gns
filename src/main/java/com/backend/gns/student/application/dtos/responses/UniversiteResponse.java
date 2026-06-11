package com.backend.gns.student.application.dtos.responses;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UniversiteResponse(
    UUID trackingId,
    String code,
    String nom,
    String ville,
    boolean estActive,
    BigDecimal soldeWallet) {}
