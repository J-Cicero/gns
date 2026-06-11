package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.responses.LiquidationResponse;
import com.backend.gns.commerce.domain.models.Liquidation;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LiquidationMapper {

    private final BoutiqueRepository boutiqueRepository;

    public LiquidationResponse toResponse(Liquidation entity) {
        String boutiqueName = boutiqueRepository.findByTrackingId(entity.getBoutique().getTrackingId())
                .map(b -> b.getNomBoutique())
                .orElse("Inconnu");

        return new LiquidationResponse(
            entity.getTrackingId(),
            boutiqueName,
            entity.getMontantALiquider(),
            entity.getDateCreation(),
            entity.getDateValidation(),
            entity.getStatut(),
            entity.getReferenceVirement()
        );
    }
}
