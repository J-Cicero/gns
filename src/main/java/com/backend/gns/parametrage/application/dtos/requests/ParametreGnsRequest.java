package com.backend.gns.parametrage.application.dtos.requests;

import com.backend.gns.parametrage.domain.enums.TypeParametreGns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ParametreGnsRequest(
    @NotNull TypeParametreGns nomParametre,
    @NotBlank String valeurParametre,
    String description,
    boolean estActif
) {}
