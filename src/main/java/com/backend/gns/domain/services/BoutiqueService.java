package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.BoutiqueRequest;
import com.backend.gns.application.dtos.responses.BoutiqueResponse;
import com.backend.gns.domain.enums.KycStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface BoutiqueService {

    BoutiqueResponse create(BoutiqueRequest request);

    Optional<BoutiqueResponse> findByTrackingId(UUID trackingId);

    BoutiqueResponse update(UUID trackingId, BoutiqueRequest request);

    void delete(UUID trackingId);

    Page<BoutiqueResponse> findByMerchantTrackingId(UUID merchantTrackingId, Pageable pageable);

    Optional<BoutiqueResponse> findByWalletTrackingId(UUID walletTrackingId);

    Page<BoutiqueResponse> findByStatutKYC(KycStatus statutKYC, Pageable pageable);

    Page<BoutiqueResponse> findAll(Pageable pageable);
}
