package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.enums.TypeBourse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record InscriptionAnnuelleResponse(
    UUID trackingId,
    UUID studentTrackingId,
    String studentLastName,
    String studentFirstName,
    String studentIdNumber,
    String academicYearLabel,
    StudentNiveau studyLevel,
    com.backend.gns.student.domain.enums.StatutInscription status,
    String rejectionReason,
    boolean isEligibleForScholarship,
    TypeBourse scholarshipType,
    LocalDateTime apiValidationDate,
    BigDecimal allocatedBudget,
    UUID walletTrackingId,
    BigDecimal walletBalance) {}
