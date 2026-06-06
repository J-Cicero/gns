package com.backend.gns.core.systemstatus.dtos;

import java.math.BigDecimal;

public record ReconciliationReportDTO(
    String nomBanque,
    BigDecimal totalPaiementsScolarite,
    BigDecimal totalReverseParBanque,
    BigDecimal soldeDuParLaBanque) {}
