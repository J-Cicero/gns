package com.backend.gns.core.application.dtos.requests;

public record BanqueRequest(
    String name,
    String code,
    String logoUrl
) {}
