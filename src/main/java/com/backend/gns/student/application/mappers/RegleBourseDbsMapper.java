package com.backend.gns.student.application.mappers;

import com.backend.gns.student.application.dtos.requests.RegleBourseDbsRequest;
import com.backend.gns.student.application.dtos.responses.RegleBourseDbsResponse;
import com.backend.gns.student.domain.models.RegleBourseDbs;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RegleBourseDbsMapper {

    public RegleBourseDbs toEntity(RegleBourseDbsRequest request) {
        if (request == null) return null;
        RegleBourseDbs entity = new RegleBourseDbs();
        entity.setTrackingId(UUID.randomUUID());
        entity.setTypeRegle(request.typeRegle());
        entity.setValeurCritere(request.valeurCritere());
        entity.setEstActif(request.estActif());
        entity.setDescription(request.description());
        return entity;
    }

    public RegleBourseDbsResponse toResponse(RegleBourseDbs entity) {
        if (entity == null) return null;
        return RegleBourseDbsResponse.builder()
            .trackingId(entity.getTrackingId())
            .typeRegle(entity.getTypeRegle())
            .valeurCritere(entity.getValeurCritere())
            .estActif(entity.isEstActif())
            .description(entity.getDescription())
            .build();
    }
}
