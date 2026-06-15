package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.application.dtos.requests.LiquidationRequest;
import com.backend.gns.commerce.application.dtos.responses.LiquidationResponse;
import com.backend.gns.commerce.application.mappers.LiquidationMapper;
import com.backend.gns.commerce.domain.enums.LiquidationStatut;
import com.backend.gns.commerce.domain.models.Liquidation;
import com.backend.gns.commerce.domain.services.LiquidationService;
import com.backend.gns.commerce.infrastructure.repositories.LiquidationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LiquidationServiceImpl implements LiquidationService {

    private final LiquidationRepository liquidationRepository;
    private final LiquidationMapper liquidationMapper;

    @Override
    @Transactional
    public LiquidationResponse create(LiquidationRequest request) {
        Liquidation liquidation = Liquidation.builder()
                .trackingId(UUID.randomUUID())
                // .boutique(boutique) // À enrichir
                .amountToLiquidate(request.amountToLiquidate())
                .createdAt(LocalDateTime.now())
                .status(LiquidationStatut.EN_ATTENTE)
                .build();
        return liquidationMapper.toResponse(liquidationRepository.save(liquidation));
    }

    @Override
    public Optional<LiquidationResponse> findByTrackingId(UUID trackingId) {
        return liquidationRepository.findByTrackingId(trackingId).map(liquidationMapper::toResponse);
    }

    @Override
    public List<LiquidationResponse> findByBoutiqueId(UUID boutiqueId) {
        // Nécessite une méthode dans le repository
        return List.of();
    }

    @Override
    public java.math.BigDecimal getPendingTotal() {
        return liquidationRepository.findAll().stream()
                .filter(l -> l.getStatus() == LiquidationStatut.EN_ATTENTE)
                .map(Liquidation::getAmountToLiquidate)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    @Override
    @Transactional
    public LiquidationResponse validerLiquidation(UUID trackingId, String referenceVirement) {
        Liquidation liquidation = liquidationRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Liquidation non trouvée"));
        
        liquidation.setStatus(LiquidationStatut.PAYE);
        liquidation.setValidatedAt(LocalDateTime.now());
        liquidation.setTransferReference(referenceVirement);
        
        return liquidationMapper.toResponse(liquidationRepository.save(liquidation));
    }
}
