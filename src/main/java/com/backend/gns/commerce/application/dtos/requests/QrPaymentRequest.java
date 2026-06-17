package com.backend.gns.commerce.application.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record QrPaymentRequest(
    @NotNull(message = "Boutique ID is required") UUID boutiqueTrackingId,
    @NotNull(message = "Student QR token is required") String studentQrToken,
    @NotNull(message = "Total amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal totalAmount
    ) {}
