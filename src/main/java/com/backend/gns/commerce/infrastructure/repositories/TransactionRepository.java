package com.backend.gns.commerce.infrastructure.repositories;

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
    Page<Transaction> findByReceiverTrackingId(UUID receiverTrackingId, Pageable pageable);
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
