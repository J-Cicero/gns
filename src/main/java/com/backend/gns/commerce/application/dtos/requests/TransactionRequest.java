package com.backend.gns.commerce.application.dtos.requests;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequest(
    UUID senderTrackingId,
    UUID receiverTrackingId,
    BigDecimal amount,
    String password,
    Boolean isCommissionPaid,
    Boolean isRetry
) {}
