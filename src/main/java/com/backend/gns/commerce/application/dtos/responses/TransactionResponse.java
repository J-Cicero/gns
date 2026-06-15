package com.backend.gns.commerce.application.dtos.responses;

import com.backend.gns.commerce.domain.enums.TransactionStatut;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
    UUID trackingId,
    UUID senderTrackingId,
    UUID receiverTrackingId,
    String senderName,
    String receiverName,
    BigDecimal amount,
    TransactionStatut status,
    LocalDateTime createdAt
) {}
