package com.backend.gns.core.application.dtos.responses;

import java.util.UUID;

public record CompteBancaireResponse(
    UUID trackingId,
    String accountNumber,
    String bankName,
    String ownerType
) {}
