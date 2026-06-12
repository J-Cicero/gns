package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.student.domain.enums.StatutDocument;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DocumentResponse(
    UUID trackingId,
    UUID userTrackingId,
    UUID inscriptionId,
    TypeDocument type,
    String cheminFichier,
    StatutDocument statut,
    String commentaireRejet,
    LocalDateTime dateDepot,
    LocalDateTime dateValidation,
    String donneesExtraites,
    Double scoreFiabilite) {}
