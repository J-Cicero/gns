package com.backend.gns.user.application.dtos.responses;

import java.util.List;
import java.util.UUID;

public record LoginResponse(
    UUID trackingId,
    String token,
    String type,
    String firstName,
    String lastName,
    String phone,
    String email,
    String roles,
    List<String> rolesList,
    String country,
    boolean active) {}
