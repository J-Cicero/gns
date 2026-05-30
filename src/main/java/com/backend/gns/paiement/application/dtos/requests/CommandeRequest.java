package com.backend.gns.paiement.application.dtos.requests;

import com.backend.gns.paiement.domain.enums.CommandeStatut;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
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
