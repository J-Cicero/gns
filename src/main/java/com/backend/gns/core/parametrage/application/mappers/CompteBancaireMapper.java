package com.backend.gns.core.parametrage.application.mappers;

import com.backend.gns.core.parametrage.application.dtos.responses.CompteBancaireResponse;
import com.backend.gns.core.parametrage.domain.models.CompteBancaire;
import org.springframework.stereotype.Component;

@Component
public class CompteBancaireMapper {
    public CompteBancaireResponse toResponse(CompteBancaire entity) {
        if (entity == null) return null;
        return new CompteBancaireResponse(
            entity.getTrackingId(),
            entity.getAccountNumber(),
            entity.getBank() != null ? entity.getBank().getName() : "N/A",
            entity.getOwnerType() != null ? entity.getOwnerType().name() : "N/A",
            null, // ribUrl (pas encore gérée ici)
            entity.getRibDocumentTrackingId()
        );
    }
}
