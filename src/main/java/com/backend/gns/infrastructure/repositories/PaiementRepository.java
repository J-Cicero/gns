package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Paiement;
import com.backend.gns.domain.enums.PaiementStatut;
import com.backend.gns.domain.enums.PaiementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    @Query("SELECT p FROM Paiement p WHERE p.trackingId = :trackingId")
    Optional<Paiement> findByTrackingId(@Param("trackingId") UUID trackingId);

    @Query("SELECT p FROM Paiement p WHERE p.statutPaiement = :statut ORDER BY p.date DESC")
    List<Paiement> findByStatutPaiement(@Param("statut") PaiementStatut statut);

    @Query("SELECT p FROM Paiement p WHERE p.typePaiement = :type ORDER BY p.date DESC")
    List<Paiement> findByTypePaiement(@Param("type") PaiementType type);

    @Query("SELECT p FROM Paiement p WHERE p.wallet.trackingId = :trackingId ORDER BY p.date DESC")
    List<Paiement> findByWalletTrackingId(@Param("trackingId") UUID trackingId);

    @Query("SELECT p FROM Paiement p WHERE p.commande.trackingId = :trackingId ORDER BY p.date DESC")
    List<Paiement> findByCommandeTrackingId(@Param("trackingId") UUID trackingId);

    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.statutPaiement = :statut")
    Long countByStatut(@Param("statut") PaiementStatut statut);

    @Query("SELECT SUM(p.montantDebite) FROM Paiement p WHERE p.statutPaiement = :statut")
    BigDecimal sumMontantDebiteByStatut(@Param("statut") PaiementStatut statut);

    @Query("SELECT SUM(p.commission) FROM Paiement p WHERE p.statutPaiement = :statut")
    BigDecimal sumCommissionByStatut(@Param("statut") PaiementStatut statut);
}
