package com.backend.gns.student.application.dtos.requests;

import com.backend.gns.student.domain.enums.TypeRegleBourse;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RegleBourseDbsRequest(
    @NotNull TypeRegleBourse typeRegle,
    @NotNull BigDecimal valeurCritere,
    boolean estActif,
    String description
) {}
