package com.backend.gns.commerce.application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record MerchantRequest(
    String email,
    String password,
    String lastName,
    String firstName,
    Boolean isActive,
    String phoneNumber,
    LocalDateTime birthDate,
    String birthPlace,
    @JsonAlias({"businessName", "nomBoutique", "boutiqueName", "name"})
    String businessName,
    @JsonAlias({"registrationNumber", "rccm", "nif"})
    String registrationNumber,
    @JsonAlias({"bankTrackingId", "banqueTrackingId"})
    UUID bankTrackingId,
    @JsonAlias({"accountNumber", "numeroCompte"})
    String accountNumber) {}
