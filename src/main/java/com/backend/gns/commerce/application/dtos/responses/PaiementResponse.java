package com.backend.gns.commerce.application.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.backend.gns.commerce.domain.enums.PaiementStatut;
import com.backend.gns.commerce.domain.enums.PaiementType;

import lombok.Builder;

@Builder
public record PaiementResponse(
    UUID trackingId,
    UUID commandeTrackingId,
    UUID walletTrackingId,
    BigDecimal commission,
    BigDecimal montantDebite,
    BigDecimal montantNetBoutique, // added
    LocalDateTime date,
    PaiementType typePaiement,
    PaiementStatut statutPaiement,
    String senderName,
    String receiverName,
    String receiverType) {}
