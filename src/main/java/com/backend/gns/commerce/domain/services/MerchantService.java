package com.backend.gns.commerce.domain.services;

import com.backend.gns.commerce.application.dtos.requests.MerchantRequest;
import com.backend.gns.commerce.application.dtos.responses.MerchantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MerchantService {

  MerchantResponse create(MerchantRequest request, org.springframework.web.multipart.MultipartFile rib);

  Optional<MerchantResponse> findByTrackingId(UUID trackingId);

  MerchantResponse update(UUID trackingId, MerchantRequest request);

  void delete(UUID trackingId);

  Page<MerchantResponse> findAll(Pageable pageable);
}
