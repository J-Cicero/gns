package com.backend.gns.domain.dtos.requests;

import java.util.UUID;

public record PaiementScolariteRequest(
        UUID walletTrackingId,
        UUID commandeTrackingId,
        Double montantScolarite
) {}
