package com.backend.gns.Shared.application.dtos.responses;

import com.backend.gns.Shared.domain.enums.TypeParametreGns;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ParametreGnsResponse(
    UUID trackingId,
    TypeParametreGns nomParametre,
    String valeurParametre,
    String description,
    boolean estActif
) {}
