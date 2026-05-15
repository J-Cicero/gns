package com.backend.gns.student.application.dtos.responses;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record RegleBourseDbsResponse(
    UUID trackingId,
    String libelle,
    String codeUnique,
    BigDecimal valeurNumerique,
    String valeurTextuelle,
    boolean estActif,
    String description
) {}
