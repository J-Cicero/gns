package com.backend.gns.core.parametrage.application.dtos.requests;

import com.backend.gns.core.parametrage.domain.enums.ProprietaireType; // Added import

import java.util.UUID;

public record CompteBancaireRequest(
    UUID banqueTrackingId,
    UUID ownerTrackingId,
    ProprietaireType ownerType, // Changed from String typeProprietaire
    String accountNumber,
    UUID ribDocumentTrackingId
) {}
