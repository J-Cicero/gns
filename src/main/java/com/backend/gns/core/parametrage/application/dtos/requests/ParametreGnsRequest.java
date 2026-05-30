package com.backend.gns.core.parametrage.application.dtos.requests;

import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ParametreGnsRequest(
    @NotNull TypeParametreGns nomParametre,
    @NotBlank String valeurParametre,
    String description,
    boolean estActif) {}
