package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.requests.LiquidationRequest;
import com.backend.gns.commerce.application.dtos.responses.LiquidationResponse;
import com.backend.gns.commerce.domain.enums.LiquidationStatut;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.domain.models.Liquidation;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LiquidationMapper {

    private final BoutiqueRepository boutiqueRepository;

    public Liquidation toEntity(LiquidationRequest request) {
        if (request == null) {
            return null;
        }

        Boutique boutique = boutiqueRepository.findByTrackingId(request.boutiqueTrackingId())
                .orElseThrow(() -> new RuntimeException("Boutique introuvable avec le trackingId: " + request.boutiqueTrackingId()));

        Liquidation liquidation = new Liquidation();
        liquidation.setTrackingId(UUID.randomUUID());
        liquidation.setAmountToLiquidate(request.amountToLiquidate());
        liquidation.setStatus(LiquidationStatut.EN_ATTENTE); 
        liquidation.setCreatedAt(LocalDateTime.now());
        
        return liquidation;
    }

    public LiquidationResponse toResponse(Liquidation entity) {
        if (entity == null) {
            return null;
        }

        String boutiqueName = "Unknown";
        if (entity.getTransactions() != null && !entity.getTransactions().isEmpty()) {
            Boutique boutique = entity.getTransactions().get(0).getReceiver();
            if (boutique != null) {
                boutiqueName = boutique.getName();
            }
        }

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