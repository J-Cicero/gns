package com.backend.gns.domain.services;

import com.backend.gns.domain.dtos.requests.StudentRequest;
import com.backend.gns.domain.dtos.responses.StudentResponse;

import java.util.List;
import java.util.UUID;

public interface StudentService {

    StudentResponse create(StudentRequest request);

    List<StudentResponse> getAll();

    StudentResponse getByTrackingId(UUID trackingId);

    StudentResponse update(UUID trackingId, StudentRequest request);

    void delete(UUID trackingId);

    /**
     * F1 - Valide le KYC de l'etudiant et cree ses wallets.
     * Cree toujours RELAIS, et HORIZON si eligible et boursier.
     */
    StudentResponse validerKYC(UUID studentTrackingId);
}
