package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.core.domain.enums.Banque;
import com.backend.gns.student.domain.enums.MandatStatut;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record BanqueEtudiantResponse(
    UUID trackingId,
    UUID studentTrackingId,
    Banque banque,
    String RIB,
    MandatStatut mandatStatut,
    boolean mandatSigne,
    LocalDateTime mandatTimestamp,
    String lieuEnregistrement,
    LocalDateTime mandatValideLeDate
) {}
