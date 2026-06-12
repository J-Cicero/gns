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
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.domain.services.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final StudentRepository studentRepository;
    private final BoutiqueRepository boutiqueRepository;
    private final ParametreGnsService parametreGnsService;
    private final WalletService walletService;

    @Override
    public java.math.BigDecimal getVolumeValide() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getStatut() == com.backend.gns.commerce.domain.enums.TransactionStatut.VALIDE)
                .map(Transaction::getMontantDebite)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    @Override
    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        log.info("Traitement de la transaction pour étudiant {} et boutique {}", request.studentId(), request.boutiqueId());

        // 1. Récupération des entités
        Student student = studentRepository.findByTrackingId(request.studentId())
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé"));
        Boutique boutique = boutiqueRepository.findByTrackingId(request.boutiqueId())
                .orElseThrow(() -> new EntityNotFoundException("Boutique non trouvée"));
        
        Wallet studentWallet = student.getWallet();
        Wallet boutiqueWallet = boutique.getWallet();

        // 2. Double débit sécurisé
        walletService.debiter(studentWallet.getTrackingId(), request.montantDebite()); // Paiement
        walletService.debiter(boutiqueWallet.getTrackingId(), request.montantDebite()); // Débit Quota (limite vente)

        // 3. Calculs et Commission
        BigDecimal taux = parametreGnsService.getValeurAsBigDecimal(TypeParametreGns.TAUX_COMMISSION_PAIEMENT);
        BigDecimal commissionTotale = request.montantDebite().multiply(taux);
        
        // Split 75% GNS / 25% Banque (à rendre configurable plus tard)
        BigDecimal commissionGns = commissionTotale.multiply(new BigDecimal("0.75"));
        BigDecimal commissionBanque = commissionTotale.multiply(new BigDecimal("0.25"));
        
        BigDecimal montantNetBoutique = request.montantDebite().subtract(commissionTotale);

        // 4. Crédit Net Boutique
        walletService.crediter(boutiqueWallet.getTrackingId(), montantNetBoutique);

        // 5. Enregistrement
        Transaction transaction = Transaction.builder()
                .trackingId(UUID.randomUUID())
                .student(student)
                .boutique(boutique)
                .montantDebite(request.montantDebite())
                .montantNetBoutique(montantNetBoutique)
                .commissionTotale(commissionTotale)
                .commissionGns(commissionGns)
                .commissionBanque(commissionBanque)
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
