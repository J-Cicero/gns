package com.backend.gns.application.dtos.requests;

import com.backend.gns.Shared.user.domain.enums.TypeRole;
import com.backend.gns.domain.enums.KycStatus;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

@Builder
public record StudentRequest(
    String email,
    String password,
    String nom,
    String prenom,
    TypeRole role,
    Boolean estActif,
    String telephone,
    LocalDate dateNaissance,
    String RIB,
    KycStatus statutKYC,
    UUID walletTrackingId,
    String pinCode) {}
