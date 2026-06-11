package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.application.dtos.requests.TransactionRequest;
import com.backend.gns.commerce.application.dtos.responses.TransactionResponse;
import com.backend.gns.commerce.application.mappers.TransactionMapper;
import com.backend.gns.commerce.domain.enums.TransactionStatut;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.domain.models.Transaction;
import com.backend.gns.commerce.domain.services.BoutiqueService;
import com.backend.gns.commerce.domain.services.TransactionService;
import com.backend.gns.commerce.infrastructure.repositories.TransactionRepository;
import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final StudentService studentService;
    private final BoutiqueService boutiqueService;
    private final ParametreGnsService parametreGnsService;

    @Override
    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        // En vrai projet, on récupère les objets complets via les services pour les mettre dans le modèle
        // Ici on simule l'enrichissement (à adapter selon les méthodes de tes services existants)
        
        // Exemple hypothétique:
        // Student student = studentService.findEntityByTrackingId(request.studentId());
        // Boutique boutique = boutiqueService.findEntityByTrackingId(request.boutiqueId());
        
        // Pour l'instant, création basique avec les UUIDs que nous avons dans le modèle
        BigDecimal taux = parametreGnsService.getValeurAsBigDecimal(TypeParametreGns.TAUX_COMMISSION_PAIEMENT);
        BigDecimal commission = request.montantDebite().multiply(taux);
        
        Transaction transaction = Transaction.builder()
                .trackingId(UUID.randomUUID())
                // .student(student) // À implémenter quand les services retournent l'entité
                // .boutique(boutique) // À implémenter quand les services retournent l'entité
                .montantDebite(request.montantDebite())
                .montantNetBoutique(request.montantDebite().subtract(commission))
                .commissionTotale(commission)
                .date(LocalDateTime.now())
                .statut(TransactionStatut.VALIDE)
                .build();
        
        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    public Optional<TransactionResponse> findByTrackingId(UUID trackingId) {
        return transactionRepository.findByTrackingId(trackingId).map(transactionMapper::toResponse);
    }

    @Override
    public Page<TransactionResponse> findAll(Pageable pageable) {
        return transactionRepository.findAll(pageable).map(transactionMapper::toResponse);
    }

    @Override
    public Page<TransactionResponse> findByBoutiqueId(UUID boutiqueId, Pageable pageable) {
        // Nécessite une méthode dans le repository
        return null;
    }
}
