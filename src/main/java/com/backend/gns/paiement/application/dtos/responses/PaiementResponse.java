package com.backend.gns.paiement.application.dtos.responses;

import com.backend.gns.paiement.domain.enums.PaiementStatut;
import com.backend.gns.paiement.domain.enums.PaiementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PaiementResponse(
    UUID trackingId,
    UUID commandeTrackingId,
    UUID walletTrackingId,
    // montantCommande removed
    BigDecimal commission,
    BigDecimal montantDebite,
    BigDecimal montantNetBoutique, // added
    LocalDateTime date,
    PaiementType typePaiement,
    PaiementStatut statutPaiement) {}
