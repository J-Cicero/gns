package com.backend.gns.Shared.user.application.dtos.responses;

import java.util.UUID;

public record UserResponse(
        UUID trackingId,
        String firstName,
        String lastName,
        String phone,
        String email,
        String role,
        String country,
        boolean active
) {
}
