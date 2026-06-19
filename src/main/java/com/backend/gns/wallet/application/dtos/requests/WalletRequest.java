package com.backend.gns.wallet.application.dtos.requests;

import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record WalletRequest(
    WalletType walletType,
    WalletStatus status,
    BigDecimal balance,
    BigDecimal limitAmount
) {

}
