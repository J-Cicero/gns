package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Versement;
import com.backend.gns.domain.enums.VersementStatut;
import com.backend.gns.domain.enums.VersementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface VersementRepository extends JpaRepository<Versement, Long> {

    Optional<Versement> findByTrackingId(UUID trackingId);

    @Query("SELECT v FROM Versement v WHERE v.wallet.trackingId = :walletTrackingId")
    Page<Versement> findByWalletTrackingId(@Param("walletTrackingId") UUID walletTrackingId, Pageable pageable);

    Page<Versement> findByStatut(VersementStatut statut, Pageable pageable);

    Page<Versement> findByTypeVersement(VersementType typeVersement, Pageable pageable);

    Long countByStatut(VersementStatut statut);
}
