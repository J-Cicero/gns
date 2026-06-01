package com.backend.gns.student.application.dtos.requests;

// Removed Enum import
import com.backend.gns.student.domain.enums.MandatStatut;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record BanqueEtudiantRequest(
    UUID studentTrackingId,
    UUID banqueId,
    String RIB,
    MandatStatut mandatStatut,
    boolean mandatSigne,
    LocalDateTime mandatTimestamp,
    String lieuEnregistrement,
    LocalDateTime mandatValideLeDate) {}
