package com.backend.gns.Shared.application.dtos.responses;

import java.util.UUID;
import lombok.Builder;

@Builder
public record ParametreGnsResponse(
    UUID trackingId,
    String cle,
    String valeur,
    String description,
    boolean estActif
) {}
