package com.backend.gns.wallet.application.dtos.requests;

import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record WalletRequest(
    @com.fasterxml.jackson.annotation.JsonProperty("typeWallet")
    WalletType walletType,
    @com.fasterxml.jackson.annotation.JsonProperty("statutWallet")
    WalletStatus status,
    @com.fasterxml.jackson.annotation.JsonProperty("solde")
    BigDecimal balance,
    @com.fasterxml.jackson.annotation.JsonProperty("plafond")
    BigDecimal limitAmount
) {

}
