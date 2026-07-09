package com.backend.gns.commerce.application.dtos.responses;

import java.math.BigDecimal;
import java.util.List;

public record TransactionChartStatsResponse(
    List<MonthlyVolume> monthlyVolume,
    List<BoutiqueShare> boutiqueShare
) {
    public record MonthlyVolume(String label, BigDecimal volume) {}
    public record BoutiqueShare(String label, BigDecimal volume) {}
}
