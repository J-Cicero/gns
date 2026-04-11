package com.backend.gns.domain.dtos.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record BudgetVirtuelResponse(
        UUID trackingId,
        UUID merchantTrackingId,
        Double montantAlloue,
        Double montantRestant,
        String periodeMois,
        boolean estEpuise,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
