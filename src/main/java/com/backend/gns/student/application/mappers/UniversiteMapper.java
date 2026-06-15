package com.backend.gns.student.application.mappers;

import com.backend.gns.student.application.dtos.requests.UniversiteRequest;
import com.backend.gns.student.application.dtos.responses.UniversiteResponse;
import com.backend.gns.student.domain.models.Universite;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UniversiteMapper {

  public Universite toEntity(UniversiteRequest request) {
    if (request == null) return null;
    Universite entity = new Universite();
    entity.setTrackingId(UUID.randomUUID());
    entity.setCode(request.code());
    entity.setFullName(request.fullName());
    entity.setCity(request.city());
    entity.setActive(request.isActive());
    return entity;
  }

  public UniversiteResponse toResponse(Universite entity) {
    if (entity == null) return null;
    return UniversiteResponse.builder()
        .trackingId(entity.getTrackingId())
        .code(entity.getCode())
        .fullName(entity.getFullName())
        .city(entity.getCity())
        .isActive(entity.isActive())
        .build();
  }
}
