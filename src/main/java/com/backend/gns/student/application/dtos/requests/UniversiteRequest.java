package com.backend.gns.student.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record UniversiteRequest(
    @NotBlank String code,
    @NotBlank String nom,
    String ville,
    boolean estActive
) {}
