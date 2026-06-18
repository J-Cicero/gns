package com.backend.gns.core.parametrage.application.dtos.responses;

import java.util.UUID;

public record CompteBancaireResponse(
    UUID trackingId,
    String accountNumber,
    String bankName,
    String ownerType, // Changed from String
    String ribUrl,
    UUID ribDocumentTrackingId
) {}
