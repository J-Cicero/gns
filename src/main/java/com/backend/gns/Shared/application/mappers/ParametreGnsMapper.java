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
        entity.setCle(request.cle());
        entity.setValeur(request.valeur());
        entity.setDescription(request.description());
        entity.setEstActif(request.estActif());
        return entity;
    }

    public ParametreGnsResponse toResponse(ParametreGns entity) {
        if (entity == null) return null;
        return ParametreGnsResponse.builder()
            .trackingId(entity.getTrackingId())
            .cle(entity.getCle())
            .valeur(entity.getValeur())
            .description(entity.getDescription())
            .estActif(entity.isEstActif())
            .build();
    }
}
