package com.backend.gns.commerce.application.dtos.requests;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.backend.gns.commerce.domain.enums.CommandeStatut;

import lombok.Builder;

@Builder
public record CommandeRequest(
    String reference,
    UUID studentTrackingId,
    UUID boutiqueTrackingId,
    BigDecimal montantTotal,
    LocalDateTime dateCommande,
    String pinCode,
    CommandeStatut statut) {}
