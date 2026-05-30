package com.backend.gns.student.domain.services;

import com.backend.gns.core.domain.enums.KycStatus;
import com.backend.gns.student.application.dtos.requests.StudentRequest;
import com.backend.gns.student.application.dtos.responses.StudentResponse;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {

  StudentResponse create(StudentRequest request);

  Optional<StudentResponse> findByTrackingId(UUID trackingId);

  StudentResponse update(UUID trackingId, StudentRequest request);

  void delete(UUID trackingId);

  Page<StudentResponse> findByStatutKYC(KycStatus statutKYC, Pageable pageable);

  Page<StudentResponse> findAll(Pageable pageable);

  Page<StudentResponse> findByUniversiteTrackingId(UUID universiteTrackingId, Pageable pageable);

  boolean verifyPin(UUID studentTrackingId, String pinCode);

  Map<String, Object> getCard(UUID studentTrackingId);

  long countAll();

  long countByEstActif(boolean active);

  long countByStatutKYC(KycStatus kycStatus);
}
