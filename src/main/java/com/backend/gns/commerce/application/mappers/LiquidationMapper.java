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
        if (entity == null) return null;

        String boutiqueName = boutiqueRepository.findByTrackingId(entity.getBoutique().getTrackingId())
                .map(b -> b.getName())
                .orElse("Unknown");

        return new LiquidationResponse(
            entity.getTrackingId(),
            boutiqueName,
            entity.getAmountToLiquidate(),
            entity.getCreatedAt(),
            entity.getValidatedAt(),
            entity.getStatus(),
            entity.getTransferReference()
        );
    }
}
