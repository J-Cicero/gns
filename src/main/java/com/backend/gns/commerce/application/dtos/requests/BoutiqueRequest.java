package com.backend.gns.commerce.application.dtos.requests;

import com.backend.gns.core.domain.enums.KycStatus;
import java.util.UUID;
import lombok.Builder;

@Builder
public record BoutiqueRequest(
    UUID merchantTrackingId,
    UUID walletTrackingId,
    String nomBoutique,
    String categorieShop,
    String cheminCarteEDJ,
    KycStatus statutKYC,
    Double latitude,
    Double longitude) {}
