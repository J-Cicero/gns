package com.backend.gns.user.application.dtos.responses;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankFinancialSummaryResponse {
    private BigDecimal totalScolariteUniversites;
    private BigDecimal totalDepensesAchats;
    private BigDecimal totalCommissionsAchats;
    private BigDecimal totalNetCommercants;
    private BigDecimal totalCommissionsBanque;
    private List<BigDecimal> monthlyProfits;
}
