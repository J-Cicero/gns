package com.backend.gns.student.domain.services;

import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import com.backend.gns.student.application.dtos.requests.StudentRequest;
import com.backend.gns.student.application.dtos.responses.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface StudentService {

  StudentResponse create(StudentRequest request, MultipartFile rib, MultipartFile mandat);

  Optional<StudentResponse> findByTrackingId(UUID trackingId);

  StudentResponse update(UUID trackingId, StudentRequest request);

  StudentResponse assignerMatricule(UUID trackingId, String matricule);

  void delete(UUID trackingId);

  Page<StudentResponse> findByStatutKYC(KycStatus statutKYC, Pageable pageable);

  Page<StudentResponse> findAll(Pageable pageable);

  Page<StudentResponse> findByUniversiteTrackingId(UUID universiteTrackingId, Pageable pageable);

  boolean verifyPassword(UUID studentTrackingId, String password);

  Map<String, Object> getCard(UUID studentTrackingId);

  long countAll();

  long countByEstActif(boolean active);

  long countByStatutKYC(KycStatus kycStatus);
}
