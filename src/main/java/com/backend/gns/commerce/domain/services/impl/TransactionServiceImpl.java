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

    @Override
    public BigDecimal getVolumeValide() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getStatus() == TransactionStatut.VALIDE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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

        // 4. Pre-check: Sufficient funds (Check-then-Act)
        if (!walletService.hasSufficientBalance(senderWallet.getTrackingId(), request.amount())) {
            throw new RuntimeException("Sender has insufficient funds.");
        }
        if (!walletService.hasSufficientBalance(receiverWallet.getTrackingId(), request.amount())) {
            throw new RuntimeException("Receiver has insufficient funds.");
        }

        // 5. Perform double debit - Atomic operation
        walletService.debit(senderWallet.getTrackingId(), request.amount());
        walletService.debit(receiverWallet.getTrackingId(), request.amount());

        // 6. Save the Transaction record
        Transaction transaction = Transaction.builder()
                .trackingId(UUID.randomUUID())
                .sender(sender)
                .receiver(receiver)
                .amount(request.amount())
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
