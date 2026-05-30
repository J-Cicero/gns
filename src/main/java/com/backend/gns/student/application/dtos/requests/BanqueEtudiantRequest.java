package com.backend.gns.student.application.dtos.requests;

import com.backend.gns.core.domain.enums.Banque;
import com.backend.gns.student.domain.enums.MandatStatut;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record BanqueEtudiantRequest(
    UUID studentTrackingId,
    Banque banque,
    String RIB,
    MandatStatut mandatStatut,
    boolean mandatSigne,
    LocalDateTime mandatTimestamp,
    String lieuEnregistrement,
    LocalDateTime mandatValideLeDate
) {}


