package com.backend.gns.academique.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record UniversiteRequest(
    @NotBlank String code,
    @NotBlank String nom,
    String ville,
    boolean estActive
) {}
