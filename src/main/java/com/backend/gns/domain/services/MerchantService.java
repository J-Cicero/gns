package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.MerchantRequest;
import com.backend.gns.application.dtos.responses.MerchantResponse;
import com.backend.gns.domain.enums.KycStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MerchantService {

    MerchantResponse create(MerchantRequest request);

    Optional<MerchantResponse> findByTrackingId(UUID trackingId);

    MerchantResponse update(UUID trackingId, MerchantRequest request);

    void delete(UUID trackingId);

    Page<MerchantResponse> findByStatutKYC(KycStatus statutKYC, Pageable pageable);

    Page<MerchantResponse> findAll(Pageable pageable);
}
