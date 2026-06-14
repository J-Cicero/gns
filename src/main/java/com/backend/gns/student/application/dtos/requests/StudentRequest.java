package com.backend.gns.student.application.dtos.requests;

import com.backend.gns.core.domain.enums.KycStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record StudentRequest(
    String email,
    String password,
    String nom,
    String prenom,
    Boolean estActif,
    String telephone,
    LocalDateTime dateNaissance,
    String matricule,
    KycStatus statutKYC,
    UUID walletTrackingId,
    UUID banqueTrackingId,
    String numeroCompte,
    UUID universiteTrackingId,
    String pinCode) {}
