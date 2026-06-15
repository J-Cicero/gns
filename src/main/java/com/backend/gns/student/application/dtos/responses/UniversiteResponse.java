package com.backend.gns.student.application.dtos.responses;

import java.util.UUID;
import lombok.Builder;

@Builder
public record UniversiteResponse(
    UUID trackingId,
    String code,
    String fullName,
    String city,
    boolean isActive) {}
