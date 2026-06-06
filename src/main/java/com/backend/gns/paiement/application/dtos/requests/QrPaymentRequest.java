package com.backend.gns.paiement.application.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record QrPaymentRequest(
    @NotNull(message = "L'ID de la boutique est requis") UUID boutiqueTrackingId,
    @NotNull(message = "Le token QR de l'étudiant est requis") String studentQrToken,
    @NotNull(message = "Le montant total est requis")
        @Positive(message = "Le montant doit être positif")
        BigDecimal montantTotal,
    String description // Optionnel, ex: achat de repas
    ) {}
