package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    @Query("SELECT p FROM Paiement p WHERE p.commandeRef = :ref")
    List<Paiement> findByCommandeRef(@Param("ref") String ref);

    @Query("SELECT p FROM Paiement p WHERE p.commande.id = :commandeId")
    List<Paiement> findByCommandeId(@Param("commandeId") Long commandeId);

    @Query("SELECT p FROM Paiement p WHERE p.wallet.id = :walletId")
    List<Paiement> findByWalletId(@Param("walletId") Long walletId);

    @Query("SELECT p FROM Paiement p WHERE p.statutPaiement = :statut")
    List<Paiement> findByStatutPaiement(@Param("statut") com.backend.gns.domain.enums.PaiementStatut statut);

    @Query("SELECT p FROM Paiement p WHERE p.trackingId = :trackingId")
    Optional<Paiement> findByTrackingId(@Param("trackingId") UUID trackingId);

    @Query("SELECT p FROM Paiement p " +
           "WHERE p.wallet.trackingId = :trackingId " +
           "ORDER BY p.dateTimestamp DESC")
    List<Paiement> findByWalletTrackingId(@Param("trackingId") UUID trackingId);

    @Query("SELECT COUNT(p) FROM Paiement p WHERE p.statutPaiement = :statut")
    Long countByStatut(@Param("statut") com.backend.gns.domain.enums.PaiementStatut statut);

    @Query("SELECT SUM(p.montantDebite) FROM Paiement p WHERE p.statutPaiement = :statut")
    Double sumMontantDebiteByStatut(@Param("statut") com.backend.gns.domain.enums.PaiementStatut statut);

    @Query("SELECT SUM(p.commission) FROM Paiement p WHERE p.statutPaiement = :statut")
    Double sumCommissionByStatut(@Param("statut") com.backend.gns.domain.enums.PaiementStatut statut);
}
