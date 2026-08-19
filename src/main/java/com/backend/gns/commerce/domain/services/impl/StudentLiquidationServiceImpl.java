package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.application.dtos.requests.StudentLiquidationRequest;
import com.backend.gns.commerce.application.dtos.responses.StudentLiquidationResponse;
import com.backend.gns.commerce.application.mappers.StudentLiquidationMapper;
import com.backend.gns.commerce.domain.enums.LiquidationStatut;
import com.backend.gns.commerce.domain.enums.TransactionStatut;
import com.backend.gns.commerce.domain.models.StudentLiquidation;
import com.backend.gns.commerce.domain.models.Transaction;
import com.backend.gns.commerce.domain.services.StudentLiquidationService;
import com.backend.gns.commerce.infrastructure.repositories.StudentLiquidationRepository;
import com.backend.gns.commerce.infrastructure.repositories.TransactionRepository;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.backend.gns.core.notification.domain.services.NotificationService;

@Service
@RequiredArgsConstructor
public class StudentLiquidationServiceImpl implements StudentLiquidationService {

    private final StudentLiquidationRepository studentLiquidationRepository;
    private final StudentRepository studentRepository;
    private final ScolariteYearRepository scolariteYearRepository;
    private final TransactionRepository transactionRepository;
    private final StudentLiquidationMapper mapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public StudentLiquidationResponse create(StudentLiquidationRequest request) {
        if(request.studentTrackingId() == null) {
            throw new RuntimeException("Le trackingId de l'étudiant est requis");
        }

        Student student = studentRepository.findByTrackingId(request.studentTrackingId())
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        if (student.getWallet() == null || student.getWallet().getStatus() != com.backend.gns.wallet.domain.enums.WalletStatus.ACTIF) {
            throw new IllegalStateException("Impossible de prélever : le portefeuille de l'étudiant n'est pas actif.");
        }

        ScolariteYear scolariteYear;
        if (request.scolariteYearTrackingId() != null) {
            scolariteYear = scolariteYearRepository.findByTrackingId(request.scolariteYearTrackingId())
                    .orElseThrow(() -> new RuntimeException("Année scolaire non trouvée"));
        } else {
            scolariteYear = scolariteYearRepository.findByIsOpenTrue()
                    .orElseThrow(() -> new RuntimeException("Aucune année scolaire active n'est ouverte."));
        }

        List<Transaction> pendingTransactions = transactionRepository
                .findBySenderTrackingIdAndStatusAndStudentLiquidationIsNull(request.studentTrackingId(), TransactionStatut.VALIDE);

        if (pendingTransactions.isEmpty()) {
            throw new RuntimeException("Aucune transaction en attente de prélèvement pour cet étudiant.");
        }

        BigDecimal sumAvailable = pendingTransactions.stream()
                .map(Transaction::getAmountDebited) 
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if(sumAvailable.compareTo(request.amountToDeduct()) < 0) {
            throw new RuntimeException("Le montant à prélever (" + request.amountToDeduct() + ") dépasse le montant des transactions (" + sumAvailable + ").");
        }

        StudentLiquidation liquidation = StudentLiquidation.builder()
                .trackingId(UUID.randomUUID())
                .scolariteYear(scolariteYear)
                .student(student)
                .amountDeducted(request.amountToDeduct())
                .createdAt(LocalDateTime.now())
                .status(LiquidationStatut.EN_ATTENTE)
                .build();
        
        StudentLiquidation savedLiquidation = studentLiquidationRepository.save(liquidation);

        for (Transaction t : pendingTransactions) {
            t.setStudentLiquidation(savedLiquidation);
            t.setDeductedFromStudentBourse(true);
        }
        transactionRepository.saveAll(pendingTransactions);

        notificationService.createNotification(
            "Nouvelle Liquidation Étudiant",
            "L'Admin GNS a créé une liquidation étudiant pour " + student.getFirstName() + " " + student.getLastName() + " (" + savedLiquidation.getAmountDeducted() + " FCFA).",
            "ADMIN_BANQUE",
            "LIQUIDATION_STUDENT"
        );

        return mapper.toResponse(savedLiquidation);
    }

    @Override
    public Optional<StudentLiquidationResponse> findByTrackingId(UUID trackingId) {
        return studentLiquidationRepository.findByTrackingId(trackingId).map(mapper::toResponse);
    }

    @Override
    public List<StudentLiquidationResponse> findByStudentId(UUID studentId) {
        return List.of();
    }

    @Override
    public List<StudentLiquidationResponse> findAll() {
        return studentLiquidationRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<StudentLiquidationResponse> findPending() {
        return studentLiquidationRepository.findAll().stream()
                .filter(l -> l.getStatus() == LiquidationStatut.EN_ATTENTE)
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public BigDecimal getPendingTotal() {
        return studentLiquidationRepository.findAll().stream()
                .filter(l -> l.getStatus() == LiquidationStatut.EN_ATTENTE)
                .map(StudentLiquidation::getAmountDeducted)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public StudentLiquidationResponse validerLiquidation(UUID trackingId, String referenceVirement) {
        StudentLiquidation liquidation = studentLiquidationRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Prélèvement non trouvé"));
        
        liquidation.setStatus(LiquidationStatut.PAYE);
        liquidation.setValidatedAt(LocalDateTime.now());
        
        StudentLiquidation savedLiquidation = studentLiquidationRepository.save(liquidation);

        List<Transaction> transactions = transactionRepository.findByStudentLiquidation_TrackingId(savedLiquidation.getTrackingId());
        for (Transaction t : transactions) {
            t.setDeductedFromStudentBourse(true);
        }
        transactionRepository.saveAll(transactions);

        notificationService.createNotification(
            "Liquidation Étudiant Validée",
            "La liquidation étudiant d'un montant de " + savedLiquidation.getAmountDeducted() + " FCFA a été validée par la Banque.",
            "ADMIN_GNS",
            "LIQUIDATION_STUDENT"
        );

        return mapper.toResponse(savedLiquidation);
    }

    @Override
    public List<StudentLiquidationResponse> findByScolariteYear(UUID scolariteYearTrackingId) {
        return studentLiquidationRepository.findByScolariteYear_TrackingId(scolariteYearTrackingId).stream()
                .map(mapper::toResponse)
                .toList();
    }
}
