package com.backend.gns.student.application.mappers;

import com.backend.gns.student.application.dtos.requests.ScolariteYearRequest;
import com.backend.gns.student.application.dtos.responses.ScolariteYearResponse;
import com.backend.gns.student.domain.models.ScolariteYear;
import org.springframework.stereotype.Component;

@Component
public class ScolariteYearMapper {

  public ScolariteYear toEntity(ScolariteYearRequest request) {
    if (request == null) {
      return null;
    }
    ScolariteYear entity = new ScolariteYear();
    entity.setLibelle(request.libelle());
    entity.setDateDebut(request.dateDebut());
    entity.setDateFin(request.dateFin());
    entity.setEstOuverte(request.estOuverte());
    return entity;
  }

  public ScolariteYearResponse toResponse(ScolariteYear entity) {
    if (entity == null) {
      return null;
    }
    return new ScolariteYearResponse(
        entity.getTrackingId(),
        entity.getLibelle(),
        entity.getDateDebut(),
        entity.getDateFin(),
        entity.isEstOuverte(),
        entity.isEstCloturee()
    );
  }
}
