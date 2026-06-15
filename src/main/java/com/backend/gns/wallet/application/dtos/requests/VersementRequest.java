package com.backend.gns.wallet.application.dtos.requests;

import com.backend.gns.wallet.domain.enums.VersementStatut;
import com.backend.gns.wallet.domain.enums.VersementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record VersementRequest(
    UUID walletTrackingId,
    BigDecimal amount,
    VersementType paymentType,
    LocalDateTime paymentDate,
    VersementStatut status) {}
