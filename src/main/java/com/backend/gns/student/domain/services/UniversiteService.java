package com.backend.gns.student.domain.services;

import com.backend.gns.student.application.dtos.requests.UniversiteRequest;
import com.backend.gns.student.application.dtos.responses.UniversiteResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UniversiteService {
  UniversiteResponse create(UniversiteRequest request);

  Optional<UniversiteResponse> findByTrackingId(UUID trackingId);

  Page<UniversiteResponse> findAll(Pageable pageable);

  void delete(UUID trackingId);

  UniversiteResponse updateEtat(UUID trackingId, boolean etat);

  List<Map<String, Object>> getSummaryStats();
}
