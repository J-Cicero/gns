package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.Shared.domain.enums.KycStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;


@Builder
public record StudentResponse(
    UUID trackingId,
    String email,
    String nom,
    String prenom,
    Boolean estActif,
    String telephone,
    LocalDateTime dateNaissance,
    String numEtudiantUL,
    KycStatus statutKYC,
    UUID walletTrackingId,
    UUID banqueEtudiantTrackingId,
    UUID universiteTrackingId,
    String pinCode
) {}
