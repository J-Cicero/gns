package com.backend.gns.core.parametrage.application.dtos.responses;

import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DocumentBanqueResponse(
        UUID trackingId,
        TypeDocument documentType,
        String fileUrl,
        StatutDocument status,
        LocalDateTime uploadedAt,
        UUID compteBancaireTrackingId
) {}