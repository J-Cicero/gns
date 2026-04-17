package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.StudentRequest;
import com.backend.gns.application.dtos.responses.StudentResponse;
import com.backend.gns.domain.enums.KycStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface StudentService {

    StudentResponse create(StudentRequest request);

    Optional<StudentResponse> findByTrackingId(UUID trackingId);

    StudentResponse update(UUID trackingId, StudentRequest request);

    void delete(UUID trackingId);

    Page<StudentResponse> findByStatutKYC(KycStatus statutKYC, Pageable pageable);

    Page<StudentResponse> findAll(Pageable pageable);
}
