package com.backend.gns.Shared.domain.services;

import com.backend.gns.Shared.application.dtos.requests.UniversiteRequest;
import com.backend.gns.Shared.application.dtos.responses.UniversiteResponse;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UniversiteService {
    UniversiteResponse create(UniversiteRequest request);
    Optional<UniversiteResponse> findByTrackingId(UUID trackingId);
    Page<UniversiteResponse> findAll(Pageable pageable);
    void delete(UUID trackingId);
}
