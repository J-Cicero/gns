package com.backend.gns.domain.services;

import com.backend.gns.domain.dtos.requests.AdminRequest;
import com.backend.gns.domain.dtos.responses.AdminResponse;

import java.util.List;
import java.util.UUID;

public interface AdminService {

    AdminResponse create(AdminRequest request);

    List<AdminResponse> getAll();

    AdminResponse getByTrackingId(UUID trackingId);

    AdminResponse update(UUID trackingId, AdminRequest request);

    void delete(UUID trackingId);
}
