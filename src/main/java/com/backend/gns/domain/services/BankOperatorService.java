package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.BankOperatorRequest;
import com.backend.gns.application.dtos.responses.BankOperatorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface BankOperatorService {

    BankOperatorResponse create(BankOperatorRequest request);

    Optional<BankOperatorResponse> findByTrackingId(UUID trackingId);

    BankOperatorResponse update(UUID trackingId, BankOperatorRequest request);

    void delete(UUID trackingId);
    
    Page<BankOperatorResponse> findAll(Pageable pageable);
}
