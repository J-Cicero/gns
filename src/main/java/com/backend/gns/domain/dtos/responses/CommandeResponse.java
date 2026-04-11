package com.backend.gns.domain.dtos.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommandeResponse(
        UUID trackingId,
        String reference,
        UUID studentTrackingId,
        UUID merchantTrackingId,
        Double montantTotal,
        LocalDateTime dateCommande,
        String statut,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
