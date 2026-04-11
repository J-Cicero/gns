package com.backend.gns.domain.services;

import com.backend.gns.domain.dtos.requests.WalletRequest;
import com.backend.gns.domain.dtos.responses.WalletResponse;

import java.util.List;
import java.util.UUID;

public interface WalletService {

    WalletResponse create(WalletRequest request);

    List<WalletResponse> getAll();

    WalletResponse getByTrackingId(UUID trackingId);

    WalletResponse update(UUID trackingId, WalletRequest request);

    void delete(UUID trackingId);
}
