package com.backend.gns.student.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RegleBourseDbsRequest(
    @NotBlank String libelle,
    @NotBlank String codeUnique,
    @NotNull BigDecimal valeurNumerique,
    String valeurTextuelle,
    boolean estActif,
    String description
) {}
