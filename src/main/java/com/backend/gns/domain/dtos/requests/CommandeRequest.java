package com.backend.gns.domain.dtos.requests;

import java.util.UUID;

public record CommandeRequest(
        UUID studentTrackingId,
        UUID merchantTrackingId,
        Double montantTotal
) {}
