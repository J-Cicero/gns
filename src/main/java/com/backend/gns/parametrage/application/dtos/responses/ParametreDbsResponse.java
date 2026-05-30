package com.backend.gns.parametrage.application.dtos.responses;

import com.backend.gns.parametrage.domain.enums.TypeParametreDbs;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ParametreDbsResponse(
    UUID trackingId,
    TypeParametreDbs nomParametre,
    String valeurParametre,
    boolean estActif,
    String description
) {}
