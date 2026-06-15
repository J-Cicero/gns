package com.backend.gns.user.application.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record AdminBanqueRequest(
    @Size(min = 2, message = "Last name must be at least 2 characters")
        @NotNull(message = "Last name is required")
        String lastName,
    @Size(min = 2, message = "First name must be at least 2 characters")
        @NotNull(message = "First name is required")
        String firstName,
    @Pattern(
            regexp = "^(\\+?[0-9]{8,15})$",
            message = "Phone number must be valid (8 to 15 digits, optional +)")
        @NotNull(message = "Phone number is required")
        String phoneNumber,
    @Email(message = "Please provide a valid email")
        @NotNull(message = "Email is required")
        String email,
    @Size(min = 6, message = "Password must be at least 6 characters")
        @NotNull(message = "Password is required")
        String password,
    String role,
    String country,
    @NotNull(message = "Bank is required for a bank administrator")
        UUID bankTrackingId) {}
