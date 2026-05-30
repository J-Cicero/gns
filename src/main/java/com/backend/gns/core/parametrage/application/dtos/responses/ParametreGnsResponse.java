package com.backend.gns.core.parametrage.application.dtos.responses;

import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
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
