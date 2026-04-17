package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Paiement;
import com.backend.gns.domain.enums.PaiementStatut;
import com.backend.gns.domain.enums.PaiementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    Optional<Paiement> findByTrackingId(UUID trackingId);

    Page<Paiement> findByStatutPaiementOrderByDateDesc(PaiementStatut statutPaiement, Pageable pageable);

    Page<Paiement> findByTypePaiementOrderByDateDesc(PaiementType typePaiement, Pageable pageable);

    @Query("SELECT p FROM Paiement p WHERE p.wallet.trackingId = :trackingId ORDER BY p.date DESC")
    Page<Paiement> findByWalletTrackingId(@Param("trackingId") UUID trackingId, Pageable pageable);

    @Query("SELECT p FROM Paiement p WHERE p.commande.trackingId = :trackingId ORDER BY p.date DESC")
    Page<Paiement> findByCommandeTrackingId(@Param("trackingId") UUID trackingId, Pageable pageable);

    Long countByStatutPaiement(PaiementStatut statutPaiement);

    @Query("SELECT SUM(p.montantDebite) FROM Paiement p WHERE p.statutPaiement = :statut")
    BigDecimal sumMontantDebiteByStatut(@Param("statut") PaiementStatut statut);

    @Query("SELECT SUM(p.commission) FROM Paiement p WHERE p.statutPaiement = :statut")
    BigDecimal sumCommissionByStatut(@Param("statut") PaiementStatut statut);
}
