package com.backend.gns.application.dtos.responses;

import com.backend.gns.domain.enums.PaiementStatut;
import com.backend.gns.domain.enums.PaiementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PaiementResponse(
    UUID trackingId,
    UUID commandeTrackingId,
    UUID walletTrackingId,
    BigDecimal montantCommande,
    BigDecimal commission,
    BigDecimal montantDebite,
    LocalDateTime date,
    PaiementType typePaiement,
    PaiementStatut statutPaiement) {}
