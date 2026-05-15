package com.backend.gns.Shared.application.dtos.responses;

import java.util.UUID;
import lombok.Builder;

@Builder
public record UniversiteResponse(
    UUID trackingId,
    String code,
    String nom,
    String ville,
    boolean estActive
) {}
