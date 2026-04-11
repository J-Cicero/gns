package com.backend.gns.domain.dtos.requests;

import java.util.UUID;

public record ProductRequest(
        UUID merchantTrackingId,
        String nom,
        String description,
        Double prix,
        Integer stock
) {}
