package com.backend.gns.domain.dtos.requests;

import java.util.UUID;

public record PaiementSimpleRequest(
        UUID walletTrackingId,
        UUID commandeTrackingId,
        Double montantProduit
) {}
