package com.backend.gns.user.application.dtos.responses;

import com.backend.gns.core.parametrage.domain.enums.KycStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
    UUID trackingId,
    String lastName,
    String firstName,
    String phoneNumber,
    String email,
    String role,
    LocalDateTime registrationDate,
    KycStatus kycStatus,
    boolean isActive) {}
