package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.enums.CommandeStatut;
import com.backend.gns.domain.models.Commande;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

  Optional<Commande> findByTrackingId(UUID trackingId);

  Page<Commande> findByStatutOrderByDateCommandeDesc(CommandeStatut statut, Pageable pageable);

  @Query(
      "SELECT c FROM Commande c WHERE c.student.trackingId = :trackingId ORDER BY c.dateCommande DESC")
  Page<Commande> findByStudentTrackingId(@Param("trackingId") UUID trackingId, Pageable pageable);

  @Query(
      "SELECT c FROM Commande c WHERE c.merchant.trackingId = :trackingId ORDER BY c.dateCommande DESC")
  Page<Commande> findByMerchantTrackingId(@Param("trackingId") UUID trackingId, Pageable pageable);
}
