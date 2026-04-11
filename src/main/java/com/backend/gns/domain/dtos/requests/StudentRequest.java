package com.backend.gns.domain.dtos.requests;

public record StudentRequest(
        String nom,
        String prenom,
        String email,
        String motDePasse,
        String telephone,
        String matriculeUL,
        String niveau,
        String mentionBac,
        Integer creditsValides,
        String RIB,
        String cheminCarteEtu,
        String cheminReleve,
        boolean mandatSigne,
        String dateMandatSigne
) {}
