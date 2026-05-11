package com.backend.gns.Shared.wallet.application.dtos.requests;

import com.backend.gns.Shared.wallet.domain.enums.VersementStatut;
import com.backend.gns.Shared.wallet.domain.enums.VersementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record VersementRequest(
    UUID trackingWalletId,
    BigDecimal montantVerse,
    VersementType typeVersement,
    LocalDateTime dateVersement,
    VersementStatut statut
) {}
