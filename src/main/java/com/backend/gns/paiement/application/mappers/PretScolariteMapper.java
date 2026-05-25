package com.backend.gns.paiement.application.mappers;

import com.backend.gns.paiement.application.dtos.responses.PretScolariteResponse;
import com.backend.gns.paiement.domain.models.PretScolarite;
import org.springframework.stereotype.Component;

@Component
public class PretScolariteMapper {

    public PretScolariteResponse toResponse(PretScolarite entity) {
        if (entity == null) return null;

        return PretScolariteResponse.builder()
            .trackingId(entity.getTrackingId())
            .studentTrackingId(entity.getStudent() != null ? entity.getStudent().getTrackingId() : null)
            .studentNom(entity.getStudent() != null ? entity.getStudent().getNom() + " " + entity.getStudent().getPrenom() : null)
            .universiteTrackingId(entity.getUniversite() != null ? entity.getUniversite().getTrackingId() : null)
            .universiteNom(entity.getUniversite() != null ? entity.getUniversite().getNom() : null)
            .montant(entity.getMontant())
            .estRembourse(entity.isEstRembourse())
            .description(entity.getDescription())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}
