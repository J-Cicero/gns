package com.backend.gns.wallet.application.dtos.responses;

import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record WalletResponse(
    UUID trackingId,
    WalletType typeWallet,
    WalletStatus statutWallet,
    BigDecimal solde,
    BigDecimal plafond,
    Boolean estVerrouille,
    LocalDateTime dateCreation) {}
