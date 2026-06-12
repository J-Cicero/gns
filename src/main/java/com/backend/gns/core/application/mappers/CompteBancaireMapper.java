package com.backend.gns.core.application.mappers;

import com.backend.gns.core.application.dtos.responses.CompteBancaireResponse;
import com.backend.gns.core.domain.models.CompteBancaire;
import org.springframework.stereotype.Component;

@Component
public class CompteBancaireMapper {
    public CompteBancaireResponse toResponse(CompteBancaire entity) {
        if (entity == null) return null;
        return new CompteBancaireResponse(
            entity.getTrackingId(),
            entity.getNumeroCompte() != null ? entity.getNumeroCompte() : (entity.getRibDocument() != null ? entity.getRibDocument().getUrlFichier() : "N/A"),
            entity.getBanque() != null ? entity.getBanque().getNom() : "N/A",
            entity.getTypeProprietaire().name()
        );
    }
}
