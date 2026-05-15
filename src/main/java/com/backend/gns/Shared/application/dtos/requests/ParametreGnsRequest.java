package com.backend.gns.Shared.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record ParametreGnsRequest(
    @NotBlank String cle,
    @NotBlank String valeur,
    String description,
    boolean estActif
) {}
