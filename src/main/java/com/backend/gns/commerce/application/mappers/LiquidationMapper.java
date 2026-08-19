package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.requests.LiquidationRequest;
import com.backend.gns.commerce.application.dtos.responses.LiquidationResponse;
import com.backend.gns.commerce.domain.enums.LiquidationStatut;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.domain.models.Liquidation;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.core.parametrage.infrastructure.repositories.CompteBancaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LiquidationMapper {

    private final BoutiqueRepository boutiqueRepository;
    private final CompteBancaireRepository compteBancaireRepository;

    public Liquidation toEntity(LiquidationRequest request) {
        if (request == null) {
            return null;
        }

        Boutique boutique = boutiqueRepository.findByTrackingId(request.boutiqueTrackingId())
                .orElseThrow(() -> new RuntimeException("Boutique introuvable avec le trackingId: " + request.boutiqueTrackingId()));

        Liquidation liquidation = new Liquidation();
        liquidation.setTrackingId(UUID.randomUUID());
        liquidation.setBoutique(boutique);
        liquidation.setAmountToLiquidate(request.amountToLiquidate());
        liquidation.setStatus(LiquidationStatut.EN_ATTENTE); 
        liquidation.setCreatedAt(LocalDateTime.now());
        
        return liquidation;
    }

    public LiquidationResponse toResponse(Liquidation entity) {
        if (entity == null) {
            return null;
        }

        String boutiqueName = "Inconnue";
        UUID boutiqueTrackingId = null;
        String merchantName = "Inconnu";
        String accountNumber = "Non renseigné";

        if (entity.getBoutique() != null) {
            boutiqueName = entity.getBoutique().getName();
            boutiqueTrackingId = entity.getBoutique().getTrackingId();
            if (entity.getBoutique().getMerchant() != null) {
                merchantName = (entity.getBoutique().getMerchant().getFirstName() + " " + entity.getBoutique().getMerchant().getLastName()).trim();
                if (compteBancaireRepository != null && entity.getBoutique().getMerchant().getTrackingId() != null) {
                    accountNumber = compteBancaireRepository.findByProprietaireTrackingId(entity.getBoutique().getMerchant().getTrackingId())
                            .map(cb -> cb.getAccountNumber())
                            .orElse("Non renseigné");
                }
            }
        }

        return new LiquidationResponse(
            entity.getTrackingId(),
            boutiqueTrackingId,
            boutiqueName,
            merchantName,
            accountNumber,
            entity.getAmountToLiquidate(),
            entity.getCreatedAt(),
            entity.getValidatedAt(),
            entity.getStatus()
        );
    }
}