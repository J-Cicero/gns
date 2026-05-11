package com.backend.gns.admin.domain.services;

import com.backend.gns.admin.application.dtos.requests.AdminRequest;
import com.backend.gns.admin.application.dtos.responses.AdminResponse;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {

  AdminResponse create(AdminRequest request);

  Optional<AdminResponse> findByTrackingId(UUID trackingId);

  AdminResponse update(UUID trackingId, AdminRequest request);

  void delete(UUID trackingId);

  Page<AdminResponse> findAll(Pageable pageable);
}
