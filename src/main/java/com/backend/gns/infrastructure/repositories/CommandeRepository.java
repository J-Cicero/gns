package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    @Query("SELECT c FROM Commande c WHERE c.reference = :ref")
    Optional<Commande> findByReference(@Param("ref") String ref);

    @Query("SELECT c FROM Commande c WHERE c.student.id = :studentId")
    List<Commande> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT c FROM Commande c WHERE c.merchant.id = :merchantId")
    List<Commande> findByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT c FROM Commande c WHERE c.trackingId = :trackingId")
    Optional<Commande> findByTrackingId(@Param("trackingId") UUID trackingId);
}
