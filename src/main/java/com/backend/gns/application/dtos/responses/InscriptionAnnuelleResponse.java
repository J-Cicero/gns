package com.backend.gns.application.dtos.responses;

import com.backend.gns.domain.enums.SourceVerification;
import com.backend.gns.domain.enums.StatutInscription;
import com.backend.gns.domain.enums.StudentNiveau;
import com.backend.gns.domain.enums.VersementType;
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
    boolean estBoursier,
    VersementType typeBourse,
    boolean fraisScolaritePayes,
    StatutInscription statut,
    SourceVerification source,
    LocalDateTime dateActivation) {}
