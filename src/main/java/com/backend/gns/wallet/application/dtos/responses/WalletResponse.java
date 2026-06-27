package com.backend.gns.wallet.application.dtos.responses;

import com.backend.gns.wallet.domain.enums.WalletFundingLevel;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record WalletResponse(
        UUID trackingId,
        @com.fasterxml.jackson.annotation.JsonProperty("typeWallet")
        WalletType walletType,
        @com.fasterxml.jackson.annotation.JsonProperty("statutWallet")
        WalletStatus status,
        @com.fasterxml.jackson.annotation.JsonProperty("niveauSolde")
        WalletFundingLevel fundingLevel,
        @com.fasterxml.jackson.annotation.JsonProperty("solde")
        BigDecimal balance,
        @com.fasterxml.jackson.annotation.JsonProperty("plafond")
        BigDecimal limitAmount,
        UUID studentTrackingId) {
}
