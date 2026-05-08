package com.backend.gns.application.dtos.requests;

import com.backend.gns.Shared.user.domain.enums.UserRole;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AdminULRequest(
    String email,
    String password,
    String nom,
    String prenom,
    UserRole role,
    Boolean estActif,
    String telephone,
    String numeroCompte,
    UUID walletTrackingId
) {}
