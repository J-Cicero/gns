package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.student.domain.enums.TypeParametreDbs;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ParametreDbsResponse(
    UUID trackingId,
    TypeParametreDbs nomParametre,
    String valeurParametre,
    boolean estActif,
    String description) {}
