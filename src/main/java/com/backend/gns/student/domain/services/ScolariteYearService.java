package com.backend.gns.student.domain.services;

import com.backend.gns.student.application.dtos.requests.ScolariteYearRequest;
import com.backend.gns.student.application.dtos.responses.ScolariteYearResponse;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScolariteYearService {

  ScolariteYearResponse create(ScolariteYearRequest request);

  Optional<ScolariteYearResponse> findByTrackingId(UUID trackingId);

  ScolariteYearResponse cloturerEtOuvrirNouvelle(UUID oldTrackingId, ScolariteYearRequest newYearRequest);

  Page<ScolariteYearResponse> findAll(Pageable pageable);

  Optional<ScolariteYearResponse> findActiveYear();
}
