package com.backend.gns.core.parametrage.application.dtos.responses;

import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CompteBancaireResponse(
        UUID trackingId,
        String accountNumber,
        String bankName,
        ProprietaireType ownerType,
        String ribUrl,                // Sera rempli par le Service
        UUID ribDocumentTrackingId    // Sera rempli par le Service
) {}
