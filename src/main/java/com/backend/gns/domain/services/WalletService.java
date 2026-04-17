package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.WalletRequest;
import com.backend.gns.application.dtos.responses.WalletResponse;
import com.backend.gns.domain.enums.WalletStatus;
import com.backend.gns.domain.enums.WalletType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface WalletService {

    WalletResponse create(WalletRequest request);

    Optional<WalletResponse> findByTrackingId(UUID trackingId);

    WalletResponse update(UUID trackingId, WalletRequest request);

    void delete(UUID trackingId);

    Page<WalletResponse> findByTypeWallet(WalletType typeWallet, Pageable pageable);

    Page<WalletResponse> findByEstVerrouille(Boolean estVerrouille, Pageable pageable);

    Page<WalletResponse> findByStatutWallet(WalletStatus statutWallet, Pageable pageable);

    Page<WalletResponse> findBySoldeLessThan(BigDecimal amount, Pageable pageable);

    Page<WalletResponse> findBySoldeGreaterThan(BigDecimal amount, Pageable pageable);

    Page<WalletResponse> findAll(Pageable pageable);
}
