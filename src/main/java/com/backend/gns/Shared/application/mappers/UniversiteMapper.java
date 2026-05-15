package com.backend.gns.Shared.application.mappers;

import com.backend.gns.Shared.application.dtos.requests.UniversiteRequest;
import com.backend.gns.Shared.application.dtos.responses.UniversiteResponse;
import com.backend.gns.Shared.domain.models.Universite;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UniversiteMapper {

    public Universite toEntity(UniversiteRequest request) {
        if (request == null) return null;
        Universite entity = new Universite();
        entity.setTrackingId(UUID.randomUUID());
        entity.setCode(request.code());
        entity.setNom(request.nom());
        entity.setVille(request.ville());
        entity.setEstActive(request.estActive());
        return entity;
    }

    public UniversiteResponse toResponse(Universite entity) {
        if (entity == null) return null;
        return UniversiteResponse.builder()
            .trackingId(entity.getTrackingId())
            .code(entity.getCode())
            .nom(entity.getNom())
            .ville(entity.getVille())
            .estActive(entity.isEstActive())
            .build();
    }
}
