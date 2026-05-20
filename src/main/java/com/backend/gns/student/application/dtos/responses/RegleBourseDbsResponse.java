package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.student.domain.enums.TypeRegleBourse;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record RegleBourseDbsResponse(
    UUID trackingId,
    TypeRegleBourse typeRegle,
    BigDecimal valeurCritere,
    boolean estActif,
    String description
) {}
