package com.backend.gns.commerce.domain.services;

import com.backend.gns.commerce.application.dtos.requests.StudentLiquidationRequest;
import com.backend.gns.commerce.application.dtos.responses.StudentLiquidationResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentLiquidationService {
    StudentLiquidationResponse create(StudentLiquidationRequest request);
    Optional<StudentLiquidationResponse> findByTrackingId(UUID trackingId);
    List<StudentLiquidationResponse> findByStudentId(UUID studentId);
    BigDecimal getPendingTotal();
    StudentLiquidationResponse validerLiquidation(UUID trackingId, String referenceVirement);
}
