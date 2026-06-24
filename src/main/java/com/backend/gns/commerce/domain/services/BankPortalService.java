package com.backend.gns.commerce.domain.services;

import com.backend.gns.user.application.dtos.responses.BankFinancialSummaryResponse;
import com.backend.gns.user.application.dtos.responses.BanqueInfoResponse;

import com.backend.gns.commerce.application.dtos.responses.BoutiqueLiquidationInfoResponse;
import com.backend.gns.commerce.application.dtos.responses.StudentLiquidationInfoResponse;

import java.util.List;
import java.util.UUID;
public interface BankPortalService {
    BankFinancialSummaryResponse getFinancialSummary(UUID bankOperatorTrackingId);
    BanqueInfoResponse getBanqueInfo(UUID bankOperatorTrackingId);
    List<StudentLiquidationInfoResponse> getStudents(UUID bankOperatorTrackingId);
    List<BoutiqueLiquidationInfoResponse> getBoutiques(UUID bankOperatorTrackingId);
}
