package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.application.dtos.requests.TransactionRequest;
import com.backend.gns.commerce.application.dtos.responses.TransactionResponse;
import com.backend.gns.commerce.application.mappers.TransactionMapper;
import com.backend.gns.commerce.domain.enums.TransactionStatut;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.domain.models.Transaction;
import com.backend.gns.commerce.domain.services.TransactionService;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.commerce.infrastructure.repositories.TransactionRepository;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.domain.services.WalletService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.backend.gns.student.domain.services.ScolariteYearService;
import com.backend.gns.student.application.dtos.responses.ScolariteYearResponse;
import com.backend.gns.core.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final StudentRepository studentRepository;
    private final BoutiqueRepository boutiqueRepository;
    private final WalletService walletService;
    private final ParametreGnsService parametreService;
    private final ScolariteYearService scolariteYearService;

    @Override
    public com.backend.gns.commerce.application.dtos.responses.TransactionStatsResponse getGlobalStats() {
        Optional<ScolariteYearResponse> activeYear = scolariteYearService.findActiveYear();
        
        java.util.List<Transaction> transactions;
        if (activeYear.isPresent()) {
            ScolariteYearResponse year = activeYear.get();
            transactions = transactionRepository.findByCreatedAtBetween(
                year.startDate().atStartOfDay(), 
                year.endDate().atTime(23, 59, 59));
        } else {
            transactions = transactionRepository.findAll();
        }

        BigDecimal volume = transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatut.VALIDE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal commission = transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatut.VALIDE)
                .map(Transaction::getTotalCommission)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal gnsCommission = transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatut.VALIDE)
                .map(Transaction::getGnsCommission)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal bankCommission = transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatut.VALIDE)
                .map(Transaction::getBankCommission)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long count = transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatut.VALIDE)
                .count();
        return new com.backend.gns.commerce.application.dtos.responses.TransactionStatsResponse(volume, commission, gnsCommission, bankCommission, count);
    }

    @Override
    @Transactional
    public TransactionResponse createPayment(TransactionRequest request) {
        log.info("Processing direct payment from sender {} to receiver {} with amount {}",
                request.senderTrackingId(), request.receiverTrackingId(), request.amount());

        // 1. Validate sender and receiver existence using repositories
        Student sender = studentRepository.findByTrackingId(request.senderTrackingId())
                .orElseThrow(() -> new EntityNotFoundException("Sender not found with ID: " + request.senderTrackingId()));
        Boutique receiver = boutiqueRepository.findByTrackingId(request.receiverTrackingId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found with ID: " + request.receiverTrackingId()));

        // 2. Fetch wallets
        Wallet senderWallet = sender.getWallet();
        Wallet receiverWallet = receiver.getWallet();

        // 3. Validate wallet existence and status
        if (senderWallet == null || receiverWallet == null) {
            throw new IllegalStateException("Both sender and receiver must have a wallet.");
        }
        if (senderWallet.getStatus() != WalletStatus.ACTIF || receiverWallet.getStatus() != WalletStatus.ACTIF) {
            throw new IllegalStateException("Both wallets must be active.");
        }

        // 4. Calculate Commissions
        BigDecimal amount = request.amount();
        BigDecimal commissionRate = new BigDecimal(parametreService.getValeur(TypeParametreGns.TAUX_COMMISSION_PAIEMENT));
        BigDecimal gnsShareRate = new BigDecimal(parametreService.getValeur(TypeParametreGns.PART_COMMISSION_GNS));

        BigDecimal totalCommission = amount.multiply(commissionRate);
        BigDecimal amountDebited = amount.add(totalCommission);
        BigDecimal amountCredited = amount;
        
        BigDecimal gnsCommission = totalCommission.multiply(gnsShareRate);
        BigDecimal bankCommission = totalCommission.subtract(gnsCommission);

        // 5. Pre-check: Sufficient funds for the debited amount
        if (!walletService.hasSufficientBalance(senderWallet.getTrackingId(), amountDebited)) {
            throw new RuntimeException("Sender has insufficient funds for amount + commission.");
        }

        // 6. Perform transaction - Atomic operation
        walletService.debit(senderWallet.getTrackingId(), amountDebited);
        walletService.credit(receiverWallet.getTrackingId(), amountCredited);

        // 7. Save the Transaction record
        Transaction transaction = Transaction.builder()
                .trackingId(UUID.randomUUID())
                .sender(sender)
                .receiver(receiver)
                .amount(amount)
                .amountDebited(amountDebited)
                .amountCredited(amountCredited)
                .totalCommission(totalCommission)
                .gnsCommission(gnsCommission)
                .bankCommission(bankCommission)
                .isCommissionPaid(false)
                .status(TransactionStatut.VALIDE)
                .createdAt(LocalDateTime.now())
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
        throw new UnsupportedOperationException("findByBoutiqueId is no longer directly supported.");
    }

    @Override
    public Page<TransactionResponse> findByStudentId(UUID studentId, Pageable pageable) {
        throw new UnsupportedOperationException("findByStudentId is no longer directly supported.");
    }
}
