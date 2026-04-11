package com.backend.gns.domain.dtos.requests;

public record MerchantRequest(
        String nom,
        String prenom,
        String email,
        String motDePasse,
        String telephone,
        String nomBoutique,
        String cheminCarteEDJ,
        String categorieShop
) {}
