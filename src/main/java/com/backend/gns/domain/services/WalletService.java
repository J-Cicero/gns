package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.WalletRequest;
import com.backend.gns.application.dtos.responses.WalletResponse;
import com.backend.gns.domain.enums.WalletType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletService {

    WalletResponse create(WalletRequest request);

    Optional<WalletResponse> findByTrackingId(UUID trackingId);

    WalletResponse update(UUID trackingId, WalletRequest request);

    void delete(UUID trackingId);

    Optional<WalletResponse> findByStudentTrackingId(UUID studentTrackingId);

    List<WalletResponse> findByTypeWallet(WalletType typeWallet);

    List<WalletResponse> findByEstVerrouille(Boolean estVerrouille);

    List<WalletResponse> findAll();
}
