package com.backend.gns.application.dtos.responses;

import com.backend.gns.Shared.user.domain.enums.UserRole;
import lombok.Builder;

import java.util.UUID;

@Builder
public record BankOperatorResponse(
    UUID trackingId,
    String email,
    String nom,
    String prenom,
    UserRole role,
    Boolean estActif,
    String telephone,
    UUID walletTrackingId
) {}
