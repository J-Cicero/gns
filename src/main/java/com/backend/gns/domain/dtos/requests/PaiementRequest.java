package com.backend.gns.domain.dtos.requests;

import java.util.UUID;

public record PaiementRequest(
        UUID commandeTrackingId,
        UUID walletTrackingId,
        Double montantProduit,
        Double commission,
        Double montantDebite,
        String typePaiement,
        boolean estSwitch
) {}
