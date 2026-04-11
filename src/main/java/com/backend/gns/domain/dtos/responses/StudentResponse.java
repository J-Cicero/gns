package com.backend.gns.domain.dtos.responses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record StudentResponse(
        UUID trackingId,
        String nom,
        String prenom,
        String email,
        String telephone,
        LocalDate dateInscription,
        boolean estActif,
        String matriculeUL,
        String niveau,
        String mentionBac,
        Integer creditsValides,
        String RIB,
        String cheminCarteEtu,
        String cheminReleve,
        boolean mandatSigne,
        LocalDate dateMandatSigne,
        String statutKYC,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
