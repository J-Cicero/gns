package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.student.domain.enums.SourceVerification;
import com.backend.gns.student.domain.enums.StatutInscription;
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
    String anneeAcademique,
    StudentNiveau niveau,
    int creditsTotalValides,
    BigDecimal moyenneBac,
    boolean estBoursier,
    TypeBourse typeBourse,
    StatutInscription statut,
    SourceVerification source,
    LocalDateTime dateActivation,
    BigDecimal plafondAccorde
) {}
