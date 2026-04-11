package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Versement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VersementRepository extends JpaRepository<Versement, Long> {

    @Query("SELECT v FROM Versement v WHERE v.trackingId = :trackingId")
    Optional<Versement> findByTrackingId(@Param("trackingId") UUID trackingId);

    @Query("SELECT v FROM Versement v WHERE v.walletTrackingId = :walletTrackingId")
    List<Versement> findByWalletTrackingId(@Param("walletTrackingId") UUID walletTrackingId);

    @Query("SELECT v FROM Versement v WHERE v.statut = :statut")
    List<Versement> findByStatut(@Param("statut") com.backend.gns.domain.enums.VersementStatut statut);

    @Query("SELECT v FROM Versement v WHERE v.typeVersement = :type")
    List<Versement> findByTypeVersement(@Param("type") com.backend.gns.domain.enums.VersementType type);
}
