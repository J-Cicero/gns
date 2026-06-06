package com.backend.gns.core.application.dtos.responses;

import java.util.UUID;
import lombok.Builder;

@Builder
public record BanqueResponse(UUID trackingId, String code, String nom) {}
