package com.backend.gns.commerce.infrastructure.repositories;

import com.backend.gns.commerce.domain.enums.TransactionStatut;
import com.backend.gns.commerce.domain.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTrackingId(UUID trackingId);
    Page<Transaction> findBySenderTrackingId(UUID senderTrackingId, Pageable pageable);
    List<Transaction> findBySenderTrackingId(UUID senderTrackingId);
    Page<Transaction> findByReceiverTrackingId(UUID receiverTrackingId, Pageable pageable);
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Transaction> findByReceiverTrackingIdAndIsCommissionPaid(UUID trackingId, Boolean isCommissionPaid);
    List<Transaction> findBySenderTrackingIdAndIsRetry (UUID senderTrackingId , Boolean isRetry);
    
    List<Transaction> findByReceiverTrackingIdAndStatusAndLiquidationIsNull(UUID receiverTrackingId, TransactionStatut status);
    
    List<Transaction> findBySenderTrackingIdAndStatusAndStudentLiquidationIsNull(UUID senderTrackingId, TransactionStatut status);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(t.amountCredited), 0) FROM Transaction t WHERE t.status = 'VALIDE' AND t.retrievedByBoutique = false")
    java.math.BigDecimal sumNetCommercants();

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(t.bankCommission), 0) FROM Transaction t WHERE t.status = 'VALIDE'")
    java.math.BigDecimal sumBankCommissions();

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(t.gnsCommission), 0) FROM Transaction t WHERE t.status = 'VALIDE'")
    java.math.BigDecimal sumGnsCommissions();

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.status = 'VALIDE'")
    java.math.BigDecimal sumTotalDepenses();
}
