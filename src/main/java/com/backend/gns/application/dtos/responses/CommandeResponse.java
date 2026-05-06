package com.backend.gns.application.dtos.responses;

import com.backend.gns.domain.enums.CommandeStatut;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
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
