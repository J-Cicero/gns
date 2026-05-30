package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.core.domain.enums.KycStatus;
import java.math.BigDecimal;
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
    String numEtudiantUniv,
    KycStatus statutKYC,
    UUID walletTrackingId,
    BigDecimal solde,
    UUID banqueEtudiantTrackingId,
    UUID universiteTrackingId,
    String universiteNom,
    String pinCode) {}
