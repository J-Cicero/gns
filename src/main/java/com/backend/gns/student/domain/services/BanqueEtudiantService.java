package com.backend.gns.student.domain.services;

import com.backend.gns.student.application.dtos.requests.BanqueEtudiantRequest;
import com.backend.gns.student.application.dtos.responses.BanqueEtudiantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface BanqueEtudiantService {
    
    BanqueEtudiantResponse create(BanqueEtudiantRequest request);
    
    Optional<BanqueEtudiantResponse> findByTrackingId(UUID trackingId);
    
    BanqueEtudiantResponse update(UUID trackingId, BanqueEtudiantRequest request);
    
    void delete(UUID trackingId);
    
    Page<BanqueEtudiantResponse> findAll(Pageable pageable);
    
    Page<BanqueEtudiantResponse> findByStudentTrackingId(UUID studentTrackingId, 
        Pageable pageable);
}
