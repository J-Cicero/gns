package com.backend.gns.domain.dtos.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaiementResponse(
        UUID trackingId,
        UUID commandeTrackingId,
        UUID walletTrackingId,
        Double montantProduit,
        Double commission,
        Double montantDebite,
        LocalDateTime dateTimestamp,
        String typePaiement,
        String statutPaiement,
        boolean estSwitch,
        String commandeRef,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
