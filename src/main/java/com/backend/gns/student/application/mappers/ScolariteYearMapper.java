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
    entity.setLabel(request.label());
    entity.setStartDate(request.startDate());
    entity.setEndDate(request.endDate());
    entity.setOpen(request.isOpen());
    return entity;
  }

  public ScolariteYearResponse toResponse(ScolariteYear entity) {
    if (entity == null) {
      return null;
    }
    return new ScolariteYearResponse(
        entity.getTrackingId(),
        entity.getLabel(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.isOpen(),
        entity.isClosed());
  }
}
