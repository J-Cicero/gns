package com.backend.gns.domain.dtos.responses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record MerchantResponse(
        UUID trackingId,
        String nom,
        String prenom,
        String email,
        String telephone,
        LocalDate dateInscription,
        boolean estActif,
        String nomBoutique,
        String cheminCarteEDJ,
        String categorieShop,
        String statutKYC,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
