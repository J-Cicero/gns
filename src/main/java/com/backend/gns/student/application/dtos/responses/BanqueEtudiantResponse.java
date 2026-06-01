package com.backend.gns.student.application.dtos.responses;

// Removed Enum import
import com.backend.gns.student.domain.enums.MandatStatut;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record BanqueEtudiantResponse(
    UUID trackingId,
    UUID studentTrackingId,
    UUID banqueId,
    String banqueName,
    String RIB,
    MandatStatut mandatStatut,
    boolean mandatSigne,
    LocalDateTime mandatTimestamp,
    String lieuEnregistrement,
    LocalDateTime mandatValideLeDate) {}
