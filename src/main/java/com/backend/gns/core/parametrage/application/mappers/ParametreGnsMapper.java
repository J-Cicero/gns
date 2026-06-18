package com.backend.gns.core.parametrage.application.mappers;

import com.backend.gns.core.parametrage.application.dtos.requests.ParametreGnsRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.ParametreGnsResponse;
import com.backend.gns.core.parametrage.domain.models.ParametreGns;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class ParametreGnsMapper {

  public ParametreGns toEntity(ParametreGnsRequest request) {
    if (request == null) return null;
    ParametreGns entity = new ParametreGns();
    entity.setTrackingId(UUID.randomUUID()); // Always generate new trackingId for new entity
    entity.setNomParametre(request.nomParametre());
    entity.setValeurParametre(request.valeurParametre());
    entity.setDescription(request.description());
    return entity;
  }

  public ParametreGnsResponse toResponse(ParametreGns entity) {
    if (entity == null) return null;
    return ParametreGnsResponse.builder()
        .trackingId(entity.getTrackingId()) // Added trackingId
        .nomParametre(entity.getNomParametre())
        .valeurParametre(entity.getValeurParametre())
        .description(entity.getDescription())
        .build();
  }
}
