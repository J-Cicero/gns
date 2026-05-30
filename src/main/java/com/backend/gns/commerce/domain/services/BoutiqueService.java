package com.backend.gns.commerce.domain.services;

import com.backend.gns.commerce.application.dtos.requests.BoutiqueRequest;
import com.backend.gns.commerce.application.dtos.responses.BoutiqueResponse;
import com.backend.gns.core.domain.enums.KycStatus;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoutiqueService {

  BoutiqueResponse create(BoutiqueRequest request);

  Optional<BoutiqueResponse> findByTrackingId(UUID trackingId);

  BoutiqueResponse update(UUID trackingId, BoutiqueRequest request);

  void delete(UUID trackingId);

  Page<BoutiqueResponse> findByMerchantTrackingId(UUID merchantTrackingId, Pageable pageable);

  Optional<BoutiqueResponse> findByWalletTrackingId(UUID walletTrackingId);

  Page<BoutiqueResponse> findByStatutKYC(KycStatus statutKYC, Pageable pageable);

  Page<BoutiqueResponse> findAll(Pageable pageable);

  Page<BoutiqueResponse> getBoutiquesEnAlerteQuota(BigDecimal seuilPourcentage, Pageable pageable);
}
