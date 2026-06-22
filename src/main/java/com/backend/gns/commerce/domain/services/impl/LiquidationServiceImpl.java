package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.application.dtos.requests.LiquidationRequest;
import com.backend.gns.commerce.application.dtos.responses.LiquidationResponse;
import com.backend.gns.commerce.application.mappers.LiquidationMapper;
import com.backend.gns.commerce.domain.enums.LiquidationStatut;
import com.backend.gns.commerce.domain.enums.TransactionStatut;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.domain.models.Liquidation;
import com.backend.gns.commerce.domain.models.Transaction;
import com.backend.gns.commerce.domain.services.LiquidationService;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.commerce.infrastructure.repositories.LiquidationRepository;
import com.backend.gns.commerce.infrastructure.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiquidationServiceImpl implements LiquidationService {

    private final LiquidationRepository liquidationRepository;
    private final LiquidationMapper liquidationMapper;
    private final BoutiqueRepository boutiqueRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public LiquidationResponse create(LiquidationRequest request) {
        if(request.boutiqueTrackingId() == null) {
             throw new RuntimeException("Le trackingId de la boutique est requis");
        }

        Boutique boutique = boutiqueRepository.findByTrackingId(request.boutiqueTrackingId())
                .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

        // 1. Récupération des transactions VALIDES et NON LIQUIDÉES pour cette boutique
        List<Transaction> pendingTransactions = transactionRepository
                .findByReceiverTrackingIdAndStatusAndLiquidationIsNull(request.boutiqueTrackingId(), TransactionStatut.VALIDE);

        if (pendingTransactions.isEmpty()) {
            throw new RuntimeException("Aucune transaction en attente de liquidation pour cette boutique.");
        }

        // 2. Calcul du montant total disponible à liquider
        BigDecimal sumAvailable = pendingTransactions.stream()
                .map(Transaction::getAmountCredited) // La boutique reçoit l'AmountCredited
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if(sumAvailable.compareTo(request.amountToLiquidate()) < 0) {
            throw new RuntimeException("Le montant à liquider demandé (" + request.amountToLiquidate() + ") dépasse le solde disponible (" + sumAvailable + ").");
        }

        // 3. Création de l'entité Liquidation
        Liquidation liquidation = Liquidation.builder()
                .trackingId(UUID.randomUUID())
                .boutique(boutique)
                .amountToLiquidate(request.amountToLiquidate()) // On garde le montant demandé par le marchand (peut être inférieur au max disponible)
                .createdAt(LocalDateTime.now())
                .status(LiquidationStatut.EN_ATTENTE)
                .build();
        
        Liquidation savedLiquidation = liquidationRepository.save(liquidation);

        // 4. Mise à jour de toutes les transactions en attente pour les lier à cette liquidation
        for (Transaction t : pendingTransactions) {
            t.setLiquidation(savedLiquidation);
            t.setRetrievedByBoutique(true);
        }
        transactionRepository.saveAll(pendingTransactions);

        return liquidationMapper.toResponse(savedLiquidation);
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
