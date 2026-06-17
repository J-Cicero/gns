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

        Boutique boutique = boutiqueRepository.findByTrackingId(request.getBoutiqueTrackingId())
                .orElseThrow(() -> new RuntimeException("Boutique introuvable avec le trackingId: " + request.getBoutiqueTrackingId()));

        Liquidation liquidation = new Liquidation();
                liquidation.setTrackingId(UUID.randomUUID().toString());
        liquidation.setBoutique(boutique);
        liquidation.setAmountToLiquidate(request.getAmountToLiquidate());
        liquidation.setStatus(LiquidationStatut.EN_ATTENTE); 
        liquidation.setCreatedAt(LocalDateTime.now());
        

        return liquidation;
    }

   
    public LiquidationResponse toResponse(Liquidation entity) {
        if (entity == null) {
            return null;
        }

        // Récupération sécurisée du nom de la boutique
        String boutiqueName = "Unknown";
        if (entity.getBoutique() != null && entity.getBoutique().getTrackingId() != null) {
            boutiqueName = boutiqueRepository.findByTrackingId(entity.getBoutique().getTrackingId())
                    .map(Boutique::getName) 
                    .orElse("Unknown");
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