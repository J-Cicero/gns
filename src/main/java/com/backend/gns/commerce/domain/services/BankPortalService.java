package com.backend.gns.commerce.domain.services;

import com.backend.gns.user.application.dtos.responses.BankFinancialSummaryResponse;
import com.backend.gns.user.application.dtos.responses.BanqueInfoResponse;

import java.util.UUID;

public interface BankPortalService {
    BankFinancialSummaryResponse getFinancialSummary(UUID bankOperatorTrackingId);
    BanqueInfoResponse getBanqueInfo(UUID bankOperatorTrackingId);
}
