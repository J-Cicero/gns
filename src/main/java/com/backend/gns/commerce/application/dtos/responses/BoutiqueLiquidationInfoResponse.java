package com.backend.gns.commerce.application.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoutiqueLiquidationInfoResponse {
    private UUID boutiqueTrackingId;
    private String nomBoutique;
    private String categorieShop;
    private String numeroCompte;
    private BigDecimal soldeWallet;
    private String proprietaireNom;
    private UUID walletTrackingId;
    private String walletStatus;
    private UUID merchantTrackingId;
}
