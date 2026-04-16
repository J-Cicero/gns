package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.AdminRequest;
import com.backend.gns.application.dtos.responses.AdminResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminService {

    AdminResponse create(AdminRequest request);

    Optional<AdminResponse> findByTrackingId(UUID trackingId);

    AdminResponse update(UUID trackingId, AdminRequest request);

    void delete(UUID trackingId);

    List<AdminResponse> findAll();
}
