package com.backend.gns.commerce.application.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.backend.gns.commerce.domain.enums.CommandeStatut;

import lombok.Builder;

@Builder
public record CommandeResponse(
    UUID trackingId,
    String reference,
    UUID studentTrackingId,
    UUID boutiqueTrackingId,
    BigDecimal montantTotal,
    LocalDateTime dateCommande,
    CommandeStatut statut) {}
