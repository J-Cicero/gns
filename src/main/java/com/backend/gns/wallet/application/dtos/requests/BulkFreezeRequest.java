package com.backend.gns.wallet.application.dtos.requests;

import java.util.List;
import java.util.UUID;

public record BulkFreezeRequest(
    List<UUID> walletTrackingIds,
    boolean geler
) {}
