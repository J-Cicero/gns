package com.backend.gns.commerce.application.dtos.responses;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record MerchantResponse(
    UUID trackingId,
    String email,
    String lastName,
    String firstName,
    Boolean isActive,
    String phoneNumber,
    LocalDateTime birthDate,
    String businessName) {}
