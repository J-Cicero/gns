package com.backend.gns.student.domain.services;

import com.backend.gns.student.application.dtos.requests.UniversityAdminRequest;
import com.backend.gns.student.application.dtos.responses.UniversityAdminResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UniversityAdminService {
    UniversityAdminResponse create(UniversityAdminRequest request);

    Optional<UniversityAdminResponse> findByTrackingId(UUID trackingId);
    
    UniversityAdminResponse update(UUID trackingId, UniversityAdminRequest request);
    
    void delete(UUID trackingId);
    
    Page<UniversityAdminResponse> findAll(Pageable pageable);

    Page<UniversityAdminResponse> findByUniversiteTrackingId(UUID universiteTrackingId, Pageable pageable);
}
