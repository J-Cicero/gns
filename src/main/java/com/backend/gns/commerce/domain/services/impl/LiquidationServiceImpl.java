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
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.backend.gns.core.notification.domain.services.NotificationService;

@Service
@RequiredArgsConstructor
public class LiquidationServiceImpl implements LiquidationService {

    private final LiquidationRepository liquidationRepository;
    private final LiquidationMapper liquidationMapper;
    private final BoutiqueRepository boutiqueRepository;
    private final TransactionRepository transactionRepository;
    private final ScolariteYearRepository scolariteYearRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public LiquidationResponse create(LiquidationRequest request) {
        if (request.boutiqueTrackingId() == null) {
            throw new RuntimeException("Le trackingId de la boutique est requis");
        }

        Boutique boutique = boutiqueRepository.findByTrackingId(request.boutiqueTrackingId())
                .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

        if (boutique.getWallet() == null || boutique.getWallet().getStatus() != com.backend.gns.wallet.domain.enums.WalletStatus.ACTIF) {
            throw new IllegalStateException("Impossible de liquider : le portefeuille de la boutique n'est pas actif.");
        }

        List<Transaction> pendingTransactions = transactionRepository
                .findByReceiverTrackingIdAndStatusAndLiquidationIsNull(request.boutiqueTrackingId(),
                        TransactionStatut.VALIDE);

        if (pendingTransactions.isEmpty()) {
            throw new RuntimeException("Aucune transaction en attente de liquidation pour cette boutique.");
        }

        BigDecimal sumAvailable = pendingTransactions.stream()
                .map(Transaction::getAmountCredited) // La boutique reçoit l'AmountCredited
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumAvailable.compareTo(request.amountToLiquidate()) < 0) {
            throw new RuntimeException("Le montant à liquider demandé (" + request.amountToLiquidate()
                    + ") dépasse le solde disponible (" + sumAvailable + ").");
        }

        ScolariteYear activeYear = scolariteYearRepository.findByIsOpenTrue()
                .orElseThrow(() -> new RuntimeException("Aucune année scolaire active n'est ouverte."));

        Liquidation liquidation = Liquidation.builder()
                .trackingId(UUID.randomUUID())
                .amountToLiquidate(request.amountToLiquidate()) // On garde le montant demandé par le marchand
                .createdAt(LocalDateTime.now())
                .status(LiquidationStatut.EN_ATTENTE)
                .scolariteYear(activeYear)
                .build();

        Liquidation savedLiquidation = liquidationRepository.save(liquidation);

        for (Transaction t : pendingTransactions) {
            t.setLiquidation(savedLiquidation);
        }
        transactionRepository.saveAll(pendingTransactions);

        notificationService.createNotification(
            "Nouvelle Liquidation Marchand",
            "Demande de liquidation marchand pour la boutique \"" + boutique.getName() + "\" d'un montant de " + savedLiquidation.getAmountToLiquidate() + " FCFA.",
            "ADMIN_BANQUE",
            "LIQUIDATION_MERCHANT"
        );

        return liquidationMapper.toResponse(savedLiquidation);
    }

    @Override
    public Optional<LiquidationResponse> findByTrackingId(UUID trackingId) {
        return liquidationRepository.findByTrackingId(trackingId).map(liquidationMapper::toResponse);
    }

    @Override
    public List<LiquidationResponse> findByBoutiqueId(UUID boutiqueId) {
        return liquidationRepository.findByBoutiqueTrackingId(boutiqueId).stream()
                .map(liquidationMapper::toResponse)
                .toList();
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
        Optional<Liquidation> liquidationOpt = liquidationRepository.findByTrackingId(trackingId);
        
        Liquidation liquidation;
        if (liquidationOpt.isPresent()) {
            liquidation = liquidationOpt.get();
        } else {
            Optional<Boutique> boutiqueOpt = boutiqueRepository.findByTrackingId(trackingId);
            if (boutiqueOpt.isPresent()) {
                Boutique boutique = boutiqueOpt.get();
                List<Liquidation> liquidations = liquidationRepository.findByBoutiqueTrackingId(boutique.getTrackingId());
                Optional<Liquidation> pendingLiquidation = liquidations.stream()
                        .filter(l -> l.getStatus() == LiquidationStatut.EN_ATTENTE)
                        .findFirst();
                
                if (pendingLiquidation.isPresent()) {
                    liquidation = pendingLiquidation.get();
                } else {
                    List<Transaction> pendingTransactions = transactionRepository
                            .findByReceiverTrackingIdAndStatusAndLiquidationIsNull(boutique.getTrackingId(),
                                    TransactionStatut.VALIDE);
                    
                    if (pendingTransactions.isEmpty()) {
                        throw new RuntimeException("Aucune transaction à liquider pour cette boutique.");
                    }
                    
                    BigDecimal sumAvailable = pendingTransactions.stream()
                            .map(Transaction::getAmountCredited)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                            
                    ScolariteYear activeYear = scolariteYearRepository.findByIsOpenTrue()
                            .orElseThrow(() -> new RuntimeException("Aucune année scolaire active n'est ouverte."));
                    
                    liquidation = Liquidation.builder()
                            .trackingId(UUID.randomUUID())
                            .scolariteYear(activeYear)
                            .amountToLiquidate(sumAvailable)
                            .createdAt(LocalDateTime.now())
                            .status(LiquidationStatut.EN_ATTENTE)
                            .build();
                            
                    liquidation = liquidationRepository.save(liquidation);
                    
                    for (Transaction t : pendingTransactions) {
                        t.setLiquidation(liquidation);
                    }
                    transactionRepository.saveAll(pendingTransactions);
                }
            } else {
                throw new RuntimeException("Liquidation ou Boutique non trouvée avec le trackingId: " + trackingId);
            }
        }

        liquidation.setStatus(LiquidationStatut.PAYE);
        liquidation.setValidatedAt(LocalDateTime.now());

        Liquidation savedLiquidation = liquidationRepository.save(liquidation);

        // Passer à retrievedByBoutique = true pour toutes les transactions liées à cette liquidation
        List<Transaction> transactions = transactionRepository.findByLiquidation_TrackingId(savedLiquidation.getTrackingId());
        for (Transaction t : transactions) {
            t.setRetrievedByBoutique(true);
        }
        transactionRepository.saveAll(transactions);

        notificationService.createNotification(
            "Liquidation Marchand Validée",
            "La liquidation marchand d'un montant de " + savedLiquidation.getAmountToLiquidate() + " FCFA a été validée par la Banque.",
            "ADMIN_GNS",
            "LIQUIDATION_MERCHANT"
        );

        return liquidationMapper.toResponse(savedLiquidation);
    }

    @Override
    public List<LiquidationResponse> findAll() {
        return liquidationRepository.findAll().stream()
                .map(liquidationMapper::toResponse)
                .toList();
    }

    @Override
    public List<LiquidationResponse> findByScolariteYear(UUID scolariteYearTrackingId) {
        return liquidationRepository.findByScolariteYear_TrackingId(scolariteYearTrackingId).stream()
                .map(liquidationMapper::toResponse)
                .toList();
    }
}
