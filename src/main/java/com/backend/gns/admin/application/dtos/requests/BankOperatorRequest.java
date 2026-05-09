package com.backend.gns.admin.application.dtos.requests;

import com.backend.gns.Shared.user.domain.enums.UserRole;
import lombok.Builder;

import java.util.UUID;

@Builder
public record BankOperatorRequest(
    String email,
    String password,
    String nom,
    String prenom,
    UserRole role,
    Boolean estActif,
    String telephone,
    UUID walletTrackingId
) {}
