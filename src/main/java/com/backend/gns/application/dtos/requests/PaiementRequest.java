package com.backend.gns.application.dtos.requests;

import com.backend.gns.domain.enums.PaiementStatut;
import com.backend.gns.domain.enums.PaiementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record PaiementRequest(
    UUID commandeTrackingId,
    UUID walletTrackingId,
    BigDecimal commission,
    BigDecimal montantDebite,
    LocalDateTime date,
    PaiementType typePaiement,
    PaiementStatut statutPaiement) {}
