package com.backend.gns.commerce.application.dtos.requests;

import com.backend.gns.Shared.user.domain.enums.UserRole;
import com.backend.gns.Shared.domain.enums.KycStatus;
import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record MerchantRequest(
    String email,
    String password,
    String nom,
    String prenom,
    UserRole role,
    Boolean estActif,
    String telephone,
    LocalDateTime dateNaissance,
    KycStatus statutKYC) {}
