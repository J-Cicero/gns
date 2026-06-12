package com.backend.gns.core.application.dtos.requests;

import java.util.UUID;

public record CompteBancaireRequest(
    UUID ribDocumentTrackingId,
    UUID banqueTrackingId,
    UUID proprietaireTrackingId,
    String typeProprietaire,
    String numeroCompte
) {}
