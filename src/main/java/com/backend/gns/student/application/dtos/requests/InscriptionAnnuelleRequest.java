package com.backend.gns.student.application.dtos.requests;

import com.backend.gns.student.domain.enums.SourceVerification;
import com.backend.gns.student.domain.enums.StatutInscription;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.enums.TypeBourse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record InscriptionAnnuelleRequest(
    UUID studentTrackingId,
    String academicYearLabel,
    StudentNiveau studyLevel,
    int totalValidatedCredits,
    BigDecimal highSchoolGrade,
    boolean isScholarshipHolder,
    TypeBourse scholarshipType,
    StatutInscription status,
    SourceVerification source) {}
