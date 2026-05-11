package com.backend.gns.student.application.dtos.requests;

import com.backend.gns.student.domain.enums.SourceVerification;
import com.backend.gns.student.domain.enums.StatutInscription;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.enums.TypeBourse;
import java.util.UUID;
import lombok.Builder;

@Builder
public record InscriptionAnnuelleRequest(
    UUID studentTrackingId,
    String anneeAcademique,
    StudentNiveau niveau,
    int creditsTotalValides,
    String mentionBac,
    boolean estBoursier,
    TypeBourse typeBourse,
    boolean fraisScolaritePayes,
    StatutInscription statut,
    SourceVerification source) {}
