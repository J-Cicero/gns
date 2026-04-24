package com.backend.gns.application.dtos.requests;

import com.backend.gns.domain.enums.TypeDocument;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DocumentEtudiantRequest(
    UUID studentTrackingId,
    UUID inscriptionTrackingId,
    TypeDocument type,
    String cheminFichier,
    String donneesExtraites) {}
