package com.backend.gns.core.parametrage.domain.services;

import com.backend.gns.core.parametrage.application.dtos.requests.CompteBancaireRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.CompteBancaireResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompteBancaireService {
    CompteBancaireResponse create(CompteBancaireRequest request);
    Optional<CompteBancaireResponse> findByTrackingId(UUID trackingId);
    List<CompteBancaireResponse> findAll();
    Optional<CompteBancaireResponse> findByOwnerTrackingId(UUID ownerTrackingId);
    void delete(UUID trackingId);
}
