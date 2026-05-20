package com.backend.gns.Shared.application.mappers;

import com.backend.gns.Shared.application.dtos.requests.ParametreGnsRequest;
import com.backend.gns.Shared.application.dtos.responses.ParametreGnsResponse;
import com.backend.gns.Shared.domain.models.ParametreGns;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ParametreGnsMapper {

    public ParametreGns toEntity(ParametreGnsRequest request) {
        if (request == null) return null;
        ParametreGns entity = new ParametreGns();
        entity.setTrackingId(UUID.randomUUID());
        entity.setNomParametre(request.nomParametre());
        entity.setValeurParametre(request.valeurParametre());
        entity.setDescription(request.description());
        entity.setEstActif(request.estActif());
        return entity;
    }

    public ParametreGnsResponse toResponse(ParametreGns entity) {
        if (entity == null) return null;
        return ParametreGnsResponse.builder()
            .trackingId(entity.getTrackingId())
            .nomParametre(entity.getNomParametre())
            .valeurParametre(entity.getValeurParametre())
            .description(entity.getDescription())
            .estActif(entity.isEstActif())
            .build();
    }
}
