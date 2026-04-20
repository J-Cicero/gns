package com.backend.gns.application.dtos.requests;

import com.backend.gns.domain.enums.SourceVerification;
import com.backend.gns.domain.enums.StatutInscription;
import com.backend.gns.domain.enums.StudentNiveau;
import com.backend.gns.domain.enums.VersementType;
import java.util.UUID;
import lombok.Builder;

@Builder
public record InscriptionAnnuelleRequest(
    UUID studentTrackingId,
    String anneeAcademique,
    StudentNiveau niveau,
    int creditsTotalValides,
    boolean estBoursier,
    VersementType typeBourse,
    boolean fraisScolaritePayes,
    StatutInscription statut,
    SourceVerification source) {}
