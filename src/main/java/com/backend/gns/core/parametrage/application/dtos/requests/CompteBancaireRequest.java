package com.backend.gns.core.application.dtos.requests;

import java.util.UUID;

public record CompteBancaireRequest(
    UUID banqueTrackingId,
    UUID ownerTrackingId,
    String typeProprietaire,
    String accountNumber,
    UUID ribDocumentTrackingId
) {}
