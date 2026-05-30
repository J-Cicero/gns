package com.backend.gns.student.application.dtos.requests;

import com.backend.gns.student.domain.enums.TypeParametreDbs;
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
