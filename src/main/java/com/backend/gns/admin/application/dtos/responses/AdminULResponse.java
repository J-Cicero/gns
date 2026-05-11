package com.backend.gns.admin.application.dtos.responses;

import com.backend.gns.Shared.user.domain.enums.UserRole;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AdminULResponse(
    UUID trackingId,
    String email,
    String nom,
    String prenom,
    UserRole role,
    Boolean estActif,
    String telephone,
    String numeroCompte,
    UUID walletTrackingId
) {}
