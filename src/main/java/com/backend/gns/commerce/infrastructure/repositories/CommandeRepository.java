package com.backend.gns.commerce.infrastructure.repositories;

import com.backend.gns.commerce.domain.enums.CommandeStatut;
import com.backend.gns.commerce.domain.models.Commande;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

  Optional<Commande> findByTrackingId(UUID trackingId);

  Page<Commande> findByStatut(CommandeStatut statut, Pageable pageable);

  @Query(
      "SELECT c FROM Commande c WHERE c.student.trackingId = :trackingId ORDER BY c.dateCommande DESC")
  Page<Commande> findByStudentTrackingId(@Param("trackingId") UUID trackingId, Pageable pageable);

  @Query(
      "SELECT c FROM Commande c WHERE c.boutique.trackingId = :boutiqueTrackingId ORDER BY c.dateCommande DESC")
  Page<Commande> findByBoutiqueTrackingId(
      @Param("boutiqueTrackingId") UUID boutiqueTrackingId, Pageable pageable);

  @Query(
      "SELECT c FROM Commande c WHERE c.boutique.merchant.trackingId = :merchantTrackingId ORDER BY c.dateCommande DESC")
  Page<Commande> findByMerchantTrackingId(
      @Param("merchantTrackingId") UUID merchantTrackingId, Pageable pageable);
}
