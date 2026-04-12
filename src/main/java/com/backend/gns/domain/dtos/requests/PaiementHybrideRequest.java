package com.backend.gns.domain.dtos.requests;

import java.util.UUID;

public record PaiementHybrideRequest(
        UUID walletPrincipalTrackingId,
        UUID commandeTrackingId,
        Double montantProduit
) {}
