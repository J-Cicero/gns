package com.backend.gns.core.parametrage.application.mappers;

import com.backend.gns.core.parametrage.application.dtos.requests.ParametreGnsRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.ParametreGnsResponse;
import com.backend.gns.core.parametrage.domain.models.ParametreGns;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ParametreGnsMapper {

  public ParametreGns toEntity(ParametreGnsRequest request) {
    if (request == null) return null;
    ParametreGns entity = new ParametreGns();
    entity.setNomParametre(request.nomParametre().name());
    entity.setValeurParametre(request.valeurParametre());
    entity.setDescription(request.description());
    return entity;
  }

  public ParametreGnsResponse toResponse(ParametreGns entity) {
    if (entity == null) return null;
    return ParametreGnsResponse.builder()
        .nomParametre(com.backend.gns.core.parametrage.domain.enums.TypeParametreGns.valueOf(entity.getNomParametre()))
        .valeurParametre(entity.getValeurParametre())
        .description(entity.getDescription())
        .build();
  }
}
