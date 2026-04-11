package com.backend.gns.domain.dtos.requests;

import java.util.UUID;

public record WalletRequest(
        UUID studentTrackingId,
        String typeWallet,
        Double solde,
        Double plafond
) {}
