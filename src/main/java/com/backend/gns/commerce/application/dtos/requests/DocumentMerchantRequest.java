package com.backend.gns.commerce.application.dtos.requests;

import com.backend.gns.core.parametrage.domain.enums.TypeDocument;

import java.util.UUID;

public record DocumentMerchantRequest(
        UUID merchantTrackingId,
        TypeDocument documentType
) {}
