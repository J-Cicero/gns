package com.backend.gns.core.parametrage.application.dtos.responses;

import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponse(
    UUID trackingId,
    UUID ownerTrackingId,
    ProprietaireType ownerType,
    TypeDocument documentType,
    String fileUrl,
    StatutDocument status,
    LocalDateTime uploadedAt
) {}
