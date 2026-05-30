package com.backend.gns.parametrage.application.mappers;

import com.backend.gns.parametrage.application.dtos.requests.ParametreDbsRequest;
import com.backend.gns.parametrage.application.dtos.responses.ParametreDbsResponse;
import com.backend.gns.parametrage.domain.models.ParametreDbs;
import org.springframework.stereotype.Component;

@Component
public class ParametreDbsMapper {

    public ParametreDbs toEntity(ParametreDbsRequest request) {
        if (request == null) return null;
        ParametreDbs entity = new ParametreDbs();
        entity.setNomParametre(request.nomParametre());
        entity.setValeurParametre(request.valeurParametre());
        entity.setEstActif(request.estActif());
        entity.setDescription(request.description());
        return entity;
    }

    public ParametreDbsResponse toResponse(ParametreDbs entity) {
        if (entity == null) return null;
        return ParametreDbsResponse.builder()
                .trackingId(entity.getTrackingId())
                .nomParametre(entity.getNomParametre())
                .valeurParametre(entity.getValeurParametre())
                .estActif(entity.isEstActif())
                .description(entity.getDescription())
                .build();
    }
}
