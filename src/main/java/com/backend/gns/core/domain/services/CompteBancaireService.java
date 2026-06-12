package com.backend.gns.core.domain.services;

import com.backend.gns.core.application.dtos.requests.CompteBancaireRequest;
import com.backend.gns.core.application.dtos.responses.CompteBancaireResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompteBancaireService {
    CompteBancaireResponse create(CompteBancaireRequest request);
    Optional<CompteBancaireResponse> findByTrackingId(UUID trackingId);
    List<CompteBancaireResponse> findAll();
    void delete(UUID trackingId);
}
