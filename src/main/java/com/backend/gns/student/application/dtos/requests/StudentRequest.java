package com.backend.gns.application.dtos.requests;

import com.backend.gns.Shared.user.domain.enums.UserRole;
import com.backend.gns.domain.enums.KycStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record StudentRequest(
    String email,
    String password,
    String nom,
    String prenom,
    UserRole role,
    Boolean estActif,
    String telephone,
    LocalDateTime dateNaissance,
    String numEtudiantUL,
    KycStatus statutKYC,
    UUID walletTrackingId,
    UUID banqueEtudiantTrackingId,
    String pinCode
) {}

