package com.backend.gns.wallet.application.dtos.requests;

import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record WalletRequest(
    WalletType walletType,
    WalletStatus status,
    BigDecimal balance,
    BigDecimal limitAmount,
    String currency,
    LocalDateTime createdAt) {}
