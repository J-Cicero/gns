package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.enums.TypeBourse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record InscriptionAnnuelleResponse(
    UUID trackingId,
    UUID studentTrackingId,
    String studentLastName,
    String studentFirstName,
    String studentIdNumber,
    String academicYearLabel,
    StudentNiveau studyLevel,
    boolean isFullyEnrolled,
    boolean isEligibleForScholarship,
    TypeBourse scholarshipType,
    LocalDateTime apiValidationDate,
    BigDecimal allocatedBudget) {}
