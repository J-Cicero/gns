package com.backend.gns.commerce.infrastructure.repositories;

import com.backend.gns.commerce.domain.enums.TransactionStatut;
import com.backend.gns.commerce.domain.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.sender LEFT JOIN FETCH t.receiver WHERE t.trackingId = :trackingId")
    Optional<Transaction> findByTrackingId(UUID trackingId);

    @Query(value = "SELECT t FROM Transaction t LEFT JOIN FETCH t.sender LEFT JOIN FETCH t.receiver WHERE t.sender.trackingId = :senderTrackingId",
           countQuery = "SELECT count(t) FROM Transaction t WHERE t.sender.trackingId = :senderTrackingId")
    Page<Transaction> findBySenderTrackingId(UUID senderTrackingId, Pageable pageable);

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.sender LEFT JOIN FETCH t.receiver WHERE t.sender.trackingId = :senderTrackingId")
    List<Transaction> findBySenderTrackingId(UUID senderTrackingId);

    @Query(value = "SELECT t FROM Transaction t LEFT JOIN FETCH t.sender LEFT JOIN FETCH t.receiver WHERE t.receiver.trackingId = :receiverTrackingId",
           countQuery = "SELECT count(t) FROM Transaction t WHERE t.receiver.trackingId = :receiverTrackingId")
    Page<Transaction> findByReceiverTrackingId(UUID receiverTrackingId, Pageable pageable);

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.sender LEFT JOIN FETCH t.receiver WHERE t.createdAt BETWEEN :start AND :end")
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.sender LEFT JOIN FETCH t.receiver WHERE t.receiver.trackingId = :receiverTrackingId AND t.status = :status AND t.liquidation IS NULL")
    List<Transaction> findByReceiverTrackingIdAndStatusAndLiquidationIsNull(UUID receiverTrackingId, TransactionStatut status);

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.sender LEFT JOIN FETCH t.receiver WHERE t.sender.trackingId = :senderTrackingId AND t.status = :status AND t.studentLiquidation IS NULL")
    List<Transaction> findBySenderTrackingIdAndStatusAndStudentLiquidationIsNull(UUID senderTrackingId, TransactionStatut status);

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.sender LEFT JOIN FETCH t.receiver WHERE t.liquidation.trackingId = :liquidationTrackingId")
    List<Transaction> findByLiquidation_TrackingId(UUID liquidationTrackingId);

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.sender LEFT JOIN FETCH t.receiver WHERE t.studentLiquidation.trackingId = :studentLiquidationTrackingId")
    List<Transaction> findByStudentLiquidation_TrackingId(UUID studentLiquidationTrackingId);

    @Query("SELECT COALESCE(SUM(t.amountCredited), 0) FROM Transaction t WHERE t.status = 'VALIDE' AND t.retrievedByBoutique = false")
    java.math.BigDecimal sumNetCommercants();

    @Query("SELECT COALESCE(SUM(t.bankCommission), 0) FROM Transaction t WHERE t.status = 'VALIDE'")
    java.math.BigDecimal sumBankCommissions();

    @Query("SELECT COALESCE(SUM(t.gnsCommission), 0) FROM Transaction t WHERE t.status = 'VALIDE'")
    java.math.BigDecimal sumGnsCommissions();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.status = 'VALIDE'")
    java.math.BigDecimal sumTotalDepenses();
}
