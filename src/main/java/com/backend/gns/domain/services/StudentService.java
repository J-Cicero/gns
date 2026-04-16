package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.StudentRequest;
import com.backend.gns.application.dtos.responses.StudentResponse;
import com.backend.gns.domain.enums.KycStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentService {

    StudentResponse create(StudentRequest request);

    Optional<StudentResponse> findByTrackingId(UUID trackingId);

    StudentResponse update(UUID trackingId, StudentRequest request);

    void delete(UUID trackingId);

    List<StudentResponse> findByStatutKYC(KycStatus statutKYC);

    List<StudentResponse> findAll();
}
