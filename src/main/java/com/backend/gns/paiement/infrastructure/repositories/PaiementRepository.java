package com.backend.gns.paiement.infrastructure.repositories;

import com.backend.gns.paiement.domain.enums.PaiementStatut;
import com.backend.gns.paiement.domain.enums.PaiementType;
import com.backend.gns.paiement.domain.models.Paiement;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

  Optional<Paiement> findByTrackingId(UUID trackingId);

  Page<Paiement> findByStatutPaiementOrderByDateDesc(
      PaiementStatut statutPaiement, Pageable pageable);

  Page<Paiement> findByTypePaiementOrderByDateDesc(PaiementType typePaiement, Pageable pageable);

  java.util.List<Paiement> findByDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

  @Query("SELECT p FROM Paiement p WHERE p.wallet.trackingId = :trackingId ORDER BY p.date DESC")
  Page<Paiement> findByWalletTrackingId(@Param("trackingId") UUID trackingId, Pageable pageable);

  @Query("SELECT p FROM Paiement p WHERE p.commande.trackingId = :trackingId ORDER BY p.date DESC")
  Page<Paiement> findByCommandeTrackingId(@Param("trackingId") UUID trackingId, Pageable pageable);

  @Query("SELECT p FROM Paiement p WHERE p.commande.student.universite.trackingId = :universiteTrackingId ORDER BY p.date DESC")
  Page<Paiement> findByUniversiteTrackingId(@Param("universiteTrackingId") UUID universiteTrackingId, Pageable pageable);

  Long countByStatutPaiement(PaiementStatut statutPaiement);

  @Query("SELECT SUM(p.montantDebite) FROM Paiement p WHERE p.statutPaiement = :statut")
  BigDecimal sumMontantDebiteByStatut(@Param("statut") PaiementStatut statut);

  @Query("SELECT SUM(p.montantDebite) FROM Paiement p WHERE p.date >= :start AND p.date <= :end AND p.statutPaiement = :statut")
  BigDecimal sumMontantDebiteBetweenDatesAndStatut(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end, @Param("statut") PaiementStatut statut);

  @Query("SELECT SUM(p.commission) FROM Paiement p WHERE p.statutPaiement = :statut")
  BigDecimal sumCommissionByStatut(@Param("statut") PaiementStatut statut);
}
