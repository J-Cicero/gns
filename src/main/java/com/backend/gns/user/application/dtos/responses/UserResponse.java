package com.backend.gns.user.application.dtos.responses;

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
    boolean isActive) {}
