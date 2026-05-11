package com.backend.gns.Shared.wallet.application.dtos.requests;

import com.backend.gns.Shared.wallet.domain.enums.WalletStatus;
import com.backend.gns.Shared.wallet.domain.enums.WalletType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record WalletRequest(
    WalletType typeWallet,
    WalletStatus statutWallet,
    BigDecimal solde,
    BigDecimal plafond,
    Boolean estVerrouille,
    LocalDateTime dateCreation) {}
