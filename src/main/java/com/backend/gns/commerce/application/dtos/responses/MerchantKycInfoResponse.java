package com.backend.gns.commerce.application.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantKycInfoResponse {
    private UUID merchantTrackingId;
    private String nom;
    private String prenom;
    private String email;
    private String phoneNumber;
    private String kycStatus;
    private String numeroCompte;
    // Wallet du marchand (wallet principal, s'il existe)
    private UUID walletTrackingId;
    private String walletStatus;
    private BigDecimal soldeWallet;
    // Boutiques associées
    private List<String> nomsBoutiques;
    private int nombreBoutiques;
}
