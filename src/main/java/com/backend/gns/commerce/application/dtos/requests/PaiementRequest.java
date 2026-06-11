package com.backend.gns.commerce.application.dtos.requests;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.backend.gns.commerce.domain.enums.PaiementStatut;
import com.backend.gns.commerce.domain.enums.PaiementType;

import lombok.Builder;

@Builder
public record PaiementRequest(
    UUID commandeTrackingId,
    UUID walletTrackingId,
    BigDecimal montantDebite,
    LocalDateTime date,
    PaiementType typePaiement,
    PaiementStatut statutPaiement) {}
