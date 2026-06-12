package com.backend.gns.student.application.dtos.requests;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.student.domain.enums.StatutDocument;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DocumentRequest(
    UUID userTrackingId,
    UUID inscriptionId,
    TypeDocument type,
    String cheminFichier,
    StatutDocument statut,
    String commentaireRejet,
    LocalDateTime dateDepot,
    LocalDateTime dateValidation,
    String donneesExtraites) {}
