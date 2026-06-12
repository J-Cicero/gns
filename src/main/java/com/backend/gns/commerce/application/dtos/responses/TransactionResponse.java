package com.backend.gns.commerce.application.dtos.responses;

import com.backend.gns.commerce.domain.enums.TransactionStatut;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
    UUID trackingId,
    String studentName,
    String boutiqueName,
    BigDecimal montantDebite,
    BigDecimal montantNetBoutique,
    BigDecimal commissionTotale,
    BigDecimal commissionGns,
    BigDecimal commissionBanque,
    LocalDateTime date,
    TransactionStatut statut
) {}
