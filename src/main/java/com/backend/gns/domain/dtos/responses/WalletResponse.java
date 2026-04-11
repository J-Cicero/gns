package com.backend.gns.domain.dtos.responses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record WalletResponse(
        UUID trackingId,
        UUID studentTrackingId,
        String typeWallet,
        Double solde,
        Double plafond,
        boolean estVerrouille,
        LocalDate dateCreation,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
