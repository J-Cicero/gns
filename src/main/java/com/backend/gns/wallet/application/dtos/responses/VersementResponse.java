package com.backend.gns.wallet.application.dtos.responses;

import com.backend.gns.wallet.domain.enums.VersementStatut;
import com.backend.gns.wallet.domain.enums.VersementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record VersementResponse(
    UUID trackingId,
    UUID trackingWalletId,
    BigDecimal montantVerse,
    VersementType typeVersement,
    LocalDateTime dateVersement,
    VersementStatut statut) {}
