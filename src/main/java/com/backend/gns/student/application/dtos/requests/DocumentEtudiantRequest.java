package com.backend.gns.application.dtos.requests;

import com.backend.gns.domain.enums.StatutDocument;
import com.backend.gns.domain.enums.TypeDocument;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DocumentEtudiantRequest(
    UUID inscriptionTrackingId,
    TypeDocument type,
    String cheminFichier,
    StatutDocument statut,
    String commentaireRejet,
    LocalDateTime dateDepot,
    LocalDateTime dateValidation,
    String donneesExtraites
) {}

