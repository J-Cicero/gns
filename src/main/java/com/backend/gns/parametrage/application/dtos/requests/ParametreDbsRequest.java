package com.backend.gns.parametrage.application.dtos.requests;

import com.backend.gns.parametrage.domain.enums.TypeParametreDbs;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ParametreDbsRequest(
    @NotNull TypeParametreDbs nomParametre,
    @NotBlank String valeurParametre,
    @NotNull Boolean estActif,
    String description
) {}
