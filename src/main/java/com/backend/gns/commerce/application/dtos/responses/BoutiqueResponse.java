package com.backend.gns.commerce.application.dtos.responses;

import com.backend.gns.core.domain.enums.KycStatus;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record BoutiqueResponse(
    UUID trackingId,
    UUID merchantTrackingId,
    UUID walletTrackingId,
    String nomBoutique,
    String categorieShop,
    String cheminCarteEDJ,
    KycStatus statutKYC,
    Double latitude,
    Double longitude,
    BigDecimal solde,
    BigDecimal plafond) {}
