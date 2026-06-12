package com.backend.gns.core.application.dtos.requests;

public record BanqueRequest(
    String nom,
    String code,
    String logoUrl
) {}
