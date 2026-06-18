package com.backend.gns.student.application.dtos.responses;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UniversiteResponse(
    UUID trackingId,
    String code,
    String fullName,
    String city,
    boolean isActive) {}
