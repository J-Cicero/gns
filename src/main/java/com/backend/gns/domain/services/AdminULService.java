package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.AdminULRequest;
import com.backend.gns.application.dtos.responses.AdminULResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface AdminULService {
    AdminULResponse create(AdminULRequest request);

    Optional<AdminULResponse> findByTrackingId(UUID trackingId);
    
    AdminULResponse update(UUID trackingId, AdminULRequest request);
    
    void delete(UUID trackingId);
    
    Page<AdminULResponse> findAll(Pageable pageable);
}
