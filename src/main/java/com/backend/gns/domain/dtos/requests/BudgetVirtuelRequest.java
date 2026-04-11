package com.backend.gns.domain.dtos.requests;

import java.util.UUID;

public record BudgetVirtuelRequest(
        UUID merchantTrackingId,
        Double montantAlloue,
        String periodeMois
) {}
