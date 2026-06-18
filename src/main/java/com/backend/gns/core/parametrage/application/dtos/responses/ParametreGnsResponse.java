package com.backend.gns.core.parametrage.application.dtos.responses;

import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ParametreGnsResponse(
    UUID trackingId,
    TypeParametreGns nomParametre,
    String valeurParametre,
    String description
) {}
