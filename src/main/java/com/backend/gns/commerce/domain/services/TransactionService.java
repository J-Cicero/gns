package com.backend.gns.commerce.domain.services;

import com.backend.gns.commerce.application.dtos.requests.TransactionRequest;
import com.backend.gns.commerce.application.dtos.responses.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface TransactionService {
    TransactionResponse createPayment(TransactionRequest request);
    Optional<TransactionResponse> findByTrackingId(UUID trackingId);
    Page<TransactionResponse> findAll(Pageable pageable);
    Page<TransactionResponse> findByBoutiqueId(UUID boutiqueId, Pageable pageable);
    Page<TransactionResponse> findByStudentId(UUID studentId, Pageable pageable);
    com.backend.gns.commerce.application.dtos.responses.TransactionStatsResponse getGlobalStats();
}
