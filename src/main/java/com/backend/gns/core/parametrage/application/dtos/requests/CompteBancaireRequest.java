package com.backend.gns.core.application.dtos.requests;

import java.util.UUID;

public record CompteBancaireRequest(
    UUID bankTrackingId,
    UUID ownerTrackingId,
    String ownerType,
    String accountNumber
) {}
