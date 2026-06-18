package com.backend.gns.wallet.application.dtos.responses;

import com.backend.gns.wallet.domain.enums.VersementStatut;
import com.backend.gns.wallet.domain.enums.VersementType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record VersementResponse(
    UUID trackingId,
    UUID walletTrackingId,
    BigDecimal amount,
    VersementType paymentType,
    LocalDateTime paymentDate,
    VersementStatut status) {}
