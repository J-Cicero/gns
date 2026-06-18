package com.backend.gns.commerce.application.dtos.requests;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record MerchantRequest(
    String email,
    String password,
    String lastName,
    String firstName,
    Boolean isActive,
    String phoneNumber,
    LocalDateTime birthDate,
    String businessName,
    String registrationNumber,
    UUID bankTrackingId,
    String accountNumber) {}
