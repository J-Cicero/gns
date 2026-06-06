package com.backend.gns.user.domain.services;

import com.backend.gns.user.application.dtos.requests.BankOperatorRequest;
import com.backend.gns.user.application.dtos.responses.BankOperatorResponse;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BankOperatorService {

  BankOperatorResponse create(BankOperatorRequest request);

  Optional<BankOperatorResponse> findByTrackingId(UUID trackingId);

  BankOperatorResponse update(UUID trackingId, BankOperatorRequest request);

  void delete(UUID trackingId);

  Page<BankOperatorResponse> findAll(Pageable pageable);
}
