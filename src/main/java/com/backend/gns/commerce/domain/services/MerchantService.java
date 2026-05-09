package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.MerchantRequest;
import com.backend.gns.application.dtos.responses.MerchantResponse;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MerchantService {

  MerchantResponse create(MerchantRequest request);

  Optional<MerchantResponse> findByTrackingId(UUID trackingId);

  MerchantResponse update(UUID trackingId, MerchantRequest request);

  void delete(UUID trackingId);

  Page<MerchantResponse> findAll(Pageable pageable);
}
