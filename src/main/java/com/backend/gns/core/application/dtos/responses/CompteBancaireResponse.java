package com.backend.gns.core.application.dtos.responses;

import java.util.UUID;

public record CompteBancaireResponse(
    UUID trackingId,
    String numeroCompte,
    String banqueNom,
    String typeProprietaire
) {}
