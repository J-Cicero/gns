package com.backend.gns.core.application.dtos.responses;

import lombok.Builder;

import java.util.UUID;

@Builder
public record BanqueResponse(UUID trackingId, String code, String name) {}
